package com.thebluealliance.androidclient.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.Intent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UpcomingMatchNotificationTest {
    private Context mContext;
    private UpcomingMatchNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        mData = ModelMaker.getModel(JsonObject.class, "notification_upcoming_match");
        mNotification = new UpcomingMatchNotification(mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2014calb");
        assertEquals(mNotification.getEventName(), "Los Angeles Regional sponsored by The Roddenberry Foundation");
        assertEquals(mNotification.getMatchKey(), "2014calb_qm17");
        assertNotNull(mNotification.getTeamKeys());
        assertEquals(mNotification.getTeamKeys().size(), 6);
        assertEquals(mNotification.getRedTeams().length, 3);
        assertEquals(mNotification.getBlueTeams().length, 3);
        assertTrue(!mNotification.getMatchTime().isJsonNull());
        assertEquals(mNotification.getMatchTime().getAsInt(), 12345);
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoMatchKey() {
        mData.remove("match_key");
        mNotification = new UpcomingMatchNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoEventName() {
        mData.remove("event_name");
        mNotification = new UpcomingMatchNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testParseNoTeams() {
        mData.remove("team_keys");
        mNotification = new UpcomingMatchNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test
    public void testParseNoTime() {
        mData.remove("scheduled_time");
        mNotification = new UpcomingMatchNotification(mData.toString());
        mNotification.parseMessageData();
        assertTrue(mNotification.getMatchTime().isJsonNull());
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
