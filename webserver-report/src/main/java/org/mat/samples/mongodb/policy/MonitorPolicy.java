package org.mat.samples.mongodb.policy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mat.samples.mongodb.Constants;
import org.mat.samples.mongodb.listener.MongoListener;
import org.mat.samples.mongodb.vo.ApplicationStats;
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
 * MonitorPolicy
 *
 * @author mlecoutre
 */
public class MonitorPolicy implements Constants {

    private static Logger logger = LoggerFactory.getLogger(MonitorPolicy.class);

    /**
     * Check if index exist for collection
     *
     * @param collection collection to update
     */
    public static void checkIndex(DBCollection collection) {
        List<DBObject> indexes = collection.getIndexInfo();

        if (indexes == null || indexes.size() <= 1) {
            logger.info("Create indexes on the collection " + collection.getName());
            BasicDBObject dbIndex = new BasicDBObject();

            // index field is ascending (1) or descending (-1)
            dbIndex.append("timestamp", 1);
            dbIndex.append("applicationName", 1);
            dbIndex.append("serverName", 1);
            dbIndex.append("asName", 1);
            dbIndex.append("type", 1);

            // not unique index
            dbIndex.append("unique", false);
            // remove duplicated keys
            //  dbIndex.append("dropDups", true);
            collection.ensureIndex(dbIndex);
        }

    }

    /**
     * List DataSources
     *
     * @param applicationName Mongo collection to request
     * @param serverName      server name
     * @param asName          AS Name
     * @return List of dataSources
     */
    public static List<String> listDataSources(String applicationName, String serverName, String asName) {
        DB db = MongoListener.getMongoDB();
        DBCollection collection = db.getCollection(applicationName);
        BasicDBObject filter = new BasicDBObject();
        filter.put("type", "was.pool.ds");

        if (serverName != null)
            filter.put("server", serverName);

        if (asName != null)
            filter.put("asName", asName);

        List<String> mList = collection.distinct("id", filter);
        logger.info("listDataSources: " + mList.size());
        return mList;
    }

    /**
     * List QCFs
     *
     * @param applicationName Mongo collection to request
     * @param serverName      server name
     * @param asName          AS Name
     * @return List of dataSources
     */
    public static List<String> listQCFs(String applicationName, String serverName, String asName) {
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);
        BasicDBObject filter = new BasicDBObject();
        filter.put("type","was.pool.qcf");

        if (serverName != null)
            filter.put("server", serverName);

        if (asName != null)
            filter.put("asName", asName);

        List mList = coll.distinct("id", filter);
        logger.info("listQCFs: " + mList.size());
        return mList;
    }


    /**
     * List Application Server
     *
     * @param applicationName Mongo collection to request
     * @return List of dataSources
     */
    public static List<String> listASs(String applicationName) {
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);

        List mList = coll.distinct("asName");
        logger.info("listASs: " + mList.size());
        return mList;
    }

    /**
     * List Application Server
     *
     * @param applicationName Mongo collection to request
     * @return List of dataSources
     */
    public static List<String> listServers(String applicationName) {
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);

        List<String> mList = coll.distinct("server");
        logger.info("listServers: " + mList.size());
        return mList;
    }

    /**
     * return list of the applications that have some data
     *
     * @return Set of Mongo Collection
     */
    public static Set<String> listApplications() {
        DB db = MongoListener.getMongoDB();
        Set<String> collections = db.getCollectionNames();
        //remove the technical system.indexes from the list of applications;
        collections.remove(SYSTEM_INDEXES_COLLECTION);
        collections.remove(SCHEDULER_CONFIG_COLLECTION);
        return collections;
    }

    /**
     * Write directly into the output stream the JS array for chart input data
     *
     * @param memory          type of memory @see org.mat.samples.mongodb.Constants
     * @param applicationName Mongo collection to be requested.
     * @param serverName      server Name
     * @param asName          Application server name
     * @param startDate       start interval
     * @param endDate         end interval
     * @param writer          output writer
     * @throws Exception return all error
     *                   <p/>
     *                   TODO manage cache or local file storage to avoid to call DB for each request.
     */
    public static void requestMemory(String memory, String applicationName, String serverName, String asName, Date startDate, Date endDate, OutputStream writer) throws Exception {


        DB db = MongoListener.getMongoDB();
        DBCollection collection = db.getCollection(applicationName);

        //Create index  if needed
        checkIndex(collection);

        BasicDBObject fields = new BasicDBObject();
        fields.put("sizemb", 1);
        fields.put("timestamp", 1);
        fields.put("server", 1);
        fields.put("_id", 0);

        BasicDBObject filter = new BasicDBObject();
        filter.put("type", "memory");
        filter.put("id", memory);
        if (serverName != null) {
            filter.put("server", serverName);
        }
        if (asName != null) {
            filter.put("asName", asName);
        }

        if (startDate != null && endDate != null)
            filter.put("timestamp", new BasicDBObject("$gte", startDate).append("$lte", endDate));

        BasicDBObject sortDBO = new BasicDBObject();
        sortDBO.put("timestamp", "-1");
        DBCursor cursor = collection.find(filter, fields).sort(sortDBO);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
            writer.write("[\n".getBytes());

            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                Date timestamp = (Date) obj.get("timestamp");
                long milli = timestamp.getTime();
                String size = (String) obj.get("sizemb");
                writer.write(String.format("[%s, %s]", milli, size).getBytes());
                if (cursor.hasNext())
                    writer.write(", \n".getBytes());
            }
            writer.write("]\n".getBytes());
        } finally {
            cursor.close();
        }
    }

    /**
     * Clear content of the collection
     *
     * @param applicationName Mongo collection to purge
     * @throws Exception
     */
    public static void purgeDB(String applicationName) throws Exception {
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);
        logger.info("Nb elements before: " + coll.count());
        //db.dropDatabase();
        coll.drop();
        logger.info("Nb elements after: " + coll.count());
    }

    /**
     * Purge data which are oldest than oldestDate
     *
     * @param applicationName application alias name
     * @param serverName      physical server name
     * @param asName          AS name
     * @param oldestDate      max date
     * @throws Exception
     */
    public static void purgeHistory(String applicationName, String serverName, String asName, String oldestDate) throws Exception {
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);

        BasicDBObject filter = new BasicDBObject();
        filter.put("server", serverName);
        filter.put("asName", asName);
        filter.append("timestamp",
                new BasicDBObject("$lt", oldestDate)
        );

        WriteResult result = coll.remove(filter);

        int count = result.getN();
        logger.info("Nb elements removed: " + count);
    }


    /**
     * Request statistics
     *
     * @param applicationName MongoDB collection to request
     * @return Statistics on data stored for the application
     */
    public static ApplicationStats requestStats(String applicationName) {
        ApplicationStats stats = new ApplicationStats(applicationName);
        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);
        stats.setNbElements(coll.count());
        stats.setDataSources(listDataSources(applicationName, null, null));
        stats.setAss(listASs(applicationName));
        stats.setServers(listServers(applicationName));
        /*  BasicDBObject fields = new BasicDBObject();
     fields.put("timestamp", 1);
     coll.findOne(fields);
     BasicDBObject sortDBO = new BasicDBObject();
     sortDBO.put("timestamp", "-1");
     DBCursor cursor = coll.findOne(fields).sort(sortDBO);*/
        return stats;
    }

    /**
     * Request Total Threads
     *
     * @param applicationName Mongo collection to request
     * @param serverName      filter on serverName (can be null)
     * @param asName          filter on asName (can be null)
     * @param startDate       start interval
     * @param endDate         end interval
     * @param writer          output writer
     * @throws Exception
     */
    public static void requestTotalThreads(String applicationName, String serverName, String asName, Date startDate, Date endDate, OutputStream writer) throws Exception {

        DB db = MongoListener.getMongoDB();
        DBCollection collection = db.getCollection(applicationName);

        //Create index  if needed
        checkIndex(collection);

        BasicDBObject fields = new BasicDBObject();
        fields.put("count", 1);
        fields.put("timestamp", 1);
        fields.put("server", 1);
        fields.put("_id", 0);

        BasicDBObject filter = new BasicDBObject();
        filter.put("id", THREADS);
        if (serverName != null) {
            filter.put("server", serverName);
        }
        if (asName != null) {
            filter.put("asName", asName);
        }
        if (startDate != null && endDate != null)
            filter.put("timestamp", new BasicDBObject("$gte", startDate).append("$lte", endDate));


        BasicDBObject sortDBO = new BasicDBObject();
        sortDBO.put("timestamp", "-1");
        DBCursor cursor = collection.find(filter, fields).sort(sortDBO);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
            writer.write("[\n".getBytes());
            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                Date timestamp = (Date) obj.get("timestamp");
                long milli = timestamp.getTime();
                String used = (String) obj.get("count");
                writer.write(String.format("[%s, %s]", milli, used).getBytes());
                if (cursor.hasNext())
                    writer.write(", \n".getBytes());
            }
            writer.write("]\n".getBytes());
        } catch (Exception e) {
            logger.error("Error while writing  total threads on the output stream", e);
        } finally {
            cursor.close();
        }
    }

    /**
     * Write the used connection of a specific datasource and QCF on an output stream
     *
     * @param idObject        name of the dataSource
     * @param applicationName Mongo Collection to request
     * @param serverName      add filter on server (can be null)
     * @param asName          add filter on seName (can be null)
     * @param startDate       start interval
     * @param endDate         end interval
     * @param writer          output stream writer
     * @throws Exception TODO manage cache or local file storage to avoid to call DB for each request.
     */
    public static void requestUsedConnection(String idObject, String applicationName, String serverName, String asName, Date startDate, Date endDate, OutputStream writer) throws Exception {

        DB db = MongoListener.getMongoDB();
        DBCollection coll = db.getCollection(applicationName);

        BasicDBObject fields = new BasicDBObject();
        fields.put("used", 1);
        fields.put("timestamp", 1);
        fields.put("server", 1);
        fields.put("_id", 0);

        BasicDBObject filter = new BasicDBObject();
        filter.put("id", idObject);
        if (serverName != null) {
            filter.put("server", serverName);
        }
        if (asName != null) {
            filter.put("asName", asName);
        }
        if (startDate != null && endDate != null)
            filter.put("timestamp", new BasicDBObject("$gte", startDate).append("$lte", endDate));

        BasicDBObject sortDBO = new BasicDBObject();
        sortDBO.put("timestamp", "-1");
        DBCursor cursor = coll.find(filter, fields).sort(sortDBO);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
            writer.write("[\n".getBytes());
            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                Date timestamp = (Date) obj.get("timestamp");
                long milli = timestamp.getTime();
                String used = (String) obj.get("used");
                writer.write(String.format("[%s, %s]", milli, used).getBytes());
                if (cursor.hasNext())
                    writer.write(", \n".getBytes());
            }
            writer.write("]\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    /**
     * Batch load of J2EE Resources
     *
     * @param strUrl          file URL to load
     * @param applicationName is used to gather all AS from a same application. One mongo collection per applicationName
     * @param serverName      server Name
     * @param asName          AS name
     * @throws IOException
     */
    public static long batchInsert(String strUrl, String applicationName, String serverName, String asName) throws IOException {
        long nbElts = 0;
        BufferedReader bufferedReader = null;
        try {
            URL u = new URL(strUrl);
            URLConnection yc = u.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String line = null;
            DB db = MongoListener.getMongoDB();
            DBCollection collection = db.getCollection(applicationName);
            //Create index  if needed
            checkIndex(collection);
            long before = collection.count();
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
            while ((line = bufferedReader.readLine()) != null) {

                DBObject doc = (DBObject) JSON.parse(line);
                //manage timestamp as date, not as string.
                String ts = (String) doc.get("timestamp");
                Date eventTimestamp = null;
                try {
                    eventTimestamp = sdf.parse(ts);
                } catch (ParseException pe) {
                    logger.error("Error parsing date", pe);
                }
                doc.put("timestamp", eventTimestamp);
                doc.put("server", serverName);
                doc.put("asName", asName);
                collection.insert(doc);
            }
            nbElts = collection.count() - before;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    logger.error("Error trying to close the buffered reader", e);
                }
            }
        }
        return nbElts;
    }

    /**
     * Temporary method to update the model for timestamp modification
     * @param applicationName
     * @return
     */
    public static boolean updateModel(String applicationName) {
        DB db = MongoListener.getMongoDB();
        DBCollection collection = db.getCollection(applicationName);
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Date eventTimestamp = null;
            try {
                String ts = (String) obj.get("timestamp");
                eventTimestamp = sdf.parse(ts);
            } catch (Exception e) {
                logger.error("Error parsing date, maybe, it's already a date format: " + e.getMessage());
                continue;
            }

            DBObject query = new BasicDBObject().append("_id", obj.get("_id"));

            DBObject doc = new BasicDBObject().append("$set", new BasicDBObject()
                    .append("timestamp", eventTimestamp)

            );
            logger.info(">>>" + eventTimestamp);

            collection.update(query, doc);
            obj.put("timestamp", eventTimestamp);
        }   // end loop
        logger.info("Process ended for " + applicationName);
        return true;


    }


    /**
     * Temporary method to update the model for timestamp modification
     * @param applicationName
     * @return
     */
    public static boolean updateType(String applicationName) {
        DB db = MongoListener.getMongoDB();
        DBCollection collection = db.getCollection(applicationName);
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Date eventTimestamp = null;
            try {
                String ts = (String) obj.get("timestamp");
                eventTimestamp = sdf.parse(ts);
            } catch (Exception e) {
                logger.error("Error parsing date, maybe, it's already a date format: " + e.getMessage());
                continue;
            }

            DBObject query = new BasicDBObject().append("_id", obj.get("_id"));

            DBObject doc = new BasicDBObject().append("$set", new BasicDBObject()
                    .append("timestamp", eventTimestamp)

            );
            logger.info(">>>" + eventTimestamp);

            collection.update(query, doc);
            obj.put("timestamp", eventTimestamp);
        }   // end loop
        logger.info("Process ended for " + applicationName);
        return true;


    }
}
