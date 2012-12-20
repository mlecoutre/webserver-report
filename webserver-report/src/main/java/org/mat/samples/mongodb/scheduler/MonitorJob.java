package org.mat.samples.mongodb.scheduler;

import static org.mat.samples.mongodb.policy.MonitorPolicy.batchInsert;

import java.util.Date;

import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.policy.SchedulerPolicy;
import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: E010925 Date: 05/12/12 Time: 14:50
 */
public class MonitorJob implements Job, Constants {

    private Logger logger = LoggerFactory.getLogger(MonitorJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {

        Scheduler scheduler = (Scheduler) jobExecutionContext.getJobDetail()
                .getJobDataMap().get(Scheduler.class.getSimpleName());

        if (null != scheduler) {

            Date now = new Date();

            logger.info("Start " + scheduler.toString());

            String status = STATUS_OK;
            try {
                batchInsert(scheduler.getEndPointURL(),
                        scheduler.getApplicationName(), scheduler.getServerName(),
                        scheduler.getAsName());
            } catch (Exception e) {
                status = String.format("%s,  with exception: '%s'", STATUS_FAILED, e.getMessage());
            }

            SchedulerPolicy.updateSchedulerStatus(scheduler.getSchedulerId(), now, status);
        }
    }

}
