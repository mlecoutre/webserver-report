package am.projects.webserver.report.utils;

import am.projects.webserver.report.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: E010925
 * Date: 12/12/12
 * Time: 17:46
 */
public class ConfigurationManager {


    private static Properties configuration = null;

    private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    public static String giveProperty(String key) {
        if (init()) return null;
        return configuration.getProperty(key);
    }

    public static Integer givePropertyAsInt(String key) {
        if (init()) return null;
        return new Integer(configuration.getProperty(key));
    }

    private static boolean init() {
        if (configuration == null) {
            configuration = new Properties();

            InputStream stream = ConfigurationManager.class.getResourceAsStream(Constants.CONFIGURATION_PROPERTIES);
            try {
                configuration.load(stream);
               String env =  configuration.getProperty("application.env");
                logger.info(String.format("Load configuration properties for [%s] environment", env));
            } catch (IOException e) {
                logger.error("Error trying to initialize configuration manager", e);
                return true;
            }
        }
        return false;
    }

}
