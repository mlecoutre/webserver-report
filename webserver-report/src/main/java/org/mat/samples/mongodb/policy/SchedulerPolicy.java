package org.mat.samples.mongodb.policy;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.listener.MongoListener;
import org.mat.samples.mongodb.vo.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mat.samples.mongodb.listener.SchedulerListener.schedule;

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
     * Schedule the job if required
     *
     * @param scheduler scheduler data
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

    /**
     * find Scheduler By Id
     *
     * @param schedulerId id of the scheduler to get back
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
     * @param schedulerId scheduler identifier to delete
     * @return boolean
     */
    public static boolean deleteSchedulerById(String schedulerId) {
        logger.info(String.format("deleteSchedulerById %s", schedulerId));
        DBCollection collection = giveCollection();
        DBObject filter = new BasicDBObject();

        filter.put("_id", new ObjectId(schedulerId));
        DBObject obj = collection.findOne(filter);
        collection.remove(obj);
        return true;
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

    private static Scheduler wrapDBObjectToScheduler(DBObject dbObj) throws IllegalAccessException, InvocationTargetException {
        Map<?, ?> map = dbObj.toMap();
        Scheduler scheduler = new Scheduler();
        BeanUtils.populate(scheduler, map);
        scheduler.setSchedulerId(map.get(ID_FIELD).toString());
        return scheduler;
    }

    /**
     * Store a scheduler definition into the dataStore
     *
     * @param scheduler schedulerData
     * @return the new scheduler Id
     * @throws IOException
     * @throws SchedulerException
     */
    public static String addScheduler(Scheduler scheduler)
            throws IOException,
            SchedulerException {

        DBCollection collection = giveCollection();

        String jsonString = mapper.writeValueAsString(scheduler);
        DBObject doc = (DBObject) JSON.parse(jsonString);

        String id = null;

        collection.insert(doc);
        logger.info(String.format("Creation of  %s.", scheduler.toString()));

        if (doc.containsField(ID_FIELD)) {
            id = String.valueOf(doc.get(ID_FIELD));
            scheduler.setSchedulerId(id);
        }
        scheduleIfNeeded(scheduler);

        return id;
    }

    /**
     * Update the scheduler
     *
     * @param schedulerId if of the scheduler to update
     * @param scheduler   scheduler data
     * @return success or not
     * @throws IOException
     */
    public static boolean updateScheduler(String schedulerId,
                                          Scheduler scheduler) throws IOException {

        scheduler.setSchedulerId(schedulerId);

        DBCollection collection = giveCollection();

        String jsonString = mapper.writeValueAsString(scheduler);
        DBObject doc = (DBObject) JSON.parse(jsonString);

        WriteResult result = collection.save(doc);
        logger.info(String.format("Update scheduler   %s.", scheduler));
        return result.getN() > 0;
    }

}
