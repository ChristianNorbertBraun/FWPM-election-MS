package de.fhws.fiw.fwpm.election.api;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.election.authentication.Roles;
import de.fhws.fiw.fwpm.election.authentication.User;
import de.fhws.fiw.fwpm.election.exceptions.DuplicateKeyException;
import de.fhws.fiw.fwpm.election.models.Ballot;
import de.fhws.fiw.fwpm.election.models.PeriodConfig;
import de.fhws.fiw.fwpm.election.network.PeriodConfigClient;
import de.fhws.fiw.fwpm.election.network.StudentCPClient;
import de.fhws.fiw.fwpm.election.storage.BallotDao;
import de.fhws.fiw.fwpm.election.storage.DaoFactory;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by christianbraun on 13/05/16.
 * Resource for Getting and Creating new Ballots for student.
 * Although there can be a lot of elections of other periods I will only return the
 * ballots for the latest election.
 *
 * @HTTP 500 For database error
 * @HTTP 403 If you haven't enough permissions.
 */
@Path("ballots")
public class BallotResource extends BasicResource {

	private static final String JSON = "json";

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
	 * Returns for role <b>student</b> the ballot for the specific student. Returns empty list if there are no ballots.
	 * and for <b>employees</b> and <b>apiKeyUser</b> all ballots.
	 *
	 * @ResponseHeader canBeUpdated Only for students. It indicates whether the current election period is valid or not.
	 * @ResponseHeader Link <b>relTypes for employee and apiKeyUser</b>: getStudentBallot <b>relTypes for student</b>: updateBallot<br>
	 * <tt>getStudentBallot:</tt>	Returns template uri for getting a student ballot (<tt>{studentNumber}</tt>).<br>
	 * <tt>updateBallot:</tt>		Returns uri for updating a ballot.<br>
	 * @HTTP 404 If the ballot for the student is not found
	 * @HTTP 423 If there is currently no ElectionPeriod
	 * @HTTP 200 For successful Request
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.STUDENT, Roles.API_KEY_USER, Roles.EMPLOYEE})
	@TypeHint(Ballot[].class)
	public Response getBallotsForLastAssignment() {

//		Students are not allowed to see all studentBallots
		if (context.isUserInRole(Roles.STUDENT)) {
			return getBallotsForStudent(context.getUserPrincipal().getName());
		}

		PeriodConfig config = getLatestPeriod();
		try {
			List<Ballot> ballots = ballotdao.readAllBallotsForElectionPeriod(config.getPeriodId());
			return Response.ok()
					.entity(ballots)
					.header(LINK, linkHeader(getBallotTemplateUri(), GET_STUDENT_BALLOT, JSON))
					.build();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Returns the ballot for a specific student.
	 *
	 * @param studentNumber Represents knumber of student.
	 * @ResponseHeader canBeUpdated Indicates whether the current election period is valid or not.
	 * @ResponseHeader Link <b>relTypes</b>: getAllBallots<br>
	 * <tt>getAllBallots</tt> Uri for collection endpoint.
	 * @HTTP 404 If the ballot for the student is not found
	 * @HTTP 423 If there is currently no ElectionPeriod
	 * @HTTP 200 For successful Request
	 */
	@GET
	@Path("/{studentNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.API_KEY_USER, Roles.EMPLOYEE})
	@TypeHint(Ballot.class)
	public Response getBallotsForStudent(@PathParam("studentNumber") String studentNumber) {
		PeriodConfig config = getLatestPeriod();
		try {
			Ballot ballot = ballotdao.readBallotForStudentAndPeriod(studentNumber, config.getPeriodId());
			if (ballot == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			} else {
				if (context.isUserInRole(Roles.STUDENT)) {
					checkPeriodValidity(config);
					return Response.ok()
							.entity(ballot)
							.link(getLocationUri(ballot), UPDATE_BALLOT)
							.link(getCollectionUri(), CREATE_BALLOT)
							.header(CAN_BE_UPDATED, config.isValid())
							.build();
				} else {
					return Response.ok()
							.entity(ballot)
							.link(getCollectionUri(), GET_ALL_BALLOTS)
							.header(CAN_BE_UPDATED, config.isValid())
							.build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns the ballot for a specific student.
	 *
	 * @ResponseHeader Location the location of the newly created ballot.
	 * @HTTP 409 If there is already a ballot for the asking student
	 * @HTTP 423 If there is currently no ElectionPeriod or if the ElectionPeriod is over
	 * @HTTP 201 For successful Request
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.STUDENT)
	@TypeHint(void.class)
	public Response createBallot() {
		PeriodConfig config = getLatestPeriod();
		try {
			checkPeriodValidity(config);
			User student = (User) context.getUserPrincipal();
			StudentCPClient client = new StudentCPClient(student.getToken());
			int cpForStudent = client.getCPsForStudent(context.getUserPrincipal().getName());
			Ballot ballot = new Ballot((User) context.getUserPrincipal());
			ballot.setCp(cpForStudent);
			ballot.setPeriodId(config.getPeriodId());
			ballotdao.insert(ballot);

			return Response.created(getBaseUri()).build();
		} catch (DuplicateKeyException key) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		} catch (SQLException | IOException | NotFoundException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the ballot of a student. The <b>created, lastUpdated, periodId</b> and <b>cp</b> of a student can't
	 * be updated. Therefore you don't have to send them.
	 * @ResponseHeader Link <b>relTypes</b>: getStudentBallot<br>
	 * <tt>getAllBallots</tt> Uri for the updated ballot.
	 *
	 * @HTTP 409 If the ballot could not be updated. For example twice the same priority in choices.
	 * @HTTP 423 If there is currently no ElectionPeriod or if the ElectionPeriod is over
	 * @HTTP 200 For successful Request
	 * @param ballot The updated ballot for the student.
	 * @return
	 */
	@PUT
	@Path("/{studentNumber}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.STUDENT)
	@TypeHint(Ballot.class)
	public Response updateStudentBallot(Ballot ballot) {
		checkPeriodValidity(getLatestPeriod());

		ballot.setStudentNumber(context.getUserPrincipal().getName());
		try {
			ballotdao.update(ballot);
			return Response.ok(ballot)
					.link(getBaseUri(), GET_STUDENT_BALLOT)
					.build();
		} catch (DuplicateKeyException key) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private URI getLocationUri(Ballot ballot) {
		return uriInfo.getBaseUriBuilder()
				.path(BallotResource.class)
				.path("/" + ballot.getStudentNumber())
				.build();
	}

	private URI getCollectionUri() {
		return uriInfo.getBaseUriBuilder().path(BallotResource.class).build();
	}

	private String getBallotTemplateUri() {
		return getBaseUri().toString() + "/{studentNumber}";
	}

	private URI getBaseUri() {
		return uriInfo.getBaseUriBuilder().path(BallotResource.class).build();
	}


	private PeriodConfig getLatestPeriod() {
		PeriodConfig config = periodClient.getLatestPeriodConfig();

		if (config == null) {
//			423 means that the resource is locked (temporary not available)
			throw new WebApplicationException(423);
		}

		return config;
	}

	private void checkPeriodValidity(PeriodConfig config) {
		if (!config.isValid()) {
//			423 means that the resource is locked (temporary not available)
			throw new WebApplicationException(423);
		}
	}

}
