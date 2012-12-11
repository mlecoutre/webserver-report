package org.mat.samples.mongodb.policy;

import static org.mat.samples.mongodb.listener.SchedulerListener.schedule;
import static org.mat.samples.mongodb.listener.SchedulerListener.unSchedule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
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

	public static DBCollection giveCollection() {
		DB db = MongoListener.getMongoDB();
		return db.getCollection(SCHEDULER_CONFIG_COLLECTION);
	}

	/**
	 * check if index exist for collection scheduler-config
	 */
	public static void checkIndex() {
		
		DBCollection coll = giveCollection();
		
		boolean exist = false;
		
		List<DBObject> indexes = coll.getIndexInfo();
		if ((null != indexes) && (!indexes.isEmpty())) {
			for (DBObject dbIndex : indexes) {
				if (dbIndex.containsField("applicationName") 
				 && dbIndex.containsField("serverName")
				 && dbIndex.containsField("asName")) {
					exist = true;
					break;
				}
			}
		}
		
		if (!exist) {
			BasicDBObject dbIndex = new BasicDBObject();
			
			// indexex field is ascending (1) or descending (-1)
			dbIndex.append("applicationName", 1);
			dbIndex.append("serverName", 1);
			dbIndex.append("asName", 1);
			
			// unique index
			dbIndex.append("unique", true);
			// remove duplicated keys
			dbIndex.append("dropDups", true);
						
			//coll.createIndex(dbIndex);  //
			coll.ensureIndex(dbIndex);
		}
		
	}
	
	// launch Schedulers as jobs
	public static void initSchedulers() throws SchedulerException {
		
		// launch the schedulers
		List<Scheduler> schedulers = listSchedulers();
		for (Scheduler scheduler : schedulers) {
			if (Constants.STATUS_RUNNING.equals(scheduler.getInitialState())) {
				logger.info("Schedule " + scheduler.toString());
				schedule(scheduler);
			}
		}
	}

	/**
	 * find Scheduler By Id
	 * 
	 * @param schedulerId
	 *            id of the scheduler to get back
	 * @return Scheduler
	 */
	public static Scheduler findSchedulerById(String schedulerId) {
		Scheduler scheduler = null;
		DBCollection collection = giveCollection();
		DBObject filter = new BasicDBObject();

		filter.put("_id", new ObjectId(schedulerId));
		DBObject obj = collection.findOne(filter);
		try {
			scheduler = wrapDBObjectToScheduler(obj);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		} catch (InvocationTargetException e) {
			logger.error("IllegalAccessException", e);
		}
		return scheduler;
	}

	/**
	 * Delete Scheduler By Id
	 * 
	 * @param schedulerId
	 *            scheduler identifier to delete
	 * @return boolean
	 * @throws SchedulerException 
	 */
	public static boolean deleteSchedulerById(String schedulerId) throws SchedulerException {

		Scheduler oldScheduler = findSchedulerById(schedulerId);
		
		logger.info(String.format("deleteSchedulerById %s", schedulerId));
		DBCollection collection = giveCollection();
		DBObject filter = new BasicDBObject();

		filter.put("_id", new ObjectId(schedulerId));
		DBObject obj = collection.findOne(filter);
		WriteResult result = collection.remove(obj);
		
		boolean success = (result.getN() > 0);
		if (success) {
			unSchedule(oldScheduler);
		}
		return success;
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
				Scheduler scheduler = wrapDBObjectToScheduler(cursor.next());
				schedulers.add(scheduler);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return schedulers;
	}

	private static Scheduler wrapDBObjectToScheduler(DBObject dbObj)
			throws IllegalAccessException, InvocationTargetException {
		Map<?, ?> map = dbObj.toMap();
		Scheduler scheduler = new Scheduler();
		BeanUtils.populate(scheduler, map);
		scheduler.setSchedulerId(map.get(ID_FIELD).toString());
		return scheduler;
	}

	/**
	 * Store a scheduler definition into the dataStore
	 * 
	 * @param scheduler
	 *            schedulerData
	 * @return the new scheduler Id
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public static String addScheduler(Scheduler scheduler) throws IOException,
			SchedulerException {

		DBCollection collection = giveCollection();

		String jsonString = mapper.writeValueAsString(scheduler);
		DBObject doc = (DBObject) JSON.parse(jsonString);

		String id = null;

		WriteResult result = collection.insert(doc);
		int success = result.getN();
		
		if (success > 0) {
			logger.info(String.format("Creation of  %s.", scheduler.toString()));
	
			if (doc.containsField(ID_FIELD)) {
				id = String.valueOf(doc.get(ID_FIELD));
				scheduler.setSchedulerId(id);
			}
	
			if (Constants.STATUS_RUNNING.equals(scheduler.getInitialState())
					|| Constants.STATUS_RUNNING.equals(scheduler.getState())) {
				logger.info("Schedule " + scheduler.toString());
				schedule(scheduler);
			}
		}
		
		return id;
	}

	/**
	 * Update the scheduler
	 * 
	 * @param schedulerId
	 *            if of the scheduler to update
	 * @param scheduler
	 *            scheduler data
	 * @return success or not
	 * @throws IOException
	 * @throws SchedulerException 
	 */
	public static boolean updateScheduler(String schedulerId,
			Scheduler scheduler) throws IOException, SchedulerException {

		Scheduler oldScheduler = findSchedulerById(schedulerId);

		scheduler.setSchedulerId(schedulerId);

		DBCollection collection = giveCollection();

		String jsonString = mapper.writeValueAsString(scheduler);
		DBObject doc = (DBObject) JSON.parse(jsonString);

		doc.put("_id", new ObjectId(schedulerId));

		WriteResult result = collection.save(doc);
		logger.info(String.format("Update scheduler   %s.", scheduler));
		boolean res = (result.getN() > 0);

		if (res) {
			unSchedule(oldScheduler);
			if (Constants.STATUS_RUNNING.equals(scheduler.getInitialState())
					|| Constants.STATUS_RUNNING.equals(scheduler.getState())) {
				logger.info("Schedule " + scheduler.toString());
				schedule(scheduler);
			}
		}

		return res;
	}

	

}
