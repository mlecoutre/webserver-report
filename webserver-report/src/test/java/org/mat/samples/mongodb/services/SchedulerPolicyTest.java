package org.mat.samples.mongodb.services;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.listener.MongoListener;
import org.mat.samples.mongodb.policy.MonitorPolicy;
import org.mat.samples.mongodb.policy.SchedulerPolicy;
import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerPolicyTest implements Constants {

	private static Logger logger = LoggerFactory
			.getLogger(SchedulerPolicyTest.class);

	private final MongoListener ml;

	public SchedulerPolicyTest() {
		super();
		ml = new MongoListener();
	}

	@Before
	public void setUp() {
		ml.contextInitialized(null);
	}

	@After
	public void tearDown() throws Exception {
		MonitorPolicy.purgeDB(SCHEDULER_CONFIG_COLLECTION);
		ml.contextDestroyed(null);
	}

	@Test
	public void testAddScheduler() throws  IOException, SchedulerException {

		String id = addScheduler();
		assertNotNull(id);

	}

	public String addScheduler() throws IOException, SchedulerException {
		Scheduler scheduler = new Scheduler();
		scheduler.setApplicationName("SteelUser");
		scheduler.setServerName("appcfm51");
		scheduler.setAsName("AS_STEELUSER");

		scheduler
				.setEndPointURL("http://appcfm51:9081/MonitoringServlet?diagnoseRes&log");

		String id = SchedulerPolicy.addScheduler(scheduler);
		logger.info(scheduler.toString());
		return id;
	}

	@Test
	public void testUpdateScheduler() throws IOException, SchedulerException {

		addScheduler();
		List<Scheduler> schedulers = SchedulerPolicy.listSchedulers();
		if (null != schedulers && !schedulers.isEmpty()) {
			Scheduler scheduler = schedulers.get(0);
			scheduler.setState(STATUS_STOPPED);
			if (SchedulerPolicy.updateScheduler(scheduler.getSchedulerId(), scheduler)) {
				schedulers = SchedulerPolicy.listSchedulers();
				if (null != schedulers) {
					for (Scheduler schedul : schedulers) {
						logger.info(schedul.toString());
					}
				}
			}

		}

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
