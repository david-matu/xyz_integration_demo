package com.bank.services.api;

public class GenericProcessingResponse {
	
	private String responseStatus;	// can be RECEIEVED, ENROLLED, QUEUED 
	private String responseMessage; // Any accompanying text (optional)
	
	public GenericProcessingResponse() {}
	
	public GenericProcessingResponse(String responseStatus, String message) {
		this.responseStatus = responseStatus;
		this.responseMessage = message;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
