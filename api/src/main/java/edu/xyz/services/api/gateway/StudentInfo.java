package edu.xyz.services.api.gateway;

/**
 * This is the client facing object for api exchanges, 
 * it has data minimized since not all information regarding a student, or information about nodes is relevant to the client 
 * 
 */
public class StudentInfo {
	private String studentID;
	private String firstName;
	private String lastName;	
	
	public StudentInfo() {}
	
	public StudentInfo(String studentID, String firstName, String lastName) {
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
}