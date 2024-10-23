package com.bank.services.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationRequest implements Serializable {
	
	/**
	 * 	This key should be transient. It should be brought to send also in the Payment Notification.
	 * 	
	 * 	This will facilitate fetching of information from cache
	 * 
	 */
	@JsonProperty(value = "request_id")
	private String requestId;
	
	@JsonProperty(value = "institution_id")
	private String institutionId;
	
	@JsonProperty(value = "payment_details")
	private ValidationPaymentDetails paymentDetails;

	public ValidationRequest() {}
	
	public ValidationRequest(String requestId, String institutionId, ValidationPaymentDetails details) {
		this.requestId = requestId;
		this.institutionId = institutionId;
		this.paymentDetails = details;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public ValidationPaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(ValidationPaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	@Override
	public String toString() {
		return "ValidationRequest [requestId=" + requestId + ", institutionId=" + institutionId + ", paymentDetails="
				+ paymentDetails.toString() + "]";
	}
}