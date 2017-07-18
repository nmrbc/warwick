package pt.ncastro.csvprocessor.util.connection;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * Some utilities to handle JSON.
 * 
 * @author Nuno de Castro
 *
 */
public class JSONUtils {

	/**
	 * Gets the JSON object represented in the given request.
	 * 
	 * @param req
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject getJSONObject(HttpServletRequest req) throws IOException, JSONException {
		if (req != null) {
			// validate if we have a JSON request
			if (!"application/json".equals(req.getContentType())) {
				throw new RuntimeException("Requested content is not JSON");
			}

			return new JSONObject(HTTPUtils.getContent(req.getInputStream()));
		}
		return null;
	}


	/**
	 * Sends a JSON object as a response to a request.
	 * 
	 * @param resp
	 * @param object
	 * @throws IOException
	 */
	public static void sendJSONObject(HttpServletResponse resp, JSONObject object) throws IOException {
		if (resp != null) {
			resp.setContentType("application/json");
		}
		HTTPUtils.writeContent(resp, object.toString());
	}
}
