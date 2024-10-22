package edu.xyz.services.rest.services;

import static java.util.logging.Level.FINE;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.ValidationPaymentDetails;

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
	public Mono<GenericProcessingResponse> getValidStudent(ValidationPaymentDetails details) {
		LOG.info("Checking validity of Student by id: {} and account number: {}", details.getStudentId(), details.getAccountNumber());
		
		// internalGetStudentValidity(details.getStudentId(), details.getAccountNumber())
		return Mono.fromCallable(() -> internalGetStudent(details.getStudentId()))
				.map(stud -> {
					// DEtermine the enrolment status of the student and respond accordingly
					LOG.info("Validating Student enrolment status: {}", stud.getStatus());
					
					// Besides returning enrolment status, Should we tell the client when the account number is valid or invalid?
					
					if(stud.getStatus().equalsIgnoreCase("ENROLLED")) {
						if(stud.getAccountNumber() == details.getAccountNumber()) {
							return new GenericProcessingResponse("ENROLLED", "Student " + stud.getFirstName() + " with id " + stud.getStudentID() + " is valid"); // Too much information, but for proof-of-concept purpose
						} else {
							return new GenericProcessingResponse("ENROLLED_INVALID_ACCOUNT_NUMBER", "Student (" + stud.getFirstName() + ") with id " + stud.getStudentID() + " is not eligible for specified account (" + details.getAccountNumber() + ")");
						}
					} else {
						return new GenericProcessingResponse("NOT_ELIGIBLE", "Student not eligible for tuition payments");
					}
				})
				.log(LOG.getName(), FINE)
				.onErrorResume(e -> {
					return Mono.just(new GenericProcessingResponse("FAILED", "An error occurred validating Student enrolment: " + e.getMessage()));
				})
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
		Optional<StudentEntity> studentEnt = repo.findByStudentId(studentId);
		
		if(studentEnt.isPresent()) {
			Student apiStudent = mapper.entityToApi(studentEnt.get());
			apiStudent.setServiceAddress(serviceUtil.getServiceAddress());
			
			return apiStudent;
		} else {
			throw new NotFoundException("No Student was found for Student ID: " + studentId);
		}
	}
	
	// De-commision this method in favour of the next 
	private Student internalGetStudent(String studentId, String accountNumber) {
		Optional<StudentEntity> studentEnt = repo.findByStudentId(studentId);
		
		if(studentEnt.isPresent()) {
			Student apiStudent = mapper.entityToApi(studentEnt.get());
			apiStudent.setServiceAddress(serviceUtil.getServiceAddress());
			
			return apiStudent;
		} else {
			throw new NotFoundException("No Student was found for Student ID: " + studentId);
		}
		
	}
	
	private Student internalGetStudentValidity(String studentId, String accountNumber) {
		Optional<StudentEntity> studentEnt = repo.findByStudentIdAndAccountNumber(studentId, accountNumber);
		
		if(studentEnt.isPresent()) {
			Student apiStudent = mapper.entityToApi(studentEnt.get());
			apiStudent.setServiceAddress(serviceUtil.getServiceAddress());
			
			return apiStudent;
		} else {
			throw new NotFoundException("No Student was found for Student ID: " + studentId);
		}
		
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