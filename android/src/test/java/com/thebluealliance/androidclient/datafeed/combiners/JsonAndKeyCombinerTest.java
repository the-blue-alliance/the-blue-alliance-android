package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class JsonAndKeyCombinerTest {

    private JsonObject mStats;
    private JsonAndKeyCombiner mCombiner;

    @Before
    public void setUp() {
        mStats = ModelMaker.getModel(JsonObject.class, "2015necmp_stats");
        mCombiner = new JsonAndKeyCombiner("2015necmp");
    }

    @Test
    public void testCombiner() {
        KeyAndJson result = mCombiner.call(mStats);

        assertNotNull(result);
        assertEquals(result.key, "2015necmp");
        assertNotNull(result.json);
        assertTrue(result.json.isJsonObject());
        assertEquals(mStats, result.json);
    }
}