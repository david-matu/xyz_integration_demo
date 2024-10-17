package com.bank.services.pmtn.persistence;

import java.time.LocalDateTime;

import com.bank.services.api.PaymentNotificationDetails;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_clients")
public class BankClientEntity {
	
	@Id
	@Column(name = "CLIENT_ID")
	private String clientId;
	
	@Column(name = "INSTITUTION_NAME")
	private String institutionName;
	
	@Column(name = "VALIDATION_ENDPOINT")
	private String validationEndpoint;
	
	@Column(name = "PAYMENT_NOTIFICATION_EP")
	private String paymentNotificationEndpoint;
	
	public BankClientEntity() {}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getValidationEndpoint() {
		return validationEndpoint;
	}

	public void setValidationEndpoint(String validationEndpoint) {
		this.validationEndpoint = validationEndpoint;
	}

	public String getPaymentNotificationEndpoint() {
		return paymentNotificationEndpoint;
	}

	public void setPaymentNotificationEndpoint(String paymentNotificationEndpoint) {
		this.paymentNotificationEndpoint = paymentNotificationEndpoint;
	}
	
	
}
