package am.projects.webserver.report.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;

public class HttpUtilsTest {

	@Test
	public void testLoadURLasRows() throws MalformedURLException, IOException {
		String url = "http://appcfm51:9081/MonitoringServlet?diagnoseRes&log";
		List<String> rows = HTTPUtils.loadURLAsRows(url);
		if (null != rows) {
			for (String string : rows) {
				System.out.println(string);
			}
		}
	}
	
}
