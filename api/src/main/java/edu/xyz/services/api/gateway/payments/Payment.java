package edu.xyz.services.api.gateway.payments;

import java.time.LocalDateTime;

public class Payment {
	
	private String paymentID;
	private String externalReference;
	private String forInvoiceId;
	private String studentId;
	private double amounPaid;
	private LocalDateTime datePaid;
	private String accountNumber;	//Account number
	
	private String comment;
	
	public Payment() {}

	public String getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getForInvoiceId() {
		return forInvoiceId;
	}

	public void setForInvoiceId(String forInvoiceId) {
		this.forInvoiceId = forInvoiceId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public double getAmounPaid() {
		return amounPaid;
	}

	public void setAmounPaid(double amounPaid) {
		this.amounPaid = amounPaid;
	}

	public LocalDateTime getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(LocalDateTime datePaid) {
		this.datePaid = datePaid;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}		
}