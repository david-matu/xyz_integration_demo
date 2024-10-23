package com.bank.services.pmtn.api;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bank.services.api.BankServiceIntegration;
import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotification;
import com.bank.services.api.PaymentNotificationDetails;
import com.bank.services.api.ValidationRequest;
import com.bank.services.api.core.BankClient;
import com.bank.services.api.exceptions.NotFoundException;
import com.bank.services.api.exceptions.UnprocessableRequestException;
import com.bank.services.pmtn.configs.BankClientMapper;
import com.bank.services.pmtn.configs.PaymentNotificationMapper;
import com.bank.services.pmtn.persistence.BankClientEntity;
import com.bank.services.pmtn.persistence.BankClientsRepository;
import com.bank.services.pmtn.persistence.PaymentNotificationEntity;
import com.bank.services.pmtn.persistence.PaymentNotificationRepository;
import com.bank.services.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


@RestController
public class BankServicesIntegrationAPI implements BankServiceIntegration {
	
	private final Logger LOG = LoggerFactory.getLogger(BankServicesIntegrationAPI.class);
	
	private final ObjectMapper mapper;
	
	private final BankClientMapper bankClientMapper;
	private final PaymentNotificationMapper paymentNotificationMapper;
	
	private final WebClient webClient;
	
	// Inject connection pool to handle database requests and not block other threads that are running for other requests
	private final Scheduler jdbcScheduler;
	
	private final PaymentNotificationRepository paymentsRepo;
	private final BankClientsRepository clientsRepo;
	
	@Autowired
	public BankServicesIntegrationAPI(
			@Qualifier("jdbcScheduler") Scheduler publishEventScheduler,
			WebClient.Builder webClient, 
			ObjectMapper mapper, PaymentNotificationRepository paymentsRepo, BankClientsRepository clientsRepo, PaymentNotificationMapper paymentNotificationMapper, BankClientMapper bankClientMapper) {
		
		this.bankClientMapper = bankClientMapper;
		this.paymentNotificationMapper = paymentNotificationMapper;
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
			LOG.info("Found Client: \n ID: {} \n name: {} \n Validation URL: {}", validateRequest.getInstitutionId(), client.get().getInstitutionName(), client.get().getValidationEndpoint());
			LOG.info("Will log Validate Request: \n{}", validateRequest.toString());
			// Send the validation request to the endpoint
			String validateEP = client.get().getValidationEndpoint();
			
			if((validateEP != null) && (validateEP.isBlank() ? false : true)) {
				LOG.debug("Will call validation endpoint on client API on URL: {}", validateEP);
				LOG.info("Calling Student Validation EP at {}", client.get().getValidationEndpoint());
				
				return webClient
						.post()
						.uri(validateEP)
						.bodyValue(validateRequest.getPaymentDetails())
						.retrieve()
						.bodyToMono(GenericProcessingResponse.class)
						.onErrorResume(ex -> {
							return Mono.just(new GenericProcessingResponse("Server Error", "There was an error requesting Student Validation: " + ex.getMessage()));
						})
						.log(LOG.getName(), FINE)
						.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
			} else {
				handleException(WebClientResponseException.create(HttpStatusCode.valueOf(422), "Validation endpoint not available!", HttpHeaders.EMPTY, null, null, null));
			}
		} else {
			throw new NotFoundException("Did not find Institution with id: " + validateRequest.getInstitutionId());
		}
		
		// The line below is unreachable, in other words, dead code
		return Mono.just(new GenericProcessingResponse("UNKNOWN", "Unknown issue occurred while contacting validation endpoint"));
	}
	
	/**
	 * POST
	 * 
	 * 	This resource will take care sending the payment_notification to db and as well send the notification to the client.
	 * 
	 * 	Recapping the narrative:
	 * 	* Check cache for the Bank_Client.CLIENT_ID to reach the payment notification endpoint url
	 * 
	 * 	If there is an issue with DB, just queue the message for save to DB
	 * 	Contact the endpoint as long as we had the notification_endpoint catched from cache
	 * 
	 * 	Will add cache support later, for now, go direct with assumption of db liveness 99.99%
	 * 
	 */
	@Override
	public Mono<GenericProcessingResponse> sendNotification(PaymentNotification body) {
		LOG.info("Fetching bank client by id: {}", body.getInstitutionId());
		
		return Mono.fromCallable(() -> internalGetBankClient(body.getInstitutionId()))
				.flatMap(b -> {
					// Determine if is not null
					
					if (b != null) {
	                   // Perform your action here (fw, etc.)
	                   LOG.info("Found BankClient: {}, to send the payment notification", b.getInstitution());
	                   
	                   return sendPaymentNotificationToExternalEndpoint(body.getPaymentDetails(), b.getPaymentNotificationUrl());
	               } else {
	                   // If null, you could throw an exception or return an alternative value
	                   // throw new NotFoundException("BankClient is null");
	                   return Mono.just(new GenericProcessingResponse("FAILED", "Client not ready for operation"));
	               }
				})
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	/*
	 *  Do the actual sending of the notification to the endpoint
	 *
	 *	We expect the client (xyz pmt notification api) to return a message in the format:
	 *
	 * 	
	 */
	private Mono<GenericProcessingResponse> sendPaymentNotificationToExternalEndpoint(PaymentNotificationDetails paymentDetails, String paymentNotificationUrl) {
		LOG.info("Just before sending Payment Details to the client: " + paymentDetails.toString());
		return webClient
				.post()
				.uri(paymentNotificationUrl)
				.bodyValue(paymentDetails)
				.retrieve()
				.bodyToMono(GenericProcessingResponse.class)
				.map(e -> {
					if(e.getResponseStatus().equalsIgnoreCase("ACCEPTED")) {
						LOG.info("Payment notification has been accepted by the client for Student ID ({})", paymentDetails.getStudentId());
						
						return (new GenericProcessingResponse("ACCEPTED", String.format("Payment notification received by client for Student ID: %s and Payment reference: %s", paymentDetails.getStudentId(), paymentDetails.getPaymentReference())));
					} else {
	                    // Return a default response when not "ACCEPTED"
	                    return new GenericProcessingResponse("FAILED", String.format("Payment notification not received for Student ID: %s and Payment reference: %s", paymentDetails.getStudentId(), paymentDetails.getPaymentReference()));
	                }
				})
				.log(LOG.getName(), FINE)
				.onErrorResume(ex -> {
					return Mono.just(new GenericProcessingResponse("Server Error", "There was an error posting payment notification to client: " + ex.getMessage()));
				})
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
		
		//return new GenericProcessingResponse("ERROR", "Bank Client not ready operation");
	}

	/*
	 * 	Get a list of bank clients.
	 * 	use thread pools (jdbcScheduler)
	 */
	@Override
	public Flux<BankClient> getBankClients() {
		
		return Mono.fromCallable(() -> internalGetBankClients())
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	@Override
	public Flux<PaymentNotification> getExistingPaymentNotifications() {
		return Mono.fromCallable(() -> internalGetPaymentNotifications())
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}
	
	private BankClient internalGetBankClient(String clientId) {
		Optional<BankClientEntity> client = clientsRepo.findByClientId(clientId);
		
		if(client.isPresent()) {
			LOG.info("Found Client for ID: {}, name: {}", clientId, client.get().getInstitutionName());
			String notifEP = client.get().getPaymentNotificationEndpoint();
			if(notifEP != null | !notifEP.isBlank()) {
				return bankClientMapper.entityToApi(client.get());
			}
		} else {
			throw new NotFoundException("Did not find Institution with id: " + clientId);
		}
		
		return null;
	}
	
	// Refactor the following code
	private List<BankClient> internalGetBankClients() {
		List<BankClientEntity> entityList = new ArrayList<>();
		
		clientsRepo.findAll().forEach(entityList::add);
		
		List<BankClient> list = bankClientMapper.entityListToApiList(entityList);
				
		LOG.debug("Response size for Bank Clients: {}", list.size());
		
		return list;
	}
	
	private List<PaymentNotification> internalGetPaymentNotifications() {
		List<PaymentNotificationEntity> entityList = new ArrayList<>();
		
		paymentsRepo.findAll().forEach(entityList::add);
		
		List<PaymentNotification> list = paymentNotificationMapper.entityListToApiList(entityList);
				
		LOG.debug("Response size for Payment Notifications: {}", list.size());
		
		return list;
	}
	
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
				return new UnprocessableRequestException(getErrorMessage(wbex));
				
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
}