package pt.ncastro.csvprocessor.service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import pt.ncastro.csvprocessor.domain.CSVDecisionFile;
import pt.ncastro.csvprocessor.util.connection.HTTPUtils;
import pt.ncastro.csvprocessor.util.connection.JSONUtils;



/**
 * The trade service servlet. This is the one that takes external POST JSON
 * messages and prepares data for later processing and showing to client.
 * 
 * @author Nuno de Castro
 *
 */
@MultipartConfig
public class CSVProcessingServiceServlet extends HttpServlet {

	// Serial version ID.
	private static final long serialVersionUID = 5872009201892502398L;
	// Logging object
	private static final Logger log = Logger.getLogger(CSVProcessingServiceServlet.class.getName());


	/**
	 * Default constructor.
	 */
	public CSVProcessingServiceServlet() {
		super();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("Receiving a request");

		try {
			Part file = req.getPart("file");
			String content = HTTPUtils.getContent(file.getInputStream());
			CSVDecisionFile result = CSVDecisionFile.createFilteredCSVDecisionFile(content);
			JSONUtils.sendJSONObject(resp, new JSONObject(result.toJSON()));

		} catch (IOException ioe) {
			log.severe(ioe.getMessage());
			JSONObject o = new JSONObject();
			o.put("error", "Error parsing the given CSV file");
			JSONUtils.sendJSONObject(resp, o);

		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}