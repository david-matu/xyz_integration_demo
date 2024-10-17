package com.bank.services.pmtn.api;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bank.services.api.BankServiceIntegration;
import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotification;
import com.bank.services.api.ValidationRequest;
import com.bank.services.api.core.BankClient;
import com.bank.services.pmtn.persistence.BankClientEntity;
import com.bank.services.pmtn.persistence.BankClientsRepository;
import com.bank.services.pmtn.persistence.PaymentNotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.xyz.services.api.exceptions.InvalidInputException;
import edu.xyz.services.api.exceptions.NotFoundException;
import edu.xyz.services.util.http.HttpErrorInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


@RestController
public class BankServicesIntegration implements BankServiceIntegration {
	
	private final Logger LOG = LoggerFactory.getLogger(BankServicesIntegration.class);
	
	private final ObjectMapper mapper;
	
	private final WebClient webClient;
	
	// Inject connection pool to handle database requests and not block other threads that are running for other requests
	private final Scheduler jdbcScheduler;
	
	private final PaymentNotificationRepository paymentsRepo;
	private final BankClientsRepository clientsRepo;
	
	@Autowired
	public BankServicesIntegration(
			@Qualifier("jdbcScheduler") Scheduler publishEventScheduler,
			WebClient.Builder webClient, 
			ObjectMapper mapper, PaymentNotificationRepository paymentsRepo, BankClientsRepository clientsRepo) {
		
		this.jdbcScheduler = publishEventScheduler;
		this.webClient = webClient.build();
		this.mapper = mapper;
		this.paymentsRepo = paymentsRepo;
		this.clientsRepo = clientsRepo;
	}
	
	/*
	@Override
	public Mono<Student> getStudent(String studentId) {
		String url = studentServiceUrl + "/" + studentId;
		
		LOG.debug("Will call StudentService API on URL: {}", url);
		return webClient.get().uri(url).retrieve().bodyToMono(Student.class)
				.log(LOG.getName(), FINE)
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}

	@Override
	public Mono<Student> addStudent(Student body) {
		
		return Mono.fromCallable(() -> {
			sendMessage("students-out-0", new Event(CREATE, body.getStudentID(), body));
			return body;
		}).subscribeOn(publishEventScheduler);		
	}

	@Override
	public Flux<Student> getStudentList() {
		String url = studentServiceUrl;
		
		return webClient.get().uri(url).retrieve().bodyToFlux(Student.class)
				.log(LOG.getName(), FINE)
				.onErrorResume(error -> empty());
	}
	*/
	
	/**
	 * 
	 * @param bindingName
	 * @param event
	 */
	/* No sending messages at this moment to keep things simple
	 * 
	 *
	private void sendMessage(String bindingName, Event event) {
		LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
		
		Message message = MessageBuilder.withPayload(event)
				.setHeader("partitionKey", event.getKey())
				.build();
		
		streamBridge.send(bindingName, message);
	}
	*/
	
	
	private Throwable handleException(Throwable ex) {
		if(!(ex instanceof WebClientResponseException)) {
			LOG.warn("Got unexpected error: {}, will rethrow it", ex.toString());
			return ex;
		}
		
		WebClientResponseException wbex = (WebClientResponseException)ex;
		
		switch (HttpStatus.resolve(wbex.getStatusCode().value())) {
			case NOT_FOUND:
				return new NotFoundException(getErrorMessage(wbex));
				
			case UNPROCESSABLE_ENTITY:
				return new InvalidInputException(getErrorMessage(wbex));
				
			default:
				LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wbex.getStatusCode());
				LOG.warn("Error body: {}", wbex.getResponseBodyAsString());
				return ex;
		}
	}
	
	// Decode the error thrown by the WebClient. This will be translated to HttpErrorInfo object which is then sent to the client as HTTP_500 response
	private String getErrorMessage(WebClientResponseException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}

	
	/**
	 * 	Fetch the validation url of the institution from db,
	 * 	
	 * 	Store in cache 
	 * 		and 
	 * 	Call the validation url with the payload ValidationRequest
	 * 
	 */
	@Override
	public Mono<GenericProcessingResponse> validateStudent(ValidationRequest validateRequest) {
		// Check first for cache
		Optional<BankClientEntity> client = clientsRepo.findByClientId(validateRequest.getInstitutionId());
		
		if(client.isPresent()) {
			LOG.info("Found Client for ID: {}, name: {}", validateRequest.getInstitutionId(), client.get().getInstitutionName());
			
			// Send the validation request to the endpoint
			String validateEP = client.get().getValidationEndpoint();
			
			if(validateEP != null | !validateEP.isBlank()) {
				LOG.debug("Will call validation endpoint on client API on URL: {}", validateEP);
				
				return webClient.get().uri(validateEP).retrieve().bodyToMono(GenericProcessingResponse.class)
						.log(LOG.getName(), FINE)
						.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
			}
		} else {
			throw new NotFoundException("Not found Institution with id: " + validateRequest.getInstitutionId());
		}
		
		return Mono.just(new GenericProcessingResponse("UNKNOWN", "Unknown issue occurred while contacting validation endpoint"));
	}

	@Override
	public Mono<GenericProcessingResponse> sendNotification(PaymentNotification body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<BankClient> getBankClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<PaymentNotification> getExistingPaymentNotifications() {
		// TODO Auto-generated method stub
		return null;
	}
}