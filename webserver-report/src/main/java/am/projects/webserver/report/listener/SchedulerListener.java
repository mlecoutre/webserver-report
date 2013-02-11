package am.projects.webserver.report.listener;


import am.projects.webserver.report.Constants;
import am.projects.webserver.report.policy.SchedulerPolicy;
import am.projects.webserver.report.scheduler.MonitorJob;
import am.projects.webserver.report.scheduler.PurgeHistoryJob;
import am.projects.webserver.report.utils.ConfigurationManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Scheduler Listener
 * @author mlecoutre
 */
@WebListener
public class SchedulerListener implements ServletContextListener, Constants {

    private static Logger logger = LoggerFactory.getLogger(SchedulerListener.class);

    // Grab the Scheduler instance from the Factory
    private static Scheduler scheduler;

    private int purgeHistoryHour = ConfigurationManager.givePropertyAsInt("purgeHistoryHour");
    private int purgeHistoryMinutes = ConfigurationManager.givePropertyAsInt("purgeHistoryMinutes");

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
            String strDisable = ConfigurationManager.giveProperty("scheduler.enabled");
            boolean isDisable = "false".equalsIgnoreCase(strDisable);
            if (isDisable) {
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
     *
     * @param aScheduler   scheduler to start
     * @throws SchedulerException
     */
    public static void schedule(am.projects.webserver.report.vo.Scheduler aScheduler) throws SchedulerException {

        if (null == scheduler)
            return;

        JobDataMap dataMap = new JobDataMap();
        dataMap.put(am.projects.webserver.report.vo.Scheduler.class.getSimpleName(), aScheduler);

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
     *
     * @param oldScheduler scheduler to stop
     * @return  boolean value
     * @throws SchedulerException
     */
    public static boolean unSchedule(
            am.projects.webserver.report.vo.Scheduler oldScheduler) throws SchedulerException {

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
            am.projects.webserver.report.vo.Scheduler aScheduler) {
        return aScheduler.getApplicationName() + "/" + aScheduler.getServerName() + "/" + aScheduler.getAsName();
    }

    private static String buildJobGroupName(
            am.projects.webserver.report.vo.Scheduler aScheduler) {
        return aScheduler.getApplicationName() + "/" + aScheduler.getServerName();

    }

    // make available scheduler
    public static Scheduler getScheduler() {
        return scheduler;
    }


}