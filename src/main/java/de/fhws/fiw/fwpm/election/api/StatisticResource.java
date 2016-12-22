package de.fhws.fiw.fwpm.election.api;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.election.authentication.Roles;
import de.fhws.fiw.fwpm.election.models.PeriodConfig;
import de.fhws.fiw.fwpm.election.models.VotesPerFWPM;
import de.fhws.fiw.fwpm.election.network.PeriodConfigClient;
import de.fhws.fiw.fwpm.election.storage.BallotDao;
import de.fhws.fiw.fwpm.election.storage.DaoFactory;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Returns statistics for the current election period.
 */
@Path("statistics")
public class StatisticResource extends BasicResource {

	@Context
	UriInfo uriInfo;
	@Context
	SecurityContext context;
	BallotDao ballotdao;
	PeriodConfigClient periodClient;

	@PostConstruct
	private void init() {
		try {
			ballotdao = DaoFactory.getInstance().createBallotService();
			periodClient = new PeriodConfigClient();
		} catch (IOException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of fwpms with the number of interested students.
	 * @HTTP 500 For database error
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 200 Ok
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.EMPLOYEE)
	@TypeHint(VotesPerFWPM[].class)
	public Response getVotePerFWPM() {
		try {
			PeriodConfig config = getLatestPeriod();
			return Response.ok().entity(ballotdao.countVotesPerFWPM(config.getPeriodId())).build();
		} catch(SQLException ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	private PeriodConfig getLatestPeriod() {
		PeriodConfig config = periodClient.getLatestPeriodConfig();

		if (config == null) {
//			423 means that the resource is locked (temporary not available)
			throw new WebApplicationException(423);
		}

		return config;
	}
}
