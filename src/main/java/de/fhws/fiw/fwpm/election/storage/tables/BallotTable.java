package de.fhws.fiw.fwpm.election.storage.tables;

import de.fhws.fiw.fwpm.election.models.Ballot;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by christianbraun on 23/05/16.
 */
public class BallotTable extends AbstractTable implements Ballot.Fields {

	public static interface Fields{
		final String TABLE_NAME = "Ballots";
	}

	@Override
	public String getTableName() {
		return Fields.TABLE_NAME;
	}

	@Override
	protected void createTable(Connection connection) {
		try {
			String query = "CREATE TABLE IF NOT EXISTS " + getTableName() + "(" +
					PERIOD_ID + " bigint unsigned NOT NULL, " +
					STUDENT_NUMBER + " varchar(255) NOT NULL, " +
					LAST_UPDATED + " timestamp NOT NULL, " +
					CREATED + " timestamp NOT NULL, " +
					FIRST_NAME + " VARCHAR (255), " +
					LAST_NAME + " VARCHAR (255), " +
					CP + " integer, " +
					EMAIL + " VARCHAR (255), " +
					MAJOR + " VARCHAR (255), " +
					SEMESTER+ " integer, " +
					FACULTY_NAME + " VARCHAR (255), " +
					REQUIRED_FWPMS + " integer NOT NULL, " +
					"PRIMARY KEY (" + STUDENT_NUMBER + ", " + PERIOD_ID + ")" +
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
