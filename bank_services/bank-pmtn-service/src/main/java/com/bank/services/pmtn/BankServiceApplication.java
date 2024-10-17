package com.bank.services.pmtn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@ComponentScan("com.bank.services")
@OpenAPIDefinition
public class BankServiceApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(BankServiceApplication.class);
		
	private final Integer threadPoolSize;
	private final Integer taskQueueSize;
	
	public BankServiceApplication(@Value("${app.threadPoolSize:10}") Integer threadPoolSize, @Value("${app.taskQueueSize:100}") Integer taskQueueSize) {
		this.threadPoolSize = threadPoolSize;
		this.taskQueueSize = taskQueueSize;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BankServiceApplication.class, args);
	}
	
	/*
	@Bean
	public Scheduler publishEventScheduler() {
		LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
	}
	*/
	
	
	@Bean
	public Scheduler jdbcScheduler() {
		LOG.info("Creates a jdbcScheduler with thread pool size = {}", threadPoolSize);
		
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool");
	}
}
