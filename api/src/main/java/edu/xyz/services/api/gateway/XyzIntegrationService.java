package edu.xyz.services.api.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bank.services.api.GenericProcessingResponse;
import com.bank.services.api.PaymentNotificationDetails;
import com.bank.services.api.ValidationPaymentDetails;

import edu.xyz.services.api.gateway.payments.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "GatewayAPI", description = "REST API for XYZ University services: Enrolment and Payment")
public interface XyzIntegrationService {
	
	@Operation(
			summary = "${api.enrolment.get-student.summary}",
			description = "${api.enrolment.get-student.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/students/{studentId}", produces = "application/json")
	Mono<StudentInfo> getStudentInfo(@PathVariable String studentId);
	
	// For administrative or internal operations, we can define the CREATE and DELETE, UPDATE operations on StudentInfo records
	@Operation(summary = "${api.enrolment.add-student.summary}", description = "${api.enrolment.add-student.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@PostMapping(value = "/xyz/students", consumes = "application/json", produces = "application/json")
	Mono<StudentInfo> addStudentInfo(@RequestBody StudentInfo body);
	
	@Operation(summary = "${api.enrolment.get-student-all.summary}", description = "${api.enrolment.get-student-all.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/students", produces = "application/json")
	Flux<StudentInfo> getStudentInfoList();
	
	/**
	 * 
	 * 	Add new APis
	 * 	(Reuse the classes in the lib project since we are running all them in one workspace)
	 * 
	 * 	Remember the API expects the subset of Validation Request which is Payment Details section
	 */
	@Operation(
			summary = "${api.enrolment.validations.summary}",
			description = "${api.enrolment.validations.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}")
	})
	@PostMapping(value = "/xyz/students/validate", produces = "application/json")
	Mono<GenericProcessingResponse> validateStudent(@RequestBody ValidationPaymentDetails body);
	
	/**
	 * This resources expects only the subject of payment notification since some details were specific to the originating channel
	 * 
	 * @param body
	 * @return
	 */
	@Operation(
			summary = "${api.payment-service.post-payment-notification.summary}",
			description = "${api.payment-service.post-payment-notification.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}")
	})
	@PostMapping(value = "/xyz/payment-notifications", produces = "application/json")
	Mono<GenericProcessingResponse> receivePaymentNotification(@RequestBody PaymentNotificationDetails body);
	
	@Operation(summary = "${api.payments.get-payment-all.summary}", description = "${api.payments.get-payment-all.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/payments", produces = "application/json")
	Flux<Payment> getPayments();
	
	@Operation(
			summary = "${api.payments.get-payment-by-id.summary}",
			description = "${api.payments.get-payment-by-id.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/payments/{paymentId}", produces = "application/json")
	Mono<Payment> getPaymentByPaymentId(@PathVariable String paymentId);
	
	@Operation(
			summary = "${api.payments.get-payment-by-external-ref.summary}",
			description = "${api.payments.get-payment-by-external-ref.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/payments/external-ref/{externalReference}", produces = "application/json")
	Mono<Payment> getPaymentByExternalRef(@PathVariable String externalReference);
	
	@Operation(summary = "${api.payments.get-payments-by-studentId.summary}", description = "${api.payments.get-payments-by-studentId.description}")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
		@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
		@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	@GetMapping(value = "/xyz/payments/student/{studentId}", produces = "application/json")
	Flux<Payment> getPaymentsByStudentId(@PathVariable String studentId);
}
