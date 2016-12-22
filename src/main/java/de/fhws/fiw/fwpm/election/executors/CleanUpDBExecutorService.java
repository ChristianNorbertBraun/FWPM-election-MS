package de.fhws.fiw.fwpm.election.executors;

import de.fhws.fiw.fwpm.election.models.PeriodConfig;
import de.fhws.fiw.fwpm.election.network.PeriodConfigClient;
import de.fhws.fiw.fwpm.election.storage.BallotDao;
import de.fhws.fiw.fwpm.election.storage.DaoFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Checks latest election period once in a while and deletes all older periods
 */
public class CleanUpDBExecutorService {

	private ScheduledExecutorService executorService;
	private BallotDao ballotDao;
	private PeriodConfigClient client;

	public CleanUpDBExecutorService() {
		try {
			ballotDao = DaoFactory.getInstance().createBallotService();
			client = new PeriodConfigClient();
		} catch (IOException e) {
			throw new WebApplicationException(
					"Unable to start CleanUpDbExecutreService",
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	public void start() {
		executorService = Executors.newSingleThreadScheduledExecutor();
		Runnable startService = () -> {
			PeriodConfig latest = client.getLatestPeriodConfig();
			if(latest != null) {
				try {
					ballotDao.deleteAllOldBallotsForPeriodId(latest.getPeriodId() - 1);
					System.out.println("All periods deleted with lower id then: " + latest.getPeriodId() );
				} catch (SQLException e) {
					// fall through
					e.printStackTrace();
				}
			}
		};

		executorService.scheduleWithFixedDelay(startService, 1, 10, TimeUnit.DAYS);
	}

	public void stop() {
		if(executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
	}
}
