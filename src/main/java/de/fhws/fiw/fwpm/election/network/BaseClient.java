package de.fhws.fiw.fwpm.election.network;

import de.fhws.fiw.fwpm.election.utils.PropertySingleton;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by christianbraun on 10/08/16.
 */
public abstract class BaseClient implements Headers {

	protected final String AUTH_TOKEN;
	protected String base_uri;
	protected Client client;
	protected WebTarget target;

	public BaseClient(String uriPropertyName, String apiKeyOrPropertyName) throws IOException {
		String authToken;
		base_uri = PropertySingleton.getInstance().getProperty(uriPropertyName);
		authToken = PropertySingleton.getInstance().getProperty(apiKeyOrPropertyName);

		if(authToken == null) {
			AUTH_TOKEN = apiKeyOrPropertyName;
		} else {
			AUTH_TOKEN = authToken;
		}

		this.client = ClientBuilder.newClient();
		this.target = client.target(base_uri);
	}

	public String getBase_uri() {
		return base_uri;
	}

	public void setBase_uri(String base_uri) {
		this.base_uri = base_uri;
	}

	protected String getApiKeyToken() {
		return "api " + AUTH_TOKEN;
	}

	protected boolean successfullStatus(Response response) {
		return response != null && response.getStatus() >= 200 && response.getStatus() <= 300;
	}

	protected Invocation.Builder retargetClient(String uri) {
		target = client.target(uri);
		return target.request();
	}

	protected void retargetClient(Link uri) {
		client.target(uri);
	}

	protected HashMap<String, String> getLinks(Response response) {
		String linkHeader = response.getHeaderString(LINK);
		if(linkHeader == null) {
			return null;
		}

		String[] links = linkHeader.split(",\\s*");
		HashMap<String, String> linkMap = new HashMap<>();

		for(String currentLinkAndRelType: links) {
			String[] linkPair = getLinkPair(currentLinkAndRelType);

			if(linkPair != null) {
				linkMap.put(linkPair[0], linkPair[1]);
			}
		}

		return linkMap;
	}

	private String[] getLinkPair(String linkAndRelType) {
		String[] links = linkAndRelType.split(";\\s*");
		String relType = null;
		String link = null;

		for(String currentLink: links) {
			if(currentLink.contains("rel=")) {
				relType = currentLink.replace("rel=", "").replace("\"", "");
			} else if (currentLink.contains("<")) {
				link = currentLink.replace("<", "").replace(">", "");
			}
		}

		return relType == null || link == null ? null : new String[] {relType, link};
	}

}
