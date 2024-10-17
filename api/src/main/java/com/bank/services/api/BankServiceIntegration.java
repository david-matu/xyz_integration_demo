package com.bank.services.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bank.services.api.core.BankClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 	This interface specifies how the REST controller will handle requests for:
 * 	* Student Validation requests
 * 	* Payment Notification details
 * 
 * 	Notes:
 * 	* Student Validation
 * 	The API will fetch details about the client specified in the "institution_id"
 * 	Append the details in the ValidationRequest since it's a separate info located in the db
 * 	Send the new ValidationRequest to the client at ValidationRequest.VALIDATION_ENDPOINT
 * 	The response will be converted to ValidationResponse and store in cache against the key: request_id
 * 	Send the ValidationResponse to the client
 * 
 *  Process the acknowledgement and store that in cache against the payment_notification.ACKNOWLEDGED
 *  
 *  The client will choose to send the payment notification based on:
 *  // ValidationResponse.status. If status == "ENROLLED", proceed to send the payment notification
 *  
 *  * Payment Notification
 *  The API will check cache for bank_clients.notification_endpoint against the institution_id
 *  
 *  
 *  Other Resources:
 *  * GET/POST: Bank_Clients
 *  For listing and creating new bank clients
 *  
 *  * GET/POST/PUT/PATCH: Payment_Notifications
 *  For creating new instances of payment notifications, listing and updating of the same in full (PUT) or in part (PATCH) 
 * 	
 * 	The API will use non-blocking API for executing requests. This is an improvement in performance
 */
@Tag(name = "Bank Service API", description = "REST API that will receive Validation and Payment Notification requests")
public interface BankServiceIntegration {
	
	@Operation(
			summary = "${api.bank_clients.validate-student.summary}",
			description = "${api.bank_clients.validate-student.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@PostMapping(value = "/bank/integration/validate-student", produces = "application/json")
	Mono<GenericProcessingResponse> validateStudent(@RequestBody ValidationRequest validateRequest);
	
	@Operation(summary = "${api.bank_clients.send_payment_notificaiton.summary}", description = "${api.bank_clients.send_payment_notificaiton.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@PostMapping(value = "/bank/integration/send-payment-notification", consumes = "application/json", produces = "application/json")
	Mono<GenericProcessingResponse> sendNotification(@RequestBody PaymentNotification body);
	
	@Operation(summary = "${api.bank_clients.listing.summary}", description = "${api.bank_clients.listing.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/bank/integration/bank-clients", produces = "application/json")
	Flux<BankClient> getBankClients();
	
	
	@Operation(summary = "${api.bank_clients.payment_notifications.summary}", description = "${api.bank_clients.payment_notifications.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/bank/integration/payment-notifications", produces = "application/json")
	Flux<PaymentNotification> getExistingPaymentNotifications();
}
