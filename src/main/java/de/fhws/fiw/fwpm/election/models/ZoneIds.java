package de.fhws.fiw.fwpm.election.models;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by christianbraun on 16/08/16.
 */
public interface ZoneIds {
	ZoneId TIME_ZONE = ZoneId.of("Europe/Berlin");
	DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
}
