package edu.xyz.services.rest.pmt.persistence;



import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "enrolment")
public class PaymentEntity {
	
	@Id
	@Column(name = "PAYMENT_ID")
	private String paymentID;
	
	@Column(name = "EXTERNAL_REFERENCE")
	private String externalReference;
	
	@Column(name = "FOR_INVOICE_ID")
	private String forInvoiceId;
	
	@Column(name = "STUDENT_ID")
	private String studentId;
	
	@Column(name = "ACCOUNT_NUMBER")
	private double amounPaid;
	
	@Column(name = "STATUS")
	private LocalDateTime datePaid;
	
	@Column(name = "WALLET")
	private String accountNumber;	//Account number
	
	private String comment;
	
	public PaymentEntity() {}

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
