package edu.xyz.services.pmt.rest.config;

import java.util.function.Consumer;

import static edu.xyz.services.api.events.Event.Type.CREATE;
import static edu.xyz.services.api.events.Event.Type.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bank.services.api.PaymentNotificationDetails;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.api.core.enrolment.StudentService;
import edu.xyz.services.api.events.Event;
import edu.xyz.services.api.exceptions.EventProcessingException;
import edu.xyz.services.api.gateway.payments.IPaymentService;
import edu.xyz.services.api.gateway.payments.Payment;

@Configuration
public class MessageProcessorConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);
	
	private final IPaymentService pmtService;

	@Autowired
	public MessageProcessorConfig(IPaymentService pmtService) {
		this.pmtService = pmtService;
	}
	
	@Bean
	public Consumer<Event<String, PaymentNotificationDetails>> messageProcessor() {
		LOG.info("Processing payment notification");
		return event -> {
			LOG.info("Process message created at {}...", event.getEventCreatedAt());
			
			switch (event.getEventType()) {
				case CREATE:
					PaymentNotificationDetails pmt = event.getData();
					
					LOG.info("Create Payment record for Student with ID: {}", pmt.getStudentId());
					pmtService.addPayment(pmt).block();
					break;
					
				default:
					String errorMsg = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event. Other events like UPDATE and DELETE will be available soon!";
					LOG.warn(errorMsg);
					throw new EventProcessingException();
			}
			LOG.info("Message processing done!");
		};
	}
}