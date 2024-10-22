package com.bank.services.api;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class will hold the inner body specified by "payment_details" key like in the following sample json:
{   
    "institution_id": "xyz-universisty",
    "payment_details": {
    	"payment_ref": "REF-123-FROM-SYS-INTERNAL-2024",
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
public class PaymentNotificationDetails {
	
	@JsonProperty(value = "payment_ref")
	String paymentReference;
	
	@JsonProperty(value = "student_id")
	String studentId;
	
	@JsonProperty(value = "first_name")
	String firstName;
	
	@JsonProperty(value = "last_name")
	String lastName;
	
	@JsonProperty(value = "account_number")
	String accountNumber;
	
	@JsonProperty(value = "amount_paid")
	double amountPaid;
	
	String currency;
	
	@JsonProperty(value = "date_paid")
	Timestamp datePaid;
	
	String branch;

	public PaymentNotificationDetails() { }

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Timestamp getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(Timestamp datePaid) {
		this.datePaid = datePaid;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	@Override
	public String toString() {
		return "PaymentNotificationDetails [paymentReference=" + paymentReference + ", studentId=" + studentId
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", accountNumber=" + accountNumber
				+ ", amountPaid=" + amountPaid + ", currency=" + currency + ", datePaid=" + datePaid + ", branch="
				+ branch + "]";
	}
	
}