package de.fhws.fiw.fwpm.election.authentication;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created by christianbraun on 12/07/16.
 */
public class CustomSecurityContext implements SecurityContext {
	private User user;
	private String scheme;


	public CustomSecurityContext(User user, String scheme){
		this.user = user;
		this.scheme = scheme;
	}

	@Override
	public Principal getUserPrincipal() {
		return user;
	}

	@Override
	public boolean isUserInRole(String s) {
		return user.getRole().equalsIgnoreCase(s);
	}

	@Override
	public boolean isSecure() {
		return "https".equals(this.scheme);
	}

	@Override
	public String getAuthenticationScheme() {
		return "custom";
	}
}
