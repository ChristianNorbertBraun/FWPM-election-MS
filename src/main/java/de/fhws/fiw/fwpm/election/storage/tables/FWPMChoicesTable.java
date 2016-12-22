package de.fhws.fiw.fwpm.election.storage.tables;

import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.FWPMChoice;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by christianbraun on 18/05/16.
 */
public class FWPMChoicesTable extends AbstractTable implements FWPMChoice.Fields {

	public static interface Fields{
		final String TABLE_NAME = "FWPMChoices";
	}
	@Override
	public String getTableName() {
		return Fields.TABLE_NAME;
	}

	@Override
	protected void createTable(Connection connection) {

		try {
			String query = "CREATE TABLE IF NOT EXISTS " + getTableName() + "(" +
					Ballot.Fields.STUDENT_NUMBER + " varchar(255) NOT NULL, " +
					Ballot.Fields.PERIOD_ID + " bigint unsigned NOT NULL, " +
					FWPM_ID + " bigint unsigned NOT NULL, " +
					PRIORITY + " integer unsigned NOT NULL, " +
					NAME + " varchar(255) NOT NULL, " +
					"PRIMARY KEY (" + Ballot.Fields.STUDENT_NUMBER + ", " + Ballot.Fields.PERIOD_ID + ", " + PRIORITY + "), " +
					"FOREIGN  KEY (" + Ballot.Fields.STUDENT_NUMBER + ") REFERENCES " + BallotTable.Fields.TABLE_NAME +
					"("+ Ballot.Fields.STUDENT_NUMBER + ") ON DELETE  CASCADE, " +
					"CONSTRAINT uc_StudentPeriodIdFWPM " +
					"UNIQUE (" + Ballot.Fields.STUDENT_NUMBER  + ", " + Ballot.Fields.PERIOD_ID + ", " + FWPM_ID + ")" +
					")";
			final Statement statement;
			statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
