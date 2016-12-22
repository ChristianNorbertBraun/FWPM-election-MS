package de.fhws.fiw.fwpm.election.api;

import de.fhws.fiw.fwpm.election.network.Headers;

/**
 * Created by christianbraun on 11/08/16.
 */
public abstract class BasicResource implements  RelTypes, Headers {


	protected String linkHeader(final String uri, final String rel, final String mediaType) {
		final StringBuilder sb = new StringBuilder();

		sb.append('<').append(uri).append(">;");
		sb.append("rel").append("=\"").append(rel).append("\"");

		if (mediaType != null && mediaType.isEmpty() == false) {
			sb.append(";");
			sb.append("type").append("=\"").append(mediaType).append("\"");
		}

		return sb.toString();
	}


}
