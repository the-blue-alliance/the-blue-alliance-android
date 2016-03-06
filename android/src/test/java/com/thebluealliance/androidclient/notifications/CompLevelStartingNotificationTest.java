package com.thebluealliance.androidclient.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CompLevelStartingNotificationTest {
    private Context mContext;
    private CompLevelStartingNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        mData = ModelMaker.getModel(JsonObject.class, "notification_level_starting");
        mNotification = new CompLevelStartingNotification(mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2014hiho");
        assertEquals(mNotification.getEventName(), "Hawaii Regional");
        assertEquals(mNotification.getCompLevelAbbrev(), "f");
        assertNotNull(mNotification.getScheduledTime());
        assertEquals(mNotification.getScheduledTime().getAsInt(), 1397330280);
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventKey() {
        mData.remove("event_key");
        mNotification = new CompLevelStartingNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventName() {
        mData.remove("event_name");
        mNotification = new CompLevelStartingNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoCompLevel() {
        mData.remove("comp_level");
        mNotification = new CompLevelStartingNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test
    public void testNoScheduledTime() {
        mData.remove("scheduled_time");
        mNotification = new CompLevelStartingNotification(mData.toString());
        mNotification.parseMessageData();
        assertNull(mNotification.getScheduledTime());
    }

    @Test
    public void testBuildNotification() {
        mNotification.parseMessageData();
        Resources res = mock(Resources.class);
        when(mContext.getResources()).thenReturn(res);
        when(res.getString(R.string.finals_header)).thenReturn("Finals Matches");
        when(res.getString(R.string.notification_level_starting_with_time, mNotification.getEventName(), "Finals Matches", "15:18:00"))
          .thenReturn("Finals Matches are scheduled for 15:18:00");
        when(res.getString(R.string.notification_level_starting_title, "HIHO", "Finals Matches"))
          .thenReturn("HIHO Finals Matches Starting");
        when(res.getString(R.string.notification_level_starting, mNotification.getEventName(), "Finals Matches"))
          .thenReturn("Finals Matches starting");
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(NotificationTypes.LEVEL_STARTING, stored.getType());
        assertEquals("HIHO Finals Matches Starting", stored.getTitle());
        assertEquals(mData.toString(), stored.getMessageData());
        assertEquals(MyTBAHelper.serializeIntent(mNotification.getIntent(mContext)), stored.getIntent());
        assertNotNull(stored.getTime());
    }

    @Test
    public void testBuildNotificationNoTime() {
        mData.remove("scheduled_time");
        mNotification = new CompLevelStartingNotification(mData.toString());
        mNotification.parseMessageData();
        Resources res = mock(Resources.class);
        when(mContext.getResources()).thenReturn(res);
        when(res.getString(R.string.finals_header)).thenReturn("Finals Matches");
        when(res.getString(R.string.notification_level_starting, mNotification.getEventName(), "Finals Matches"))
          .thenReturn("Finals Matches starting");
        when(res.getString(R.string.notification_level_starting_title, "HIHO", "Finals Matches"))
          .thenReturn("HIHO Finals Matches Starting");
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(NotificationTypes.LEVEL_STARTING, stored.getType());
        assertEquals("HIHO Finals Matches Starting", stored.getTitle());
        assertEquals("Finals Matches starting", stored.getBody());
        assertEquals(stored.getMessageData(), mData.toString());
        assertEquals(stored.getIntent(), MyTBAHelper.serializeIntent(mNotification.getIntent(mContext)));
        assertNotNull(stored.getTime());
    }

    @Test
    public void testGetIntent() {
        mNotification.parseMessageData();
        Intent intent = mNotification.getIntent(mContext);
        assertNotNull(intent);
        assertEquals(intent.getComponent().getClassName(), "com.thebluealliance.androidclient.activities.ViewEventActivity");
        assertEquals(intent.getStringExtra(ViewEventActivity.EVENTKEY), mNotification.getEventKey());
        assertEquals(intent.getIntExtra(ViewEventActivity.TAB, -1), ViewEventFragmentPagerAdapter.TAB_MATCHES);
    }
}
