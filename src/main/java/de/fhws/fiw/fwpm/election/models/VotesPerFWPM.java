package de.fhws.fiw.fwpm.election.models;

/**
 * Representing the number of votes for a specific fwpm and periodId.
 */
public class VotesPerFWPM {
	private String name;
	private long fwpmId;
	private long periodId;
	private int votes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFwpmId() {
		return fwpmId;
	}

	public void setFwpmId(long fwpmId) {
		this.fwpmId = fwpmId;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
}
