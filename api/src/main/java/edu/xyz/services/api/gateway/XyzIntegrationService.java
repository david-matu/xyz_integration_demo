package edu.xyz.services.api.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
