package com.bank.services.pmtn.persistence;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_notifications")
public class PaymentNotificationEntity {
	
	@Id
	@Column(name = "NOTIFICATION_ID")
	private String paymentRef;
	
	@Column(name = "CLIENT_ID")
	private String institutionId;
	
	@Column(name = "CLIENT_CALLBACK_URL")
	private String callbackUrl;
	
	@Column(name = "PAYMENT_BODY")
	private String paymentDetails;
	
	private LocalDateTime queuedAt;
	
	private int isSent;

	private LocalDateTime sentAt;
	
	private int isAcknowledged;
	
	private String acknowledgementResponse;
	
	private int sendCount;
	
	private String commentLog;
	
	
	public PaymentNotificationEntity() {}

	public String getPaymentRef() {
		return paymentRef;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public LocalDateTime getQueuedAt() {
		return queuedAt;
	}

	public void setQueuedAt(LocalDateTime queuedAt) {
		this.queuedAt = queuedAt;
	}

	public int isSent() {
		return isSent;
	}

	public void setSent(int isSent) {
		this.isSent = isSent;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	public int isAcknowledged() {
		return isAcknowledged;
	}

	public void setAcknowledged(int isAcknowledged) {
		this.isAcknowledged = isAcknowledged;
	}

	public String getAcknowledgementResponse() {
		return acknowledgementResponse;
	}

	public void setAcknowledgementResponse(String acknowledgementResponse) {
		this.acknowledgementResponse = acknowledgementResponse;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public String getCommentLog() {
		return commentLog;
	}

	public void setCommentLog(String commentLog) {
		this.commentLog = commentLog;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	
}
