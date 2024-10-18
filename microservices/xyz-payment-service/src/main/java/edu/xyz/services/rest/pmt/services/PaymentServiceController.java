package edu.xyz.services.rest.pmt.services;

import static java.util.logging.Level.FINE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import edu.xyz.services.api.exceptions.InvalidInputException;
import edu.xyz.services.api.exceptions.NotFoundException;
import edu.xyz.services.api.gateway.payments.IPaymentService;
import edu.xyz.services.api.gateway.payments.Payment;
import edu.xyz.services.rest.pmt.persistence.PaymentEntity;
import edu.xyz.services.rest.pmt.persistence.PaymentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * This API will basically just do four things:
 * 	* Consume message from topic in mq, 
 *  * Save Payment Notification to DB
 *  * Provide resource to list the API by ID, 
 *  	or 
 *  * as List
 */
@RestController
public class PaymentServiceController implements IPaymentService {
	private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceController.class);
	
	private final PaymentRepository repo;
	
	private final PaymentMapper pmtMapper;
	
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
	public PaymentServiceController(
			@Qualifier("jdbcScheduler") Scheduler jdbcScheduler, 
			PaymentRepository repo, Payment mapper, PaymentMapper pmtMapper) {
		this.repo = repo;
		this.pmtMapper = pmtMapper;
		this.jdbcScheduler = jdbcScheduler;
	}
	
	/**
	 * This resource will send CREATE event for Payment to the messaging system
	 * 
	 */
	@Override
	public Mono<Payment> addPayment(Payment body) {
		LOG.info("Saved student record to database: {}", body.getPaymentID());
		
		return Mono.fromCallable(() -> internalCreatePayment(body))
				.subscribeOn(jdbcScheduler);
	}

	@Override
	public Mono<Payment> getPayment(String paymentId) {
		LOG.info("Fetching Payment by id: {}", paymentId);
		
		return Mono.fromCallable(() -> internalGetPayment(paymentId))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	private List<Payment> internalGetPaymentList() {
		List<PaymentEntity> entityList = repo.findAll();
		
		List<Payment> list = pmtMapper.entityListToApiList(entityList);
		
		
		LOG.debug("Payment List response size: {}", list.size());
		
		return list;
	}

	private Payment internalGetPayment(String paymentId) {
		PaymentEntity studentEnt = repo.findByPaymentID(paymentId);
		
		Payment apiPayment = pmtMapper.entityToApi(studentEnt);
		
		if(apiPayment != null) {
			return apiPayment;
		} else {
			throw new NotFoundException("No Payment was found for Payment ID: " + paymentId);
		}
	}
	
	
	private Payment internalCreatePayment(Payment body) {
		try {
			PaymentEntity entity = pmtMapper.apiToEntity(body);
			PaymentEntity newEntity = repo.save(entity);
			
			LOG.debug("createPayment: created a Payment notification record: {}", body.getPaymentID());
			return pmtMapper.entityToApi(newEntity);
		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, Payment Id: " + body.getPaymentID() + ", Payment Id: " + body.getPaymentID());
		}		
	}

	@Override
	public Flux<Payment> getAllPayment() {
		LOG.info("Fetching list of payments");
		
		return Mono.fromCallable(() -> internalGetPaymentList())
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}

	@Override
	public Flux<Payment> getPaymentsByStudentID(String studentId) {
		LOG.info("Fetching Payments for Student with id: {} \nThis feature is under development", studentId);
		
		/*
		return Mono.fromCallable(() -> internalGetPayment(paymentId))
				.flatMap(e -> e.getAccountNumber())
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
				*/
		return Flux.empty();
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