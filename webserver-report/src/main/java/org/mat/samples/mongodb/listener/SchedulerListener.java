package org.mat.samples.mongodb.listener;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.policy.SchedulerPolicy;
import org.mat.samples.mongodb.scheduler.MonitorJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mlecoutre
 */
@WebListener
public class SchedulerListener implements ServletContextListener, Constants {

    private Logger logger = LoggerFactory.getLogger(SchedulerListener.class);

    // Grab the Scheduler instance from the Factory
    private static Scheduler scheduler;

    public SchedulerListener() {
        logger.info("Start Job Scheduling");

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            SchedulerPolicy.initSchedulers();
        } catch (SchedulerException e) {
            logger.error("Error during Scheduler initialization", e);
        }
    }

    /**
     * Close the scheduler
     *
     * @param event ServletContextEvent not used
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            logger.error("Error during Scheduler shutdown", e);
        }
    }

    public static void schedule(org.mat.samples.mongodb.vo.Scheduler aScheduler) throws SchedulerException {
		
    	if (null == scheduler)
    		return;
    	
    	JobDataMap dataMap = new JobDataMap();
		dataMap.put(org.mat.samples.mongodb.vo.Scheduler.class.getSimpleName(), aScheduler);
		
		// define the job and tie it to our HelloJob class
		String groupName = aScheduler.getApplicationName()+ "/" + aScheduler.getServerName();
		String jobName = groupName + "/" + aScheduler.getAsName();
		
		JobDetail job = newJob(MonitorJob.class)
		    .withIdentity(jobName, groupName)
		    .usingJobData(dataMap)
		    .build();
			    
		Trigger trigger = newTrigger()
			//.withIdentity("", "")
			.startNow()
			.withSchedule(simpleSchedule().withIntervalInMinutes(aScheduler.getRequestRepeatIntervalInMinutes()).repeatForever())
			.build(); 
		
		scheduler.scheduleJob(job, trigger);
	}

	// make available scheduler
	public static Scheduler getScheduler() {
		return scheduler;
	}
    
    


}