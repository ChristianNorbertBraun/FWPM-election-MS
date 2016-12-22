package de.fhws.fiw.fwpm.election.storage;

import java.io.IOException;

/**
 * Created by christianbraun on 18/05/16.
 */
public class DaoFactory {

	private static DaoFactory instance;

	private Persistency persistency;
	private BallotDao ballotDao;


	private DaoFactory(boolean deleteDatabase) throws IOException {
		persistency = Persistency.getInstance(deleteDatabase);
	}

	public static DaoFactory getInstance() throws IOException {
		return getInstance(false);
	}

	public static DaoFactory getInstance(boolean deleteDatabase) throws IOException {
		if (instance == null) {
			instance = new DaoFactory(deleteDatabase);
		}
		return instance;
	}

	public BallotDao createBallotService() {
		if (ballotDao == null) {
			ballotDao = new BallotDaoImpl(persistency);
		}
		return ballotDao;
	}

}
