package org.mat.samples.mongodb.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.mat.samples.mongodb.policy.MonitorPolicy;
import org.mat.samples.mongodb.policy.SchedulerPolicy;
import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeHistoryJob implements Job {
	
	private final Logger logger = LoggerFactory.getLogger(PurgeHistoryJob.class);
	private final DateFormat formatter;

	public PurgeHistoryJob() {
		super();
		
		formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00.00", Locale.ENGLISH);
		
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		long start = System.currentTimeMillis();
		
		Calendar calendar = GregorianCalendar.getInstance();
		
		Date now = new Date();
    	    	
    	List<Scheduler> schedulers = SchedulerPolicy.listSchedulers();
    	if ((null != schedulers) && (!schedulers.isEmpty())) {
    		for (Scheduler scheduler : schedulers) {
    			
    			int amount = 0-scheduler.getMaxHistoryToKeepInDays();
    			
    			calendar.setTime(now);
            	calendar.add(Calendar.DATE, amount);
            	
            	String oldestDate = formatter.format(calendar.getTime());
            	
            	String applicationName = scheduler.getApplicationName();
				String serverName = scheduler.getServerName();
				String asName = scheduler.getAsName();
				logger.info(String.format("Purge datas for application %s, server %s and AS %s before %s", applicationName,
                        serverName, asName, oldestDate));
            	try {
					MonitorPolicy.purgeHistory(applicationName, 
							serverName,
							asName, 
							oldestDate);
				} catch (Exception e) {
					logger.error("Receive exception while removing datas history", e);
				}
				
			}
    	}
    	
    	long ellapsedTime = System.currentTimeMillis() - start;
    	logger.info(String.format("Purge history job during %d ms.", ellapsedTime));
    	
	}
	
	

}
