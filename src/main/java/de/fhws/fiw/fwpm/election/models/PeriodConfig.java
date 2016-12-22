package de.fhws.fiw.fwpm.election.models;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Created by christianbraun on 10/08/16.
 */
public class PeriodConfig implements ZoneIds{

	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	private String[] fwpms;
	private long periodId;

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = ZonedDateTime.of(LocalDateTime.parse(startDate, FORMATTER), TIME_ZONE);
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = ZonedDateTime.of(LocalDateTime.parse(endDate, FORMATTER), TIME_ZONE);
	}

	public String[] getFwpms() {
		return fwpms;
	}

	public void setFwpms(String[] fwpms) {
		this.fwpms = fwpms;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public boolean isOver() {
		return this.endDate.isBefore(ZonedDateTime.now(TIME_ZONE));
	}

	public boolean isStarted() {
		return this.startDate.isBefore(ZonedDateTime.now(TIME_ZONE));
	}

	public boolean isValid() {
		return isStarted() && !isOver();
	}
}
