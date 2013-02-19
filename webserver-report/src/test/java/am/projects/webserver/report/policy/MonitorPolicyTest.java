package am.projects.webserver.report.policy;

import am.projects.webserver.report.TestConstants;
import am.projects.webserver.report.listener.MongoListener;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import static org.junit.Assert.*;

/**
 * User: E010925
 * Date: 19/02/13
 * Time: 15:03
 */
public class MonitorPolicyTest implements TestConstants {

    private int FAKE_PORT = 11211;

    HttpServer server = null;

    static String response = "{\"timestamp\": \"2013-01-02 00:09:05.449\", \"type\": \"memory\", \"id\": \"total_memory\", \"status\": \"OK\", \"sizemb\": \"1024\", \"message\": null}";
    private static Logger logger = LoggerFactory
            .getLogger(MonitorPolicyTest.class);

    private MongoListener ml = new MongoListener(MONGO_SERVER, MONGO_PORT, MONGO_DB);

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            try {
                logger.debug("wait 2 seconds...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //nothing to do
            }
            logger.debug("   > Send response");

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    @Before
    public void setUp() throws Exception {
        ml.contextInitialized(null);
        logger.debug(" ** Start mock server");
        server = HttpServer.create(new InetSocketAddress(FAKE_PORT), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @After
    public void tearDown() {
        ml.contextDestroyed(null);
        logger.debug(" ** Stop mock server");
        server.stop(0);
    }

    @Test
    public void testBatchInsertTimeout() throws Exception {
        boolean isTimeoutThrow = false;
        try {
            MonitorPolicy.HTTP_TIMEOUT = 1000;
            MonitorPolicy.batchInsert("http://localhost:" + FAKE_PORT + "/test", APPLICATION_NAME, SERVER_NAME, AS_NAME);
        } catch (SocketTimeoutException ste) {
            logger.debug("Timeout OK");
            isTimeoutThrow = true;
        }
        assertTrue("Should throw a timeout exception and free the thread", isTimeoutThrow);
    }
}
