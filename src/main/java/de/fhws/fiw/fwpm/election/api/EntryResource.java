package de.fhws.fiw.fwpm.election.api;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.election.authentication.Roles;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Entry Point of Application.
 */
@Path("/")
public class EntryResource extends BasicResource {

	@Context
	UriInfo uriInfo;
	@Context
	SecurityContext context;

	/**
	 * Returns the links to all resources.
	 * @ResponseHeader Link <b>reltypes</b>: ballots, statistics<br>
	 * <tt>ballots:</tt>	Contains the uri for the ballot resource.<br>
	 * <tt>statistics:</tt> Contains the uri for the statistics resource.
	 * @HTTP 204 NoContent
	 * @HTTP 403 If you haven't enough permissions.
	 */
	@GET
	@RolesAllowed({Roles.API_KEY_USER, Roles.STUDENT, Roles.EMPLOYEE})
	@TypeHint(void.class)
	public Response getResources() {
		return Response.noContent()
				.link(
						uriInfo.getBaseUriBuilder().path(BallotResource.class).build(),
						BALLOTS)
				.link(
						uriInfo.getBaseUriBuilder().path(StatisticResource.class).build(),
						STATISTICS)
				.build();
	}
}
