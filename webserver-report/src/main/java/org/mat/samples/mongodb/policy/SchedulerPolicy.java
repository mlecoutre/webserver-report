package org.mat.samples.mongodb.policy;

import java.util.List;

import org.mat.samples.mongodb.vo.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SchedulerPolicy
 * 
 * @author vincentClaeysen
 */
public class SchedulerPolicy {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerPolicy.class);

	private SchedulerPolicy() {
		super();
	}

	public static List<Scheduler> listSchedulers() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String addScheduler(Scheduler scheduler) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean updateScheduler(String schedulerId,
			Scheduler scheduler) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
}

