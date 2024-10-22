package edu.xyz.services.rest.persistence;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "enrolment")
public class StudentEntity {
	
	@Id
	@Column(name = "STUDENT_ID")
	private String studentId;
	
	/*
	 * Implement this when dealing with distributed transactions
	@Version
	private int version;
	*/
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;
	
	@Column(name = "STATUS")
	private String status;
	
	public StudentEntity() {}
	
	/**
	 * All args constructor
	 * 
	 * @param studentID
	 * @param firstName
	 * @param lastName
	 * @param accountNumber
	 * @param status
	 */
	public StudentEntity(String studentID, String firstName, String lastName, String accountNumber, String status) {
		this.studentId = studentID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountNumber = accountNumber;
		this.status = status;
	}

	public String getStudentID() {
		return studentId;
	}

	public void setStudentID(String studentID) {
		this.studentId = studentID;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
