package org.mat.samples.mongodb.scheduler;

import static org.mat.samples.mongodb.policy.MonitorPolicy.batchInsert;

import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: E010925
 * Date: 05/12/12
 * Time: 14:50
 */
public class MonitorJob implements Job {

    private Logger logger = LoggerFactory.getLogger(MonitorJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        Scheduler scheduler = (Scheduler) jobExecutionContext.getJobDetail()
                .getJobDataMap().get(Scheduler.class.getSimpleName());

        if (scheduler.isStopped()) {
            logger.info(String.format("Job %s on server %s for application %s is in STOPPED status", scheduler.getAsName(),
                    scheduler.getServerName(), scheduler.getApplicationName()));
            return;
        }
        logger.info(String.format("START Job %s for AS %s for application %s", scheduler.getAsName(),
                scheduler.getServerName(), scheduler.getApplicationName()));
        batchInsert(scheduler.getEndPointURL(), scheduler.getApplicationName(),
                scheduler.getServerName(), scheduler.getAsName());

    }
}
