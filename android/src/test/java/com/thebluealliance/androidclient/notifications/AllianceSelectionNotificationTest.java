package com.thebluealliance.androidclient.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.database.writers.EventWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.StoredNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class AllianceSelectionNotificationTest {

    @Mock private Context mContext;
    @Mock private EventWriter mWriter;
    private AllianceSelectionNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
        mWriter = mock(EventWriter.class);
        mData = ModelMaker.getModel(JsonObject.class, "notification_alliance_selection");
        mNotification = new AllianceSelectionNotification(mData.toString(), mWriter);
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2014necmp");

        Event event = mNotification.getEvent();
        assertNotNull(event);
    }

    @Test
    public void testDbWrite() {
        mNotification.parseMessageData();
        mNotification.updateDataLocally();

        Event event = mNotification.getEvent();
        verify(mWriter).write(eq(event), anyLong());
    }

    @Test(expected = JsonParseException.class)
    public void testNoEvent() {
        mData.remove("event");
        mNotification = new AllianceSelectionNotification(mData.toString(), mWriter);
        mNotification.parseMessageData();
    }

    @Test
    public void testBuildNotification() {
        mNotification.parseMessageData();
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(stored.getType(), NotificationTypes.ALLIANCE_SELECTION);
        assertEquals(stored.getTitle(), mContext.getString(R.string.notification_alliances_updated_title, "NECMP"));
        assertEquals(stored.getBody(), mContext.getString(R.string.notification_alliances_updated, "New England"));
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
        assertEquals(intent.getIntExtra(ViewEventActivity.TAB, -1), ViewEventFragmentPagerAdapter.TAB_ALLIANCES);
    }
}
