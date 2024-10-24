package edu.xyz.services.gateway.api;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;
import static edu.xyz.services.api.events.Event.Type.CREATE;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotificationDetails;
import com.bank.services.api.ValidationPaymentDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.xyz.services.api.core.enrolment.Student;
import edu.xyz.services.api.core.enrolment.StudentService;
import edu.xyz.services.api.events.Event;
import edu.xyz.services.api.exceptions.InvalidInputException;
import edu.xyz.services.api.exceptions.NotFoundException;
import edu.xyz.services.api.gateway.payments.IPaymentService;
import edu.xyz.services.api.gateway.payments.Payment;
import edu.xyz.services.util.http.HttpErrorInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class Xyz_ServicesIntegration implements StudentService , IPaymentService {
	
	private final Logger LOG = LoggerFactory.getLogger(Xyz_ServicesIntegration.class);
	
	private final ObjectMapper mapper;
	
	private final String studentServiceUrl;
	
	private final String paymentServiceUrl;
	
	private final WebClient webClient;
	
	// Define a stream bridge that will send messages out to the messaging systems
	private final StreamBridge streamBridge;
	private final Scheduler publishEventScheduler;
	
	@Autowired
	public Xyz_ServicesIntegration(
			@Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
			WebClient.Builder webClient, 
			ObjectMapper mapper,
			StreamBridge streamBridge,
			@Value("${app.student-service.host}") 
			String studentServiceHost, 
			
			@Value("${app.student-service.port}") 
			String studentServicePort,
			
			@Value("${app.payment-service.host}") 
			String paymentServiceHost, 
			
			@Value("${app.payment-service.port}") 
			String paymentServicePort) {
		
		this.publishEventScheduler = publishEventScheduler;
		this.webClient = webClient.build();
		this.mapper = mapper;
		this.streamBridge = streamBridge;
		
		this.studentServiceUrl = "http://" + studentServiceHost + ":" + studentServicePort + "/students";
		this.paymentServiceUrl = "http://" + paymentServiceHost + ":" + paymentServicePort + "/xyz/payments";
		
		LOG.info("Resolved Student service url: " + studentServiceUrl);
		LOG.info("Resolved Payment service url: " + paymentServiceUrl);
	}
	
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
	
	@Override
	public Mono<GenericProcessingResponse> getValidStudent(ValidationPaymentDetails details) {
		
		String url = studentServiceUrl + "/validate";
		LOG.debug("Will call StudentService Validation API on URL: {}", url);
		
		return webClient
				.post()
				.uri(url)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(details)
				.retrieve()
				.bodyToMono(GenericProcessingResponse.class)
				.onErrorResume(ex -> {
					return Mono.just(new GenericProcessingResponse("Server Error", "There was an error serving Student Validation request: " + ex.getMessage()));
				})
				.log(LOG.getName(), FINE)
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}
	
	@Override
	public Flux<Payment> getPaymentsByStudentID(String studentId) {
		String url = paymentServiceUrl + "/student/" + studentId;
		
		return webClient.get().uri(url).retrieve().bodyToFlux(Payment.class)
				.log(LOG.getName(), FINE)
				.onErrorResume(error -> empty());
	}

	@Override
	public Mono<Payment> getPayment(long paymentId) {
		String url = paymentServiceUrl + "/" + paymentId;
		
		return webClient.get().uri(url).retrieve().bodyToMono(Payment.class)
				.log(LOG.getName(), FINE)
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}

	// Push message to queue
	@Override
	public Mono<GenericProcessingResponse> addPayment(PaymentNotificationDetails body) {
		
		LOG.info("Received payment notification, proceeding to queue. \n{}", body.toString());
		
		return Mono.fromCallable(() -> {
			sendMessage("payments-out-0", new Event(CREATE, body.getPaymentReference(), body));
			
			return new GenericProcessingResponse("ACCEPTED", "Message accepted for processing");
		}).subscribeOn(publishEventScheduler);
	}

	@Override
	public Flux<Payment> getAllPayments() {
		String url = paymentServiceUrl;
		
		return webClient.get().uri(url).retrieve().bodyToFlux(Payment.class)
				.log(LOG.getName(), FINE)
				.onErrorResume(error -> empty());
	}
	
	@Override
	public Mono<Payment> getPaymentByExternalRef(String externalRef) {
		String url = paymentServiceUrl + "/external-ref/" + externalRef;
		
		return webClient.get().uri(url).retrieve().bodyToMono(Payment.class)
				.log(LOG.getName(), FINE)
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}
	
	/**
	 * 
	 * @param bindingName
	 * @param event
	 */
	private void sendMessage(String bindingName, Event event) {
		LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
		
		LOG.info("Sending a {} message to {}", event.getEventType(), bindingName);
		
		Message message = MessageBuilder.withPayload(event)
				.setHeader("partitionKey", event.getKey())
				.build();
		
		streamBridge.send(bindingName, message);
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

}