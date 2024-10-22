package edu.xyz.services.api.gateway.payments;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotificationDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Define the core services (API operations) for Payment validations
 */
public interface IPaymentService {
	
	/**
	 * This interface specifies how the REST service will implemented to facilitate consuming messages from queue
	 * 
	*/
	
	/**
	 * 	The Gateway Service API will do a GET request with two parameters: PaymentID and Account Number
	 * 	Will return Payment {} object if valid
	 * 	
	 * 	The Gateway will transform to the format {responseStatus, responseMessage}
	 * 
	 * 	@param studentId
	 * 	@param accountNumber
	 * 	@return
	 */
	@GetMapping(value = "/xyz/payments/{studentId}", produces = "application/json")
	Flux<Payment> getPaymentsByStudentID(@PathVariable String studentId);
	
	@Operation(
			summary = "${api.payments.get-payment.summary}",
			description = "${api.payments.get-payment.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/payments/{paymentRef}", produces = "application/json")
	Mono<Payment> getPayment(@PathVariable long paymentId);
	
	// Will be called after consuming SAVE_TO_DB event
	@Operation(
			summary = "${api.payments.add-payment.summary}",
			description = "${api.payments.add-payment.description}")
	@ApiResponses(value = {	
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@PostMapping(value = "/xyz/payments", consumes = "application/json", produces = "application/json")
	Mono<GenericProcessingResponse> addPayment(@RequestBody PaymentNotificationDetails body);
	
	@Operation(
			summary = "${api.payments.get-payment-all.summary}",
			description = "${api.payments.get-payment-all.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}")	
	})
	@GetMapping(value = "/xyz/payments", produces = "application/json")
	Flux<Payment> getAllPayments();
}