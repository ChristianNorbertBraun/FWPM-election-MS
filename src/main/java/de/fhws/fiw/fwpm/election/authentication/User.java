package de.fhws.fiw.fwpm.election.authentication;

import com.owlike.genson.annotation.JsonProperty;

import java.security.Principal;

/**
 * Created by christianbraun on 12/07/16.
 */
public class User implements Principal {

	private String token;
	private String firstName;
	private String lastName;
	private String email;
	private String cn;
	private String role;
	private String degreeProgram;
	private int semester;
	private String facultyName;

	@Override
	public String getName() {
		return cn;
	}

	public String getToken(){
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getEmail() {
		return email;
	}

	@JsonProperty("emailAddress")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("studentNumber")
	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDegreeProgram() {
		return degreeProgram;
	}

	public void setDegreeProgram(String degreeProgram) {
		this.degreeProgram = degreeProgram;
	}

	public int getSemester() {
		return semester;
	}


	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}


}
