package com.thebluealliance.androidclient.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.Intent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ScoreNotificationTest {

    @Mock private Context mContext;
    @Mock private MatchWriter mWriter;
    @Mock private MatchRenderer mRenderer;

    private ScoreNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        mWriter = mock(MatchWriter.class);
        mData = ModelMaker.getModel(JsonObject.class, "notification_match_score");
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer);
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
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer);
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoEventName() {
        mData.remove("event_name");
        mNotification = new ScoreNotification(mData.toString(), mWriter, mRenderer);
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
