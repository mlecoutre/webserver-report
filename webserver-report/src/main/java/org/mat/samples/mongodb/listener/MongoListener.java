package org.mat.samples.mongodb.listener;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.mat.samples.mongodb.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initialize EntityManager Factory
 * For Heroku, try to find the environment property DATABASE_URL, and transform
 * it into a valid jdbc URL to initialize properly the DB.
 *
 * @author mlecoutre
 *         <p/>
 *         TODO Check best practices for pooling or connection sharing
 */
@WebListener
public class MongoListener implements ServletContextListener, Constants {

    private Logger logger = LoggerFactory.getLogger(MongoListener.class);


    private static DB mongoDB;
    private static Mongo mongo;

    private String server;
    private int port;
    private String db;


    public MongoListener() {
        logger.info("Initialize connection to mongo DB");
        this.db = MONGO_DB;
        this.server = MONGO_SERVER;
        this.port = MONGO_PORT;
    }

    public MongoListener(String server, int port, String db) {
        logger.info("Initialize connection to Mongo DB with custom parameters");
        this.server = server;
        this.port = port;
        this.db = db;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            mongo = new Mongo(server, port);
            mongoDB = mongo.getDB(db);
        } catch (Exception e) {
            logger.error("Mongo Listener not correctly initialized", e);
        }
    }

    /**
     * Close the entity manager
     *
     * @param event ServletContextEvent not used
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        mongo.close();
    }

    public static DB getMongoDB() {
        return mongoDB;
    }
}