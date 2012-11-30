package org.mat.samples.mongodb.services;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mat.samples.mongodb.listener.MongoListener;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * User: E010925
 * Date: 27/11/12
 * Time: 14:56
 */
public class MonitorServiceTest {
    private static final String MONGO_SERVER = "dun-tst-devf01";
    private static final int MONGO_PORT = 27017;
    private static final String MONGO_DB = "mydbTest2";
    private static final String RESOURCE_FILE = "/j2eeMonitoring.log";
    private static final String SERVER_NAME = "appcfm51";
    public static final String AS_NAME = "AS_STEELUSER";
    public static final String APPLICATION_NAME = "SteelUserTest";

    private MongoListener ml = new MongoListener(MONGO_SERVER, MONGO_PORT, MONGO_DB);

    @Before
    public void setUp() throws Exception {
        ml.contextInitialized(null);
        MonitorService.batchInsert(MongoListener.class.getResource(RESOURCE_FILE).toExternalForm(), APPLICATION_NAME, SERVER_NAME, AS_NAME);
    }

    @After
    public void tearDown() throws Exception {
        MonitorService.purgeDB(APPLICATION_NAME);
        ml.contextDestroyed(null);
    }

    @Test
    public void testDistinctDataSource() {
        List<String> mList = MonitorService.listDataSources(APPLICATION_NAME, SERVER_NAME, AS_NAME);
        System.out.println("testDistinctDataSource : " + mList);
        assertTrue("We should have 11 DS in DB ", mList.size() == 11);
    }

    @Test
    public void testRequestUsedConnection() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MonitorService.requestUsedConnection("DS_STEELUSER_MASTER", APPLICATION_NAME, SERVER_NAME, AS_NAME, baos);
        assertNotNull("TotalThreads should not be null", baos.toString());
    }

    @Test
    public void testRequestTotalThreads() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MonitorService.requestTotalThreads(APPLICATION_NAME, SERVER_NAME, AS_NAME, baos);
        assertNotNull("TotalThreads should not be null", baos.toString());
    }

    @Test
    public void testListServers() {
        List<String> servers = MonitorService.listServers(APPLICATION_NAME);
        System.out.println("servers: " + servers.size());
        assertTrue("We should have only one server appcfm51", servers.size() == 1);
        assertEquals("Server should be appcfm51", servers.get(0), "appcfm51");
    }

    @Test
    public void testListASs() {
        List<String> ass = MonitorService.listASs(APPLICATION_NAME);
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
        Set<String> applications = MonitorService.listApplications();
        String applicationName = (String) applications.toArray()[0];
        System.out.println("testListApplications: " + applicationName);
        // we have SteelUserTest
        assertEquals("We should have only one application", applications.size(), 1);
        assertEquals("Application should be 'SteelUserTest'", "SteelUserTest", applicationName);
    }
}
