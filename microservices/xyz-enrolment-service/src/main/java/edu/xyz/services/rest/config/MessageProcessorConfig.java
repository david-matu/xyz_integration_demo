package edu.xyz.services.rest.config;

import java.util.function.Consumer;

import static edu.xyz.services.api.events.Event.Type.CREATE;
import static edu.xyz.services.api.events.Event.Type.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.api.core.enrolment.StudentService;
import edu.xyz.services.api.events.Event;
import edu.xyz.services.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);
	
	private final StudentService studentService;

	@Autowired
	public MessageProcessorConfig(StudentService studentService) {
		this.studentService = studentService;
	}
	
	@Bean
	public Consumer<Event<String, Student>> messageProcessor() {
		return event -> {
			LOG.info("Process message created at {}...", event.getEventCreatedAt());
			
			switch (event.getEventType()) {
				case CREATE:
					Student student = event.getData();
					
					LOG.info("Create student record for Student with ID: {}", student.getStudentID());
					studentService.addStudent(student).block();
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