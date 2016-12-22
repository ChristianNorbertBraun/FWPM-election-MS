package de.fhws.fiw.fwpm.election.network;

import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by christianbraun on 01/09/16.
 */
public class StudentCPClient extends BaseClient {

	public StudentCPClient(String apiKeyOrPropertyName) throws IOException {
		super("CP_URL", apiKeyOrPropertyName);
	}

	public int getCPsForStudent(String studentNumber) throws NotFoundException {
		Invocation.Builder builder = retargetClient(base_uri.replace("{knummer}", studentNumber));
		builder.header(FHWS_JWT_TOKEN, AUTH_TOKEN);
		Response studentResponse = builder.get();

		if (successfullStatus(studentResponse)) {
			try {
				String studentAsJson = studentResponse.readEntity(String.class);
				JSONObject student = new JSONObject(studentAsJson);
				return student.getInt("creditPoints");

			} catch (JSONException e) {
				throw new NotFoundException();
			}
		}

		throw new NotFoundException();
	}
}
