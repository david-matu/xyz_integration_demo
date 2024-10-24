package edu.xyz.services.api.exceptions;

public class InvalidInputException extends RuntimeException {

	public InvalidInputException() {}

	/*
	public InvalidInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	*/

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	}	
}
