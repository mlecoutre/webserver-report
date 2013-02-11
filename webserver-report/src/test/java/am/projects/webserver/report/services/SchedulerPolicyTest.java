package am.projects.webserver.report.services;

import com.mongodb.DBObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import am.projects.webserver.report.Constants;
import am.projects.webserver.report.listener.MongoListener;
import am.projects.webserver.report.policy.MonitorPolicy;
import am.projects.webserver.report.policy.SchedulerPolicy;
import am.projects.webserver.report.vo.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SchedulerPolicyTest implements Constants {
    private static final String MONGO_SERVER = "dun-tst-devf01";
    private static final int MONGO_PORT = 27017;
    private static final String MONGO_DB = "mydbTest";

    private static DBObject oneObject;

    private static Logger logger = LoggerFactory
            .getLogger(SchedulerPolicyTest.class);

    private final MongoListener ml;

    public SchedulerPolicyTest() {
        super();
        ml = new MongoListener(MONGO_SERVER, MONGO_PORT, MONGO_DB);
    }

    @Before
    public void setUp() throws Exception{

        ml.contextInitialized(null);
        addScheduler();
        SchedulerPolicyTest.oneObject = SchedulerPolicy.giveCollection().findOne();
    }

    @After
    public void tearDown() throws Exception {
        MonitorPolicy.purgeDB(SCHEDULER_CONFIG_COLLECTION);
        ml.contextDestroyed(null);
    }

    @Test
    public void testAddScheduler() throws IOException, SchedulerException {

        String id = addScheduler();
        assertNotNull("Once created into the dataStore, schedulerId should not be null", id);

    }

    public String addScheduler() throws IOException, SchedulerException {
        Scheduler scheduler = new Scheduler();
        scheduler.setApplicationName("SteelUser2");
        scheduler.setServerName("appcfm512");
        scheduler.setAsName("AS_STEELUSER2");

        scheduler
                .setEndPointURL("http://appcfm51:9081/MonitoringServlet?diagnoseRes&log");

        String id = SchedulerPolicy.addScheduler(scheduler);
        logger.info(scheduler.toString());
        return id;
    }

    @Test
    public void testUpdateScheduler() throws IOException, SchedulerException {

        List<Scheduler> schedulers = SchedulerPolicy.listSchedulers();
        if (null != schedulers && !schedulers.isEmpty()) {
            Scheduler scheduler = schedulers.get(0);
            scheduler.setState(STATUS_STOPPED);
            if (SchedulerPolicy.updateScheduler(scheduler.getSchedulerId(), scheduler)) {
                schedulers = SchedulerPolicy.listSchedulers();
                if (null != schedulers) {
                    for (Scheduler s : schedulers) {
                        logger.info(s.toString());
                    }
                }
            }

        }

    }

    @Test
    public void testFindSchedulerById() {
        String schedulerId = oneObject.get("_id").toString();
        Scheduler scheduler = SchedulerPolicy.findSchedulerById(schedulerId);
        logger.info(String.format("testFindSchedulerById: %s", scheduler));
        assertNotNull("Scheduler should not be null", scheduler);
    }


    @Test
    public void testListSchedulers() {

        List<Scheduler> schedulers = SchedulerPolicy.listSchedulers();
        if (null != schedulers) {
            for (Scheduler scheduler : schedulers) {
                logger.info(scheduler.toString());
            }
        }

    }

}
