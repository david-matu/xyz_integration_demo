package edu.xyz.services.pmt.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication(scanBasePackages = { "edu.xyz", "edu.xyz.services.rest.pmt" })
@ComponentScan("edu.xyz.services.rest.pmt")
@OpenAPIDefinition
public class PaymentServiceApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceApplication.class);
	
	private final Integer threadPoolSize;
	private final Integer taskQueueSize;
	

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(PaymentServiceApplication.class, args);
		
		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		LOG.info("Connected to MySQL: " + mysqlUri);
	}
	
	public PaymentServiceApplication(@Value("${app.threadPoolSize:10}") Integer threadPoolSize, @Value("${app.taskQueueSize:100}") Integer taskQueueSize) {
		this.threadPoolSize = threadPoolSize;
		this.taskQueueSize = taskQueueSize;
	}
	
	// This bean will help us run database operations in non-blocking mode using available threads in a thread pool
	@Bean
	public Scheduler jdbcScheduler() {
		LOG.info("Creates a jdbcScheduler with thread pool size = {}", threadPoolSize);
		
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool");
	}
	
	// The following bean provides a platform to send messages asynchrously (to messaging systems / queues)
	@Bean
	public Scheduler publishEventScheduler() {
		LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
	}
}