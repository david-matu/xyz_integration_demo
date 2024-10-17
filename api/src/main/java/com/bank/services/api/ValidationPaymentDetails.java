package com.bank.services.api;

import java.io.Serializable;

/**
 * This is a sub-object that is comes as 'payment_details' like in the example json below:
 * 
{
    "payment_ref": "REF-123-FROM-SYS-INTERNAL-2024",
    "institution_id": "xyz-universisty",
    "payment_details": {
        "student_id": "BE232",
        "account_number": "11223334444"
    }
}
 */
public class ValidationPaymentDetails implements Serializable {
	
	String studentId;
	String accountNumber;
	
	public ValidationPaymentDetails() {}
	
	public ValidationPaymentDetails(String studentId, String accountNumber) {
		this.studentId = studentId;
		this.accountNumber = accountNumber;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
}