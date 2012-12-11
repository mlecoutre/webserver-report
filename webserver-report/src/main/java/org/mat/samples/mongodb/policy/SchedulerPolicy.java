package org.mat.samples.mongodb.policy;

import static org.mat.samples.mongodb.listener.SchedulerListener.schedule;
import static org.mat.samples.mongodb.listener.SchedulerListener.unSchedule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
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

        if (exist) {
            BasicDBObject dbIndex = new BasicDBObject();

            // indexex field is ascending (1) or descending (-1)
            dbIndex.append("applicationName", 1);
            dbIndex.append("serverName", 1);
            dbIndex.append("asName", 1);

            // unique index
            dbIndex.append("unique", true);
            // remove duplicated keys
            dbIndex.append("dropDups", true);

            // coll.createIndex(dbIndex); //
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
     * @throws SchedulerException
     */
    public static boolean deleteSchedulerById(String schedulerId)
            throws SchedulerException {

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
        scheduler.setSchedulerId(map.get("_id").toString());
        scheduler.setApplicationName((String) map.get("applicationName"));
        scheduler.setServerName((String) map.get("serverName"));
        scheduler.setAsName((String) map.get("asName"));
        scheduler.setEndPointURL((String) map.get("endPointURL"));
        scheduler.setState((String) map.get("state"));
        scheduler.setLastStatus((String) map.get("lastStatus"));
        scheduler.setInitialState((String) map.get("initialState"));
        Object requestRepeatIntervalInMinutes = map.get("requestRepeatIntervalInMinutes");
        if (requestRepeatIntervalInMinutes != null)
            scheduler.setRequestRepeatIntervalInMinutes(new Integer(requestRepeatIntervalInMinutes.toString()));
        Object maxHistoryToKeepInDays = map.get("maxHistoryToKeepInDays");
        if (maxHistoryToKeepInDays != null)
            scheduler.setMaxHistoryToKeepInDays(new Integer(maxHistoryToKeepInDays.toString()));
        Object obj = map.get("lastExecution");

//        scheduler.setLastExecution(d);

        //BeanUtils.populate(scheduler, map);
        //scheduler.setSchedulerId(map.get(ID_FIELD).toString());
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
    public static String addScheduler(Scheduler scheduler) throws IOException,
            SchedulerException {

        DBCollection collection = giveCollection();
        scheduler.setLastExecution(new Date());

        String jsonString = mapper.writeValueAsString(scheduler);
        DBObject doc = (DBObject) JSON.parse(jsonString);

        String id = null;

        WriteResult result = collection.insert(doc);

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

        return id;
    }

    /**
     * Stop the scheduler schedulerId
     *
     * @param schedulerId id of the scheduler to stop
     * @return boolean value
     * @throws IOException
     * @throws SchedulerException
     */
    public static boolean stopScheduler(String schedulerId) throws IOException,
            SchedulerException {

        boolean res = true;

        Scheduler scheduler = findSchedulerById(schedulerId);
        if (null != scheduler) {
            scheduler.setSchedulerId(schedulerId);
            scheduler.setState(STATUS_STOPPED);
            res = updateScheduler(schedulerId, scheduler);
        }

        return res;
    }

    /**
     * start the scheduler schedulerId
     *
     * @param schedulerId id of the scheduler to start
     * @return boolean value
     * @throws IOException
     * @throws SchedulerException
     */
    public static boolean startScheduler(String schedulerId)
            throws IOException, SchedulerException {

        boolean res = true;

        Scheduler scheduler = findSchedulerById(schedulerId);
        if (null != scheduler) {
            scheduler.setSchedulerId(schedulerId);
            scheduler.setState(STATUS_RUNNING);
            res = updateScheduler(schedulerId, scheduler);
        }

        return res;
    }

    /**
     * Update the scheduler
     *
     * @param schedulerId if of the scheduler to update
     * @param scheduler   scheduler data
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

        collection.save(doc);
        logger.info(String.format("Update scheduler   %s.", scheduler));

        unSchedule(oldScheduler);
        if (Constants.STATUS_RUNNING.equals(scheduler.getInitialState())
                || Constants.STATUS_RUNNING.equals(scheduler.getState())) {
            logger.info("Schedule " + scheduler.toString());
            schedule(scheduler);
        }


        return true;
    }

    /**
     * Update lastExecution and lastStatus for scheduler schedulerId
     *
     * @param lastExecution timestamp of the last execution
     * @param lastStatus    last status message
     * @return boolean value
     */
    public static boolean updateSchedulerStatus(String schedulerId,
                                                Date lastExecution, String lastStatus) {

        DBCollection collection = giveCollection();
        DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(schedulerId));
        DBObject doc = new BasicDBObject();
        doc.put("lastExecution", lastExecution);
        doc.put("lastStatus", lastStatus);

        collection.update(query, doc);

        logger.info(String.format("Update lastExecution and lastStatus for scheduler %s.", schedulerId));
        return true;

    }

}
