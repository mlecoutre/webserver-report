package org.mat.samples.mongodb.listener;


import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.policy.SchedulerPolicy;
import org.mat.samples.mongodb.scheduler.MonitorJob;
import org.mat.samples.mongodb.scheduler.PurgeHistoryJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mlecoutre
 */
@WebListener
public class SchedulerListener implements ServletContextListener, Constants {

    private static Logger logger = LoggerFactory.getLogger(SchedulerListener.class);

    // Grab the Scheduler instance from the Factory
    private static Scheduler scheduler;

	private int purgeHistoryHour = 23;
	private int purgeHistoryMinutes = 0;

    public SchedulerListener() {
    	super();
    }
    
    public SchedulerListener(int purgeHistoryHour, int purgeHistoryMinutes) {
		super();
		this.purgeHistoryHour = purgeHistoryHour;
		this.purgeHistoryMinutes = purgeHistoryMinutes;
	}
    
	public int getPurgeHistoryHour() {
		return purgeHistoryHour;
	}

	public void setPurgeHistoryHour(int purgeHistoryHour) {
		this.purgeHistoryHour = purgeHistoryHour;
	}

	public int getPurgeHistoryMinutes() {
		return purgeHistoryMinutes;
	}

	public void setPurgeHistoryMinutes(int purgeHistoryMinutes) {
		this.purgeHistoryMinutes = purgeHistoryMinutes;
	}

	@Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            String isDisable = System.getProperty("DISABLE_SCHEDULER");
            if ("true".equalsIgnoreCase(isDisable)) {
                logger.warn("WARNING: SCHEDULER ARE DISABLED");
            } else {
                logger.warn("STARTING SCHEDULER");
                scheduler = StdSchedulerFactory.getDefaultScheduler();

                scheduler.start();
                
                SchedulerPolicy.initSchedulers();
				schedulePurgeHistory(purgeHistoryHour, purgeHistoryMinutes);
                                
            }
        } catch (SchedulerException e) {
            logger.error("Error during Scheduler initialization", e);
        }
    }

    private void schedulePurgeHistory(int hour, int minutes) throws SchedulerException {
		
    	if (null == scheduler)
            return;

        // define the job and tie it to our HelloJob class
        String jobName = PurgeHistoryJob.class.getSimpleName();
        String groupName = "purges";        
        
        JobKey jobKey = new JobKey(jobName, groupName);
        JobDetail job = newJob(PurgeHistoryJob.class)
                .withIdentity(jobKey)
                .build();

        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        Trigger trigger = newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(dailyAtHourAndMinute(hour, minutes))
                .build();

        scheduler.scheduleJob(job, trigger);
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

    /**
     * schedule a job
     * @param aScheduler
     * @throws SchedulerException
     */
    public static void schedule(org.mat.samples.mongodb.vo.Scheduler aScheduler) throws SchedulerException {

        if (null == scheduler)
            return;

        JobDataMap dataMap = new JobDataMap();
        dataMap.put(org.mat.samples.mongodb.vo.Scheduler.class.getSimpleName(), aScheduler);

        // define the job and tie it to our HelloJob class
        String jobName = buildJobName(aScheduler);
        String groupName = buildJobGroupName(aScheduler);        
        JobKey jobKey = new JobKey(jobName, groupName);
        
        JobDetail job = newJob(MonitorJob.class)
                .withIdentity(jobKey)
                .usingJobData(dataMap)
                .build();

        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        Trigger trigger = newTrigger()
                .withIdentity(triggerKey)
                .startNow()
                .withSchedule(simpleSchedule().withIntervalInMinutes(aScheduler.getRequestRepeatIntervalInMinutes()).repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }
    
    
    /**
     * remove a scheduled job
     * @param oldScheduler
     * @return
     * @throws SchedulerException
     */
    public static boolean unSchedule(
			org.mat.samples.mongodb.vo.Scheduler oldScheduler) throws SchedulerException {

    	if (null == scheduler)
            return true;
    	
    	String jobName = buildJobName(oldScheduler);
        String groupName = buildJobGroupName(oldScheduler);
    	JobKey jobKey = new JobKey(jobName, groupName);
    	
    	boolean success = true;
    	if (scheduler.checkExists(jobKey)) {
    		success = scheduler.deleteJob(jobKey);
            logger.info(String.format("unSchedule %s", jobName));
    	}
    	
		return success;
    	
	}

	private static String buildJobName(
			org.mat.samples.mongodb.vo.Scheduler aScheduler) {
		String jobName = aScheduler.getApplicationName() + "/" + aScheduler.getServerName() + "/" + aScheduler.getAsName();
		return jobName;
	}

	private static String buildJobGroupName(
			org.mat.samples.mongodb.vo.Scheduler aScheduler) {
		String groupName = aScheduler.getApplicationName() + "/" + aScheduler.getServerName();
		return groupName;
	}

    // make available scheduler
    public static Scheduler getScheduler() {
        return scheduler;
    }
    

}