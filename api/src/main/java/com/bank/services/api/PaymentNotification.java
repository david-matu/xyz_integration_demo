package com.bank.services.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class houses the payment notification that looks like this:
{
    "payment_ref": "REF-123-FROM-SYS-INTERNAL-2024",
    "institution_id": "xyz-universisty",
    "payment_details": {
        "student_id": "BE232",
        "first_name": "David",
        "last_name": "Matu",
        "account_number": "11223334444",
        "amount_paid": "56024.0",
        "currency": "KES",
        "date_paid": "2024-10-17",
        "branch": "02"
    }
}
 */
public class PaymentNotification {
	
	@JsonProperty(value = "payment_ref")
	private String paymentRef;
	
	@JsonProperty(value = "institution_id")
	private String institutionId;
	
	@JsonProperty(value = "callback_url")
	private String callbackUrl;
	
	@JsonProperty(value = "payment_details")
	private PaymentNotificationDetails paymentDetails;

	public PaymentNotification() {}
	
	public PaymentNotification(String paymentRef, String institutionId, PaymentNotificationDetails details, String callbackUrl) {
		this.paymentRef = paymentRef;
		this.institutionId = institutionId;
		this.paymentDetails = details;
		this.callbackUrl = callbackUrl;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public PaymentNotificationDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentNotificationDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
}
