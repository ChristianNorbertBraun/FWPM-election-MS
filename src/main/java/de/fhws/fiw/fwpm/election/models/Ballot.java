package de.fhws.fiw.fwpm.election.models;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import de.fhws.fiw.fwpm.election.authentication.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representing the whole election of a single student for a specific election period.
 * It contains all fwpms which were choosen by the student as well as some information about him.
 */
public class Ballot implements ZoneIds{

	private String studentNumber;
	private String firstName;
	private String lastName;
	private int cp;
	private String email;
	private String major;
	private int semester;
	private String facultyName;
	private long periodId;
	private ZonedDateTime lastUpdated;
	private ZonedDateTime created;
	private List<FWPMChoice> choices;
	private int requiredFwpms;

	public Ballot(){
		lastUpdated = ZonedDateTime.now(TIME_ZONE);
		created = ZonedDateTime.now(TIME_ZONE);
		choices = new ArrayList<>();
	}

	public Ballot(User user) {
		this();
		this.studentNumber = user.getCn();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.semester = user.getSemester();
		this.major = user.getDegreeProgram();
		this.facultyName = user.getFacultyName();
		this.email = user.getEmail();
	}

	@DocumentationExample("k12345")
	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	@DocumentationExample("FirstName")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@DocumentationExample("LastName")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getCp() {
		return cp;
	}

	public void setCp(int cp) {
		this.cp = cp;
	}

	@DocumentationExample("test@test.de")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@DocumentationExample("BIN")
	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	@DocumentationExample("6")
	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	@DocumentationExample("FIW")
	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}


	/**
	 * Actually a ZonedDateTime but you can hand in Strings in the Format: <b>dd.MM.yyyy kk:mm</b>
	 */
	@JsonProperty("lastUpdated")
	@com.fasterxml.jackson.annotation.JsonProperty("lastUpdated")
	public String getLastUpdatedAsString() {
		return lastUpdated.format(FORMATTER);
	}

	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public ZonedDateTime getLastUpdated() {
		return lastUpdated;
	}

	@JsonIgnore
	public void setLastUpdated(ZonedDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@JsonProperty("lastUpdated")
	public void setLastUpdatedAsString(String date) {
		LocalDateTime dateTime = LocalDateTime.parse(date, FORMATTER);
		this.lastUpdated = ZonedDateTime.of(dateTime, TIME_ZONE);
	}

	/**
	 * Actually a ZonedDateTime but you can hand in Strings in the Format: <b>dd.MM.yyyy kk:mm</b>
	 */
	@JsonProperty("created")
	@com.fasterxml.jackson.annotation.JsonProperty("created")
	public String getCreatedAsString() {
		return created.format(FORMATTER);
	}


	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	public ZonedDateTime getCreated() {
		return created;
	}

	@JsonIgnore
	public void setCreated(ZonedDateTime created) {
		this.created = created;
	}

	@JsonProperty("created")
	public void setCreatedAsString(String date) {
		LocalDateTime dateTime = LocalDateTime.parse(date, FORMATTER);
		this.created = ZonedDateTime.of(dateTime, TIME_ZONE);
	}

	public List<FWPMChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<FWPMChoice> choices) {
		this.choices = choices;
	}

	public void addChoice(FWPMChoice choice){
		choices.add(choice);
	}

	public int getRequiredFwpms() {
		return requiredFwpms;
	}

	public void setRequiredFwpms(int requiredFwpms) {
		this.requiredFwpms = requiredFwpms;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Ballot ballot = (Ballot) o;

		if (periodId != ballot.periodId) return false;
		if (!studentNumber.equals(ballot.studentNumber)) return false;
		return created.equals(ballot.created);

	}

	@Override
	public int hashCode() {
		int result = studentNumber.hashCode();
		result = 31 * result + (int) (periodId ^ (periodId >>> 32));
		result = 31 * result + created.hashCode();
		return result;
	}

	public static interface Fields{
		String STUDENT_NUMBER = "studentNumber";
		String FIRST_NAME = "firstName";
		String LAST_NAME = "lastName";
		String CP = "cp";
		String EMAIL = "email";
		String MAJOR = "major";
		String SEMESTER = "semester";
		String FACULTY_NAME = "facultyName";
		String PERIOD_ID = "periodId";
		String LAST_UPDATED = "lastUpdated";
		String CREATED = "created";
		String REQUIRED_FWPMS = "requiredFwpms";
	}
}
