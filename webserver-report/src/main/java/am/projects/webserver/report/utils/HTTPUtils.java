package am.projects.webserver.report.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author U002617
 * Utils class for loading datas from URL
 *
 */
public class HTTPUtils {
	
	private HTTPUtils() {
		super();
	}

	/**
	 * return the result of the GET HTTP on the specified URL
	 * 
	 * @param urlString
	 *            the url to connect on
	 * @return a list of string
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws HTTPCommunicationException
	 */
	public static List<String> loadURLAsRows(String urlString)
			throws MalformedURLException, IOException {

		List<String> raws = new ArrayList<String>();

		HttpURLConnection connection = null;
		try {
			// The URL to connect to
			URL url = new URL(urlString);

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setUseCaches(false);
			connection.setRequestMethod("GET");

			// Connect to the URL with timeout management
			int responseCode = -1;
			try {
				responseCode = connection.getResponseCode();
			} catch (SocketTimeoutException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			}

			if (HttpURLConnection.HTTP_OK == responseCode) {
				BufferedReader buffer = null;
				try {
					buffer = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));

					String str = null;
					while ((str = buffer.readLine()) != null) {
						raws.add(str);
					}
				} finally {
					if (null != buffer) {
						try {
							buffer.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				throw new IOException("Get on URL '" + urlString + "' return : " + responseCode);
			}
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}

		return raws;
	}
	
}
