package com.bank.services.api.exceptions;

public class UnprocessableRequestException extends RuntimeException {

	public UnprocessableRequestException() {}

	public UnprocessableRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnprocessableRequestException(String message) {
		super(message);
	}

	public UnprocessableRequestException(Throwable cause) {
		super(cause);
	}	
}
