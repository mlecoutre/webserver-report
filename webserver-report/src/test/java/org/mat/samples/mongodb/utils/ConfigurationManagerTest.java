package org.mat.samples.mongodb.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: E010925
 * Date: 12/12/12
 * Time: 17:51
 */
public class ConfigurationManagerTest {

    @Test
    public void testGiveProperty() {
        String res = ConfigurationManager.giveProperty("datastore.host");
        assertTrue("DataStore.host should be equals to 'dun-tst-devf01' in configuration.properties", "dun-tst-devf01".equals(res));

    }

    @Test
    public void testGivePropertyAsInt() {
        int res = ConfigurationManager.givePropertyAsInt("purgeHistoryHour");
        assertTrue("purgeHistoryHour should an int", 23 == res);

    }
}
