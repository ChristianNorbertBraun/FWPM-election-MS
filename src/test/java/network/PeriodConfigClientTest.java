package network;

import de.fhws.fiw.fwpm.election.models.PeriodConfig;
import de.fhws.fiw.fwpm.election.network.PeriodConfigClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


/**
 * Created by christianbraun on 01/09/16.
 */
public class PeriodConfigClientTest extends Assert {

	PeriodConfigClient client;

	@Before
	public void init() throws IOException {
		client = new PeriodConfigClient("PERIOD_CONFIG_BASE_URI_TEST");
	}

	@Test
	public void canGetPeriodConfig() {
		try {
			PeriodConfig config = client.getLatestPeriodConfig();
		} catch (Exception e) {
			fail("GetLatestPeriodConfig threw exception but shouldn't");
		}
	}
}
