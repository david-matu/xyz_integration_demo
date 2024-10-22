package edu.xyz.services.pmt.rest.persistence;



import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentEntity {
	
	@Id
	@Column(name = "PAYMENT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long paymentId;
	
	@Column(name = "EXTERNAL_REFERENCE")
	private String externalReference;
	
	@Column(name = "FOR_INVOICE_ID")
	private String forInvoiceId;
	
	@Column(name = "STUDENT_ID")
	private String studentId;
	
	@Column(name = "AMOUNT_PAID")
	private double amounPaid;
	
	@Column(name = "DATE_PAID")
	private Timestamp datePaid;
	// private LocalDateTime datePaid;
	
	@Column(name = "WALLET")
	private String accountNumber;	//Account number
	
	private String comment;
	
	public PaymentEntity() {}

	public long getPaymentID() {
		return paymentId;
	}

	public void setPaymentID(long paymentID) {
		this.paymentId = paymentID;
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

	public Timestamp getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(Timestamp datePaid) {
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
