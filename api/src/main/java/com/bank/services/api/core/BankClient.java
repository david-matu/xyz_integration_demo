package com.bank.services.api.core;

/**
 * Container to store bank client information
 * 
 * This is somehow a direct return of db structure to the api
 * 
 */
public class BankClient {
	
	private String clientID;
	private String institution;
	private String validationUrl;
	private String paymentNotificationUrl;
	
	public BankClient() {}
	
	public BankClient(String clientID, String institution, String validationEP, String notificationEP) {
		this.clientID = clientID;
		this.institution = institution;
		this.validationUrl = validationEP;
		this.paymentNotificationUrl = notificationEP;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getValidationUrl() {
		return validationUrl;
	}

	public void setValidationUrl(String validationUrl) {
		this.validationUrl = validationUrl;
	}

	public String getPaymentNotificationUrl() {
		return paymentNotificationUrl;
	}

	public void setPaymentNotificationUrl(String paymentNotificationUrl) {
		this.paymentNotificationUrl = paymentNotificationUrl;
	}
}
