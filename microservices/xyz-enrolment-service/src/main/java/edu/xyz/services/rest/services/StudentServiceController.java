package edu.xyz.services.rest.services;

import static java.util.logging.Level.FINE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.api.core.enrolment.StudentService;
import edu.xyz.services.api.exceptions.InvalidInputException;
import edu.xyz.services.api.exceptions.NotFoundException;
import edu.xyz.services.rest.persistence.StudentEntity;
import edu.xyz.services.rest.persistence.StudentRepository;
import edu.xyz.services.util.http.ServiceUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class StudentServiceController implements StudentService {
	private static final Logger LOG = LoggerFactory.getLogger(StudentServiceController.class);
	
	private final ServiceUtil serviceUtil;
	
	private final StudentMapper mapper;
	private final StudentRepository repo;
	
	private final Scheduler jdbcScheduler;
	
	/**
	 * All args constructor
	 * 
	 * @param jdbcScheduler - the thread pool scheduler
	 * @param repo
	 * @param mapper
	 * @param serviceUtil
	 */
	@Autowired
	public StudentServiceController(
			@Qualifier("jdbcScheduler") Scheduler jdbcScheduler, 
			StudentRepository repo, StudentMapper mapper, ServiceUtil serviceUtil, StreamBridge streamBridge) {
		this.jdbcScheduler = jdbcScheduler;
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repo = repo;
	}
	
	/**
	 * This resource will send CREATE event for Student to the messaging system
	 * 
	 */
	@Override
	public Mono<Student> addStudent(Student body) {
		
		// return Mono.fromCallable(() -> internalCreateStudent(body))
		/*
		return Mono.fromCallable(() -> {
			
			sendMessage("students-out-0", new Event(CREATE, body.getStudentID(), body));
			return body;
		}).subscribeOn(publishEventScheduler);
		*/
		
		// LOG.info("Saving student record to database: {}", body.getStudentID());
		
		// StudentEntity entity = mapper.apiToEntity(body);
		// StudentEntity saveEntity = repo.save(entity);
		
		LOG.info("Saved student record to database: {}", body.getStudentID());
		
		return Mono.fromCallable(() -> internalCreateStudent(body))
				.subscribeOn(jdbcScheduler);
	}

	@Override
	public Mono<Student> getStudent(String studentId) {
		LOG.info("Fetching student by id: {}", studentId);
		
		return Mono.fromCallable(() -> internalGetStudent(studentId))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	@Override
	public Flux<Student> getStudentList() {		
		LOG.info("Fetching list of students");
		
		return Mono.fromCallable(() -> internalGetStudentList())
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	

	private List<Student> internalGetStudentList() {
		List<StudentEntity> entityList = repo.findAll();
		
		List<Student> list = mapper.entityListToApiList(entityList);
		
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
		
		LOG.debug("Response size: {}", list.size());
		
		return list;
	}

	private Student internalGetStudent(String studentId) {
		StudentEntity studentEnt = repo.findByStudentID(studentId);
		
		Student apiStudent = mapper.entityToApi(studentEnt);
		
		if(apiStudent != null) {
			apiStudent.setServiceAddress(serviceUtil.getServiceAddress());
		} else {
			throw new NotFoundException("No Student was found for Student ID: " + studentId);
		}
		
		return apiStudent;
	}
	
	private Student internalCreateStudent(Student body) {
		try {
			StudentEntity entity = mapper.apiToEntity(body);
			StudentEntity newEntity = repo.save(entity);
			
			LOG.debug("createStudent: created a student entity: {}", body.getStudentID());
			return mapper.entityToApi(newEntity);
		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, Student Id: " + body.getStudentID() + ", Student Id: " + body.getStudentID());
		}		
	}
	
	/**
	 *	Commenting because this instance is already a message a consumer
	 * 
	 *	@param bindingName
	 * 	@param event
	 *
	private void sendMessage(String bindingName, Event event) {
		LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
		
		Message msg = MessageBuilder.withPayload(event)
				.setHeader("partitionKey", event.getKey())
				.build();
		
		streamBridge.send(bindingName, msg);
	}
	*/
}