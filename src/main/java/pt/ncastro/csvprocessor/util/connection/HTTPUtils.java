package pt.ncastro.csvprocessor.util.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;



/**
 * Some utilities to handle HTTP requests.
 * 
 * @author Nuno de Castro
 *
 */
public class HTTPUtils {

	/**
	 * Gets the HTTP request content into a string.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String getContent(InputStream is) throws IOException {
		if (is != null) {
			ByteArrayOutputStream baos = null;
			try {
				// get the data
				baos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int n;
				while ((n = is.read(buff)) > 0) {
					baos.write(buff, 0, n);
				}
				return baos.toString();

			} catch (Exception e) {
				throw new RuntimeException(e);

			} finally {
				// perform cleanups
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}
			}
		}
		return null;
	}


	/**
	 * Writes a string as a response to a request.
	 * 
	 * @param resp
	 * @param content
	 * @throws IOException
	 */
	public static void writeContent(HttpServletResponse resp, String content) throws IOException {
		if (resp != null && content != null) {
			byte[] data = content.getBytes();
			resp.setContentLength(data.length);

			OutputStream out = null;
			try {
				out = resp.getOutputStream();
				out.write(data);
				out.flush();

			} catch (Exception e) {
				throw new RuntimeException(e);

			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}
}
