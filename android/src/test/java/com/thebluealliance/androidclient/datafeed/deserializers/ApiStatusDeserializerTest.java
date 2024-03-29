package com.thebluealliance.androidclient.datafeed.deserializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.ApiStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ApiStatusDeserializerTest {

    private static final String API_STATUS_FILE = "api_status";

    @Mock JsonDeserializationContext mContext;

    private JsonObject mJsonData;
    private ApiStatus mStatus;
    private APIStatusDeserializer mDeserializer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mJsonData = ModelMaker.getModel(JsonObject.class, API_STATUS_FILE);
        mDeserializer = new APIStatusDeserializer();
    }

    @Test
    public void testApiStatus() {
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);

        assertNotNull(mStatus);
        assertNotNull(mStatus.getMaxSeason());
        assertEquals(mStatus.getMaxSeason().intValue(), 2015);
        assertNotNull(mStatus.getMinAppVersion());
        assertEquals(mStatus.getMinAppVersion().intValue(), 123456);
        assertNotNull(mStatus.getLatestAppVersion());
        assertEquals(mStatus.getLatestAppVersion().intValue(), 123488);
        assertEquals(mStatus.getFmsApiDown(), false);
        assertEquals(mStatus.getJsonBlob(), mJsonData.toString());
        assertNotNull(mStatus.getHasMessage());
        assertTrue(mStatus.getHasMessage());
        assertEquals("This is only a test", mStatus.getMessageText());
        assertNotNull(mStatus.getMessageExpiration());
        assertEquals(1449698422L * 1000, mStatus.getMessageExpiration().longValue());
        assertNotNull(mStatus.getLastOkHttpCacheClear());
        assertEquals(1449698422, mStatus.getLastOkHttpCacheClear().longValue());

        List<String> downKeys = mStatus.getDownEvents();
        assertNotNull(downKeys);
        assertEquals(downKeys.size(), 2);
        assertEquals(downKeys.get(0), "2015test");
        assertEquals(downKeys.get(1), "2015down");
    }

    @Test(expected = JsonParseException.class)
    public void testWrongType() {
        JsonArray badData = new JsonArray();
        mStatus = mDeserializer.deserialize(badData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMaxSeason() {
        mJsonData.remove(APIStatusDeserializer.MAX_SEASON_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadMaxSeason() {
        mJsonData.add(APIStatusDeserializer.MAX_SEASON_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test()
    public void testLowerMaxSeason() {
        // Make sure maxSeason >= currentSeason
        mJsonData.addProperty(APIStatusDeserializer.MAX_SEASON_TAG, 2013);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
        assertEquals(mStatus.getCurrentSeason(), mStatus.getMaxSeason());
    }

    @Test
    public void testNoCurrentSeason() {
        mJsonData.remove(APIStatusDeserializer.CURRENT_SEASON_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        assertNotNull(mStatus.getCurrentSeason());
        assertEquals(currentYear, mStatus.getCurrentSeason().intValue());
    }

    @Test
    public void testBadCurrentSeason() {
        mJsonData.add(APIStatusDeserializer.CURRENT_SEASON_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        assertNotNull(mStatus.getCurrentSeason());
        assertEquals(currentYear, mStatus.getCurrentSeason().intValue());
    }

    @Test(expected = JsonParseException.class)
    public void testNoApiStatus() {
        mJsonData.remove(APIStatusDeserializer.FMS_API_DOWN_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadApiStatus() {
        mJsonData.add(APIStatusDeserializer.FMS_API_DOWN_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoDownEvents() {
        mJsonData.remove(APIStatusDeserializer.DOWN_EVENTS_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadDownEvents() {
        mJsonData.addProperty(APIStatusDeserializer.DOWN_EVENTS_TAG, "hello");
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoAndroidSettings() {
        mJsonData.remove(APIStatusDeserializer.ANDROID_SETTINGS_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadAndroidSettings() {
        mJsonData.addProperty(APIStatusDeserializer.ANDROID_SETTINGS_TAG, "hello");
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMinVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .remove(APIStatusDeserializer.MIN_APP_VERSION_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadMinVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .add(APIStatusDeserializer.MIN_APP_VERSION_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoLatestVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .remove(APIStatusDeserializer.LATEST_APP_VERSION_TAG);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testBadLatestVersion() {
        mJsonData.get(APIStatusDeserializer.ANDROID_SETTINGS_TAG).getAsJsonObject()
          .add(APIStatusDeserializer.LATEST_APP_VERSION_TAG, new JsonArray());
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test
    public void testNoMessage() {
        mJsonData.remove(APIStatusDeserializer.MESSAGE_DICT);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
        assertNotNull(mStatus.getHasMessage());
        assertFalse(mStatus.getHasMessage());
    }

    @Test(expected = JsonParseException.class)
    public void testNoMessageText() {
        mJsonData.get(APIStatusDeserializer.MESSAGE_DICT).getAsJsonObject()
          .remove(APIStatusDeserializer.MESSAGE_TEXT);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test(expected = JsonParseException.class)
    public void testNoMessageExpiration() {
        mJsonData.get(APIStatusDeserializer.MESSAGE_DICT).getAsJsonObject()
          .remove(APIStatusDeserializer.MESSAGE_EXPIRATION);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
    }

    @Test
    public void testNoLastCacheClear() {
        mJsonData.remove(APIStatusDeserializer.LAST_OKHTTP_CACHE_CLEAR);
        mStatus = mDeserializer.deserialize(mJsonData, ApiStatus.class, mContext);
        assertNotNull(mStatus.getLastOkHttpCacheClear());
        assertEquals(-1, mStatus.getLastOkHttpCacheClear().intValue());
    }
}