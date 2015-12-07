package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class JsonArrayAndKeyCombinerTest {

    private JsonArray mRankings;
    private JsonArrayAndKeyCombiner mCombiner;

    @Before
    public void setUp() {
        mRankings = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        mCombiner = new JsonArrayAndKeyCombiner("2015necmp");
    }

    @Test
    public void testCombiner() {
        KeyAndJson result = mCombiner.call(mRankings);

        assertNotNull(result);
        assertEquals(result.key, "2015necmp");
        assertNotNull(result.json);
        assertTrue(result.json.isJsonArray());
        assertEquals(mRankings, result.json);
    }

}