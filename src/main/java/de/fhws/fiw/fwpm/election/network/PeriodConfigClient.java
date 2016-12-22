package de.fhws.fiw.fwpm.election.network;

import de.fhws.fiw.fwpm.election.models.PeriodConfig;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by christianbraun on 10/08/16.
 */
public class PeriodConfigClient extends BaseClient {

	private static final String LATEST_PERIOD_REL_TYPE = "latestPeriod";

	public PeriodConfigClient() throws IOException {
		super("PERIOD_CONFIG_BASE_URI", "PERIOD_CONFIG");
	}

	public PeriodConfigClient(String testUriPropertyName) throws IOException {
		super(testUriPropertyName, "PERIOD_CONFIG");
	}

	public PeriodConfig getLatestPeriodConfig() {

		Invocation.Builder builder = retargetClient(base_uri);
		builder.header(AUTHORIZATION, getApiKeyToken());
		Response linkResponse = builder.get();

		if (successfullStatus(linkResponse)) {
			String latestPeriodUrl = getLinks(linkResponse).get(LATEST_PERIOD_REL_TYPE);
			if(latestPeriodUrl == null) {
				return null;
			}
			else {
				builder = retargetClient(latestPeriodUrl);
				builder.header(AUTHORIZATION, getApiKeyToken());
				Response latestPeriodResponse = builder.get();

				if(successfullStatus(latestPeriodResponse)) {
					return latestPeriodResponse.readEntity(PeriodConfig.class);
				}
			}
		}

		return null;
	}
}
