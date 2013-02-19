package am.projects.webserver.report.services;

import am.projects.webserver.report.Constants;
import am.projects.webserver.report.TestConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import am.projects.webserver.report.listener.MongoListener;
import am.projects.webserver.report.policy.MonitorPolicy;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

/**
 * User: E010925
 * Date: 27/11/12
 * Time: 14:56
 */
public class MonitorServiceTest implements TestConstants {


    private MongoListener ml = new MongoListener(MONGO_SERVER, MONGO_PORT, MONGO_DB);

    @Before
    public void setUp() throws Exception {
        ml.contextInitialized(null);
        //create configuration for getting as and server data.
        BasicDBObject obj = new BasicDBObject();
        obj.put("applicationName", APPLICATION_NAME);
        obj.put("serverName", SERVER_NAME);
        obj.put("asName", AS_NAME);
        obj.put("state", "manual");
        MongoListener.getMongoDB().getCollection(Constants.SCHEDULER_CONFIG_COLLECTION).insert(obj);
        MonitorPolicy.batchInsert(MongoListener.class.getResource(RESOURCE_FILE).toExternalForm(), APPLICATION_NAME, SERVER_NAME, AS_NAME);
    }

    @After
    public void tearDown() throws Exception {
        MonitorPolicy.purgeDB(APPLICATION_NAME);
        ml.contextDestroyed(null);
    }

    @Test
    public void testDistinctDataSource() {
        List<String> mList = MonitorPolicy.listDataSources(APPLICATION_NAME, SERVER_NAME, AS_NAME);
        System.out.println("testDistinctDataSource : " + mList);
        assertTrue("We should have 11 DS in DB ", mList.size() == 11);
    }

    @Test
    public void testRequestUsedConnection() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MonitorPolicy.requestUsedConnection("DS_STEELUSER_MASTER", APPLICATION_NAME, SERVER_NAME, AS_NAME, null, null, baos);
        assertNotNull("TotalThreads should not be null", baos.toString());
    }

    @Test
    public void testRequestTotalThreads() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MonitorPolicy.requestTotalThreads(APPLICATION_NAME, SERVER_NAME, AS_NAME, null, null, baos);

        List mThreads = (List) JSON.parse(baos.toString());

        assertNotNull("TotalThreads should not be null", baos.toString());
        assertTrue("Nb threads measures should be equals to 144", mThreads.size() == 144);
    }

    @Test
    public void testListServers() {
        List<String> servers = MonitorPolicy.listServers(APPLICATION_NAME);
        System.out.println("servers: " + servers.size());
        assertTrue("We should have only one server appcfm51", servers.size() == 1);
        assertEquals("Server should be appcfm51", servers.get(0), "appcfm51");
    }

    @Test
    public void testListASs() {
        List<String> ass = MonitorPolicy.listASs(APPLICATION_NAME);
        System.out.println("ass: " + ass.size());
        assertTrue("We should have only one AS", ass.size() == 1);
        assertEquals("AS should be AS_STEELUSER", ass.get(0), "AS_STEELUSER");
    }

    @Test
    public void testGetServerInfo() {
        String[] array = "http://appcfm51/log/WebSphere/AppServer/appcfm51Node/AS_STEELUSER/j2eeMonitoring.log.2012-11-26".split("/");
        System.out.println(array[2] + "/" + array[array.length - 2]);
    }

    @Test
    public void testListApplications() {
        Set<String> applications = MonitorPolicy.listApplications();
        String applicationName = (String) applications.toArray()[0];
        System.out.println("testListApplications: " + applicationName);
        // we have SteelUserTest
        assertEquals("We should have only one application", applications.size(), 1);
        assertEquals("Application should be 'SteelUserTest'", "SteelUserTest", applicationName);
    }
}
