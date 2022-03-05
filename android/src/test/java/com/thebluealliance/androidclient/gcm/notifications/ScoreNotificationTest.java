package com.thebluealliance.androidclient.gcm.notifications;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ScoreNotificationTest {

    @Mock
    private Context mContext;
    @Mock
    private MatchWriter mWriter;
    @Mock
    private MatchRenderer mRenderer;

    private ScoreNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        mWriter = mock(MatchWriter.class);
        mData = ModelMaker.getModel(JsonObject.class, "notification_match_score");
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer, TBAAndroidModule.getGson());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2014necmp");
        assertEquals(mNotification.getEventName(), "New England FRC Region Championship");
        assertEquals(mNotification.getMatchKey(), "2014necmp_f1m1");
        assertNotNull(mNotification.getMatch());
    }

    @Test
    public void testDbWrite() {
        mNotification.parseMessageData();
        mNotification.updateDataLocally();

        Match match = mNotification.getMatch();
        verify(mWriter).write(eq(match), anyLong());
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoMatch() {
        mData.remove("match");
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer, TBAAndroidModule.getGson());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoEventName() {
        mData.remove("event_name");
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer, TBAAndroidModule.getGson());
        mNotification.parseMessageData();
    }

    @Test
    public void testGetIntent() {
        mNotification.parseMessageData();
        Intent intent = mNotification.getIntent(mContext);
        assertNotNull(intent);
        assertEquals(intent.getComponent().getClassName(), "com.thebluealliance.androidclient.activities.ViewMatchActivity");
        assertEquals(intent.getStringExtra(ViewMatchActivity.MATCH_KEY), mNotification.getMatchKey());
    }

    //TODO need to test built notification
    //Waiting until we have a better way to deal with resources
    //Too many possibilities/individual things to mock individually
}
