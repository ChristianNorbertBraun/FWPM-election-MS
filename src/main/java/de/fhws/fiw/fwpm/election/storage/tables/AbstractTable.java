package de.fhws.fiw.fwpm.election.storage.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by christianbraun on 18/05/16.
 */
public abstract class AbstractTable {

	public abstract String getTableName();

	public final void initTable(boolean delete, Connection connection) throws SQLException {
		if (delete) {
			deleteTable(connection);
		}
		createTable(connection);
	}

	protected abstract void createTable(Connection connection);

	protected final void deleteTable(Connection connection) throws SQLException {
		String query = String.format("DROP TABLE IF EXISTS %s;", getTableName());
		final Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.close();
	}
}
