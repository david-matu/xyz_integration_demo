package edu.xyz.services.api.core.enrolment;

/**
 * This is what will be exchanged in the API when validations are requested
 * 
 * The information returned here is minimal as a means of data protection and efficiency
 */
public class Student {
	private String studentID;
	private String firstName;
	private String lastName;
	
	// accountNo and status fields will be handled by the db entity class
	
	private String serviceAddress; // return information (IP) of the instance that has served this request
	
	public Student() {}
	
	public Student(String studentID, String firstName, String lastName) {
		this.studentID = studentID;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
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

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
}