package edu.xyz.services.api.core.enrolment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Define the core services (API operations) for Student validations
 */
public interface StudentService {
	
	/**
	 * Define how the Open API documentation will be fetched from a config file
	*/
	
	/*
	 * @param studentId
	 * @return
	 */
	/*
	@Operation(
			summary = "${api.enrolment.get-student.summary}",
			description = "${api.enrolment.get-student.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	})
	*/
	
	/**
	 * 	The Gateway Service API will do a GET request with two parameters: StudentID and Account Number
	 * 	Will return Student {} object if valid
	 * 	
	 * 	The Gateway will transform to the format {responseStatus, responseMessage}
	 * 
	 * 	@param studentId
	 * 	@param accountNumber
	 * 	@return
	 */
	@GetMapping(value = "/students/{studentId}/{account_number}", produces = "application/json")
	Mono<Student> getValidStudent(@PathVariable String studentId, @PathVariable String accountNumber);
	
	@GetMapping(value = "/students/{studentId}", produces = "application/json")
	Mono<Student> getStudent(@PathVariable String studentId);
	
	// For administrative or internal operations, we can define the CREATE and DELETE, UPDATE operations on Student records
	@PostMapping(value = "/students", consumes = "application/json", produces = "application/json")
	Mono<Student> addStudent(@RequestBody Student body);
	
	@GetMapping(value = "/students", produces = "application/json")
	Flux<Student> getStudentList();
}