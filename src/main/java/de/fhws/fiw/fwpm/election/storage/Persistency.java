package de.fhws.fiw.fwpm.election.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.fhws.fiw.fwpm.election.storage.tables.AbstractTable;
import de.fhws.fiw.fwpm.election.storage.tables.BallotTable;
import de.fhws.fiw.fwpm.election.storage.tables.FWPMChoicesTable;
import de.fhws.fiw.fwpm.election.utils.PropertySingleton;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by christianbraun on 18/05/16.
 */
public class Persistency {

	private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_PORT = "3306";
	private static String DATABASE_HOST;
	private static String DATABASE_NAME;

	private static String USERNAME;
	private static String PASSWORD;

	private static Persistency instance;

	private ComboPooledDataSource cpds;

	private Persistency(boolean deleteDatabase) throws IOException {
		loadProperties();
		createConnectionPool();
		createAllTables(deleteDatabase);
	}

	public static Persistency getInstance(boolean deleteDatabase) throws IOException {
		if (instance == null) {
			instance = new Persistency(deleteDatabase);
		}
		return instance;
	}

	public final Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	protected void createConnectionPool() {
		try {
			Class.forName(COM_MYSQL_JDBC_DRIVER);
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(COM_MYSQL_JDBC_DRIVER);
			cpds.setJdbcUrl("jdbc:mysql://" + DATABASE_HOST + ":" + DATABASE_PORT + "/" + DATABASE_NAME
					+ "?autoReconnect=true&useSSL=false");
			cpds.setUser(USERNAME);
			cpds.setPassword(PASSWORD);
			cpds.setTestConnectionOnCheckout(true);
			cpds.setMinPoolSize(5);
			cpds.setAcquireIncrement(5);
		} catch (Exception ex) {
			// Mysql driver not found
			ex.printStackTrace();
			cpds = null;
		}
	}

	public void closeConnectionPool() {
		cpds.close();
	}

	protected void createAllTables(boolean deleteDatabase) {
		Connection connection = null;
		final List<AbstractTable> tables = getAllTables();

		try {
			connection = getConnection();

			for (AbstractTable table : tables) {
				table.initTable(deleteDatabase, connection);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private List<AbstractTable> getAllTables() {
		List<AbstractTable> tables = new ArrayList<>();
		tables.add(new BallotTable());
		tables.add(new FWPMChoicesTable());

		return tables;
	}

	private void loadProperties() throws IOException {
		Properties props = PropertySingleton.getInstance();

		DATABASE_HOST = props.getProperty("DATABASE_HOST");
		DATABASE_NAME = props.getProperty("DATABASE_NAME");
		USERNAME = props.getProperty("USER_NAME");
		PASSWORD = props.getProperty("USER_PASSWORD");
	}
}
