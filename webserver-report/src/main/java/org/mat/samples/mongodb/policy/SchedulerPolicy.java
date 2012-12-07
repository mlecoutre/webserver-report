package org.mat.samples.mongodb.policy;

import static org.mat.samples.mongodb.listener.SchedulerListener.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.listener.MongoListener;
import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/**
 * SchedulerPolicy
 * 
 * @author vincentClaeysen
 */
public class SchedulerPolicy implements Constants {

	private static final String ID_FIELD = "_id";

	private static Logger logger = LoggerFactory
			.getLogger(SchedulerPolicy.class);

	private static ObjectMapper mapper = new ObjectMapper();

	private SchedulerPolicy() {
		super();
	}

	private static DBCollection giveCollection() {
		DB db = MongoListener.getMongoDB();
		DBCollection collection = db.getCollection(SCHEDULER_CONFIG_COLLECTION);
		return collection;
	}

	/**
	 * Schedule the job if required
	 * 
	 * @param scheduler
	 * @throws SchedulerException
	 */
	private static void scheduleIfNeeded(Scheduler scheduler)
			throws SchedulerException {
		if (Constants.STATUS_RUNNING.equals(scheduler.getInitialState())) {
			logger.info("Schedule " + scheduler.toString());
			schedule(scheduler);
		}
	}

	// launch Schedulers as jobs
	public static void initSchedulers() throws SchedulerException {

		List<Scheduler> schedulers = listSchedulers();
		for (Scheduler scheduler : schedulers) {
			scheduleIfNeeded(scheduler);
		}
	}

	public static List<Scheduler> listSchedulers() {

		DBCollection collection = giveCollection();

		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("applicationName", "-1");
		orderBy.put("serverName", "-1");
		orderBy.put("asName", "-1");

		DBCursor cursor = collection.find().sort(orderBy);

		List<Scheduler> schedulers = new ArrayList<Scheduler>();
		try {

			while (cursor.hasNext()) {
				DBObject dbObj = cursor.next();

				Map<?, ?> map = dbObj.toMap();
				Scheduler scheduler = new Scheduler();
				BeanUtils.populate(scheduler, map);
				schedulers.add(scheduler);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return schedulers;
	}
	
	/**
	 * 
	 * @param scheduler
	 * @return the new scheduler Id
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public static String addScheduler(Scheduler scheduler)
			throws JsonGenerationException, JsonMappingException, IOException,
			SchedulerException {

		DBCollection collection = giveCollection();

		String jsonString = mapper.writeValueAsString(scheduler);
		DBObject doc = (DBObject) JSON.parse(jsonString);

		String id = null;

		collection.insert(doc);
		if (doc.containsField(ID_FIELD)) {
			id = String.valueOf(doc.get(ID_FIELD));
			scheduler.set_id(id);
		}
		scheduleIfNeeded(scheduler);

		return id;

	}

	/**
	 * 
	 * @param schedulerId
	 * @param scheduler
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static boolean updateScheduler(String schedulerId,
			Scheduler scheduler) throws JsonGenerationException, JsonMappingException, IOException {
		
		scheduler.set_id(schedulerId);
		
		DBCollection collection = giveCollection();

		String jsonString = mapper.writeValueAsString(scheduler);
		DBObject doc = (DBObject) JSON.parse(jsonString);
		
		WriteResult result = collection.save(doc);
		
		return result.getN()>0;
	}

}
