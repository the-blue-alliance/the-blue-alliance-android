package com.thebluealliance.androidclient.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ScheduleUpdatedNotificationTest {
    private Context mContext;
    private ScheduleUpdatedNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
        mData = ModelMaker.getModel(JsonObject.class, "notification_schedule_updated");
        mNotification = new ScheduleUpdatedNotification(mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2015ausy");
        assertEquals(mNotification.getEventName(), "Australia Regional");
        assertNotNull(mNotification.getMatchTime());
        assertEquals(mNotification.getMatchTime().getAsInt(), 1397330280);
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventKey() {
        mData.remove("event_key");
        mNotification = new ScheduleUpdatedNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventName() {
        mData.remove("event_name");
        mNotification = new ScheduleUpdatedNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoMatchTime() {
        mData.remove("first_match_time");
        mNotification = new ScheduleUpdatedNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test
    public void testBuildNotification() {
        mNotification.parseMessageData();
        when(mContext.getString(R.string.notification_schedule_updated_with_time, "Australia", "15:18:00"))
          .thenReturn("The match schedule at Australia has been updated. The next match starts at 15:18:00");
        when(mContext.getString(R.string.notification_schedule_updated_title, "AUSY"))
          .thenReturn("Event Schedule Updated AUSY");
        Notification notification = mNotification.buildNotification(mContext);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(stored.getType(), NotificationTypes.SCHEDULE_UPDATED);
        assertEquals(stored.getTitle(), "Event Schedule Updated AUSY");
        assertEquals(stored.getBody(), "The match schedule at Australia has been updated. The next match starts at 15:18:00");
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
