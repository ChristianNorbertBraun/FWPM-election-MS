package de.fhws.fiw.fwpm.election.models;

import com.webcohesion.enunciate.metadata.DocumentationExample;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Representing a single choice of a student for an election period.
 */
public class FWPMChoice {
	private long fwpmId;
	@Min(1) @Max(4)
	private int priority;
	private String name;

	public long getFwpmId() {
		return fwpmId;
	}

	public void setFwpmId(long fwpmId) {
		this.fwpmId = fwpmId;
	}

	/**
	 * Has to be a number between 1 and 4. Every priority can only occur once
	 * for a ballot.
	 */
	@DocumentationExample("1")
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@DocumentationExample("Microservices")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static interface Fields{
		String FWPM_ID = "fwpmId";
		String PRIORITY = "priority";
		String NAME = "name";
	}
}
