package edu.xyz.services.pmt.rest.services;

import static java.util.logging.Level.FINE;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotificationDetails;

import edu.xyz.services.api.exceptions.InvalidInputException;
import edu.xyz.services.api.exceptions.NotFoundException;
import edu.xyz.services.api.gateway.payments.IPaymentService;
import edu.xyz.services.api.gateway.payments.Payment;
import edu.xyz.services.pmt.rest.persistence.PaymentEntity;
import edu.xyz.services.pmt.rest.persistence.PaymentRepository;
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
			PaymentRepository repo, PaymentMapper pmtMapper) {
		this.repo = repo;
		this.pmtMapper = pmtMapper;
		this.jdbcScheduler = jdbcScheduler;
	}
	
	/**
	 * This resource will send CREATE event for Payment to the messaging system
	 * 
	 *
	@Override
	public Mono<Payment> addPayment(Payment body) {
		LOG.info("Saved student record to database: {}", body.getPaymentID());
		
		return Mono.fromCallable(() -> internalCreatePayment(body))
				.subscribeOn(jdbcScheduler);
	}
	*/

	@Override
	public Mono<Payment> getPayment(long paymentId) {
		LOG.info("Fetching Payment by id: {}", paymentId);
		
		return Mono.fromCallable(() -> internalGetPayment(paymentId))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	/*
	@Override
	public Flux<Payment> getAllPayments() {
		LOG.info("Fetching list of payments");
		
		return Mono.fromCallable(() -> internalGetPaymentList())
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	*/
	
	@Override
	public Flux<Payment> getPaymentsByStudentID(String studentId) {
		LOG.info("Fetching Payments for Student with id: {} \nThis feature is now complete", studentId);
		
		return Mono.fromCallable(() -> internalGetPaymentListByStudentId(studentId))
				.flatMapMany(stud -> Flux.fromIterable(stud))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	@Override
	public Flux<Payment> getAllPayments() {
		return Mono.fromCallable(() -> internalGetPaymentList())
				.flatMapMany(stud -> Flux.fromIterable(stud))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}

	@Override
	public Mono<Payment> getPaymentByExternalRef(String externalRef) {
		LOG.info("Fetching Payment by externalReference: {}", externalRef);
		
		return Mono.fromCallable(() -> internalGetPaymentByExternalReference(externalRef))
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	

	/**
	 * 	Overriding this method, will scrap it out later because the intention to save the notification is fulfilled by the payment notification consumer
	 * 
	 * 	This problem is proudly brought to you by share contracts: IPaymentService, which is borrowed by the Gateway service 
	 * 	to ensure all operations leading to Payment Service (here) are met
	 * 
	 */
	@Override
	public Mono<GenericProcessingResponse> addPayment(PaymentNotificationDetails body) {
		LOG.info("Save Payment Notification: {}", body.getPaymentReference());
		
		return Mono.fromCallable(() -> internalCreatePayment(body))
				.map(e -> {
					LOG.info("Saved Payment Notification for: " + e.getFirstName());
					if(e.getPaymentReference() != null) {
						return new GenericProcessingResponse("ACCEPTED", "Back channel: notification posting accepted");
					}
					return new GenericProcessingResponse("ERROR", "Back channel: notification can't be posted because payment ref is void");
				})
				.doOnError(ex -> {
					LOG.info("There was an error in saving Payment notification: " + ex.getMessage());
					//return Mono.just(new GenericProcessingResponse("ERROR", "Back channel: notification can't be posted because payment ref is void"));
				})
				.subscribeOn(jdbcScheduler);
	}
	
	private List<Payment> internalGetPaymentList() {
		List<PaymentEntity> entityList = repo.findAll();
		
		List<Payment> list = pmtMapper.entityListToApiList(entityList);
		
		
		LOG.debug("Payment List response size: {}", list.size());
		
		return list;
	}
	
	private List<Payment> internalGetPaymentListByStudentId(String studentId) {
		List<PaymentEntity> entityList = repo.findByStudentId(studentId);
		
		List<Payment> list = pmtMapper.entityListToApiList(entityList);
		
		
		LOG.debug("Payment List response size: {}", list.size());
		
		return list;
	}
	
	private Payment internalGetPayment(long paymentId) {
		Optional<PaymentEntity> studentEnt = repo.findByPaymentId(paymentId);
		
		if(studentEnt.isPresent()) {
			// Payment apiPayment = pmtMapper.entityToApi(studentEnt.get());
			
			return pmtMapper.entityToApi(studentEnt.get());
		}
		throw new NotFoundException("No Payment was found for Payment ID: " + paymentId);
	}
	
	private Payment internalGetPaymentByExternalReference(String externalReference) {
		Optional<PaymentEntity> studentEnt = repo.findByExternalReference(externalReference);
		
		if(studentEnt.isPresent()) {
			// Payment apiPayment = pmtMapper.entityToApi(studentEnt.get());
			
			return pmtMapper.entityToApi(studentEnt.get());
		}
		throw new NotFoundException("No Payment was found for External Reference: " + externalReference);
	}
	
	private PaymentNotificationDetails internalCreatePayment(PaymentNotificationDetails body) {
		try {
			PaymentEntity entity = pmtMapper.notifDetailsToEntity(body); //.apiToEntity(body);
			PaymentEntity newEntity = repo.save(entity);
			
			LOG.info("createPaymentNotification: created a Payment notification record: {}", body.getPaymentReference());
			//return pmtMapper.entityToApi(newEntity);
			return pmtMapper.entityToNotifDetails(newEntity);
		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, Payment Id: " + body.getPaymentReference() + ", Payment Id: " + body.getPaymentReference());
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