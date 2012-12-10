package org.mat.samples.mongodb;

/**
 * User: E010925
 * Date: 27/11/12
 * Time: 14:50
 */
public interface Constants {


    public static final String MONGO_SERVER = "dun-tst-devf01"; //"dun-tst-devf01";
    public static final int MONGO_PORT = 27017;
    public static final String MONGO_DB = "mydb";

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String SCHEDULER_CONFIG_COLLECTION = "scheduler-config";
    public static final String SYSTEM_INDEXES_COLLECTION = "system.indexes";

    public static final String MEM_FREE = "free_memory";
    public static final String MEM_MAX = "max_memory";
    public static final String MEM_TOTAL = "total_memory";
    public static final String MEM_AVAILABLE = "available_memory";
    public static final String THREADS = "total_threads";

    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_STOPPED = "stopped";

}
