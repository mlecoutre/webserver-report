package org.mat.samples.mongodb.listener;


import com.mongodb.DB;
import com.mongodb.Mongo;
import org.mat.samples.mongodb.Constants;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author mlecoutre
 */
@WebListener
public class SchedulerListener implements ServletContextListener, Constants {

    private Logger logger = LoggerFactory.getLogger(SchedulerListener.class);
    // Grab the Scheduler instance from the Factory

    private Scheduler scheduler;

    public SchedulerListener() {
        logger.info("Start Job Scheduling");

    }


    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
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


}