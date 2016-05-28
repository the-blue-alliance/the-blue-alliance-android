package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.APIStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class APIStatusDeserializerTest {

    private static final String API_STATUS_FILE = "api_status";

    @Mock JsonDeserializationContext mContext;

    private JsonObject mJsonData;
    private APIStatus mStatus;
    private APIStatusDeserializer mDeserializer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mJsonData = ModelMaker.getModel(JsonObject.class, API_STATUS_FILE);
        mDeserializer = new APIStatusDeserializer();
    }

    @Test
    public void testApiStatus() {
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);

        assertNotNull(mStatus);
        assertEquals(mStatus.getMaxSeason(), 2015);
        assertEquals(mStatus.getMinAppVersion(), 123456);
        assertEquals(mStatus.getLatestAppersion(), 123488);
        assertEquals(mStatus.isFmsApiDown(), false);
        assertEquals(mStatus.getJsonBlob(), mJsonData.toString());
        assertTrue(mStatus.hasMessage());
        assertEquals("This is only a test", mStatus.getMessageText());
        assertEquals(new Date(1449698422L * 1000), mStatus.getMessageExipration());
        assertEquals(1449698422, mStatus.getLastOkHttpCacheClear());
        assertEquals("https://www.thebluealliance.com", mStatus.getChampsPitLocationsUrl());
        assertEquals(12345678, mStatus.getChampsPitLocationsUpdateTime());

        List<String> downKeys = mStatus.getDownEvents();
        assertNotNull(downKeys);
        assertEquals(downKeys.size(), 2);
        assertEquals(downKeys.get(0), "2015test");
        assertEquals(downKeys.get(1), "2015down");
    }

    @Test(expected = JsonParseException.class)
    public void testWrongType() {
        JsonArray badData = new JsonArray();
        mStatus = mDeserializer.deserialize(badData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMaxSeason() {
        mJsonData.remove(APIStatusDeserializer.MAX_SEASON_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadMaxSeason() {
        mJsonData.add(APIStatusDeserializer.MAX_SEASON_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoApiStatus() {
        mJsonData.remove(APIStatusDeserializer.FMS_API_DOWN_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadApiStatus() {
        mJsonData.add(APIStatusDeserializer.FMS_API_DOWN_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoDownEvents() {
        mJsonData.remove(APIStatusDeserializer.DOWN_EVENTS_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadDownEvents() {
        mJsonData.addProperty(APIStatusDeserializer.DOWN_EVENTS_TAG, "hello");
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoAndroidSettings() {
        mJsonData.remove(APIStatusDeserializer.ANDROID_SETTINGS_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadAndroidSettings() {
        mJsonData.addProperty(APIStatusDeserializer.ANDROID_SETTINGS_TAG, "hello");
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMinVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .remove(APIStatusDeserializer.MIN_APP_VERSION_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadMinVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .add(APIStatusDeserializer.MIN_APP_VERSION_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoLatestVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .remove(APIStatusDeserializer.LATEST_APP_VERSION_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadLatestVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .add(APIStatusDeserializer.LATEST_APP_VERSION_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test
    public void testNoMessage() {
        mJsonData.remove(APIStatusDeserializer.MESSAGE_DICT);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
        assertFalse(mStatus.hasMessage());
    }

    @Test(expected = JsonParseException.class)
    public void testNoMessageText() {
        mJsonData.get(APIStatusDeserializer.MESSAGE_DICT).getAsJsonObject()
          .remove(APIStatusDeserializer.MESSAGE_TEXT);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMessageExpiration() {
        mJsonData.get(APIStatusDeserializer.MESSAGE_DICT).getAsJsonObject()
          .remove(APIStatusDeserializer.MESSAGE_EXPIRATION);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
    }

    @Test
    public void testNoLastCacheClear() {
        mJsonData.remove(APIStatusDeserializer.LAST_OKHTTP_CACHE_CLEAR);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
        assertEquals(-1, mStatus.getLastOkHttpCacheClear());
    }

    @Test
    public void testNoChampsPitLocationsUrl() {
        mJsonData.remove(APIStatusDeserializer.CHAMPS_PIT_LOCATIONS_URL);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
        assertEquals(null, mStatus.getChampsPitLocationsUrl());
    }

    @Test
    public void testNoChampsPitLocationsUpdateTime() {
        mJsonData.remove(APIStatusDeserializer.CHAMPS_PIT_LOCATIONS_UPDATE_TIME);
        mStatus = mDeserializer.deserialize(mJsonData, APIStatus.class, mContext);
        assertEquals(-1, mStatus.getChampsPitLocationsUpdateTime());
    }
}