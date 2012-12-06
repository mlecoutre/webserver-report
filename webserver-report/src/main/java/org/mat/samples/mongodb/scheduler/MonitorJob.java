package org.mat.samples.mongodb.scheduler;

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
public class MonitorJob implements Job{

    private Logger logger = LoggerFactory.getLogger(MonitorJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Start Quartz Job");
    }
}
