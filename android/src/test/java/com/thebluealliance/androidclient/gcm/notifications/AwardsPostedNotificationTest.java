package com.thebluealliance.androidclient.gcm.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.StoredNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AwardsPostedNotificationTest {

    @Mock
    private Context mContext;
    @Mock
    private AwardListWriter mWriter;
    private AwardsPostedNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
        mWriter = mock(AwardListWriter.class);
        mData = ModelMaker.getModel(JsonObject.class, "notification_awards_posted");
        mNotification = new AwardsPostedNotification(mData.toString(), mWriter, TBAAndroidModule.getGson());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getEventKey(), "2014necmp");
        assertEquals(mNotification.getEventName(), "New England FRC Region Championship");

        List<Award> awards = mNotification.getAwards();
        assertNotNull(awards);
        assertEquals(awards.size(), 1);
    }

    @Test
    public void testDbWrite() {
        mNotification.parseMessageData();
        mNotification.updateDataLocally();

        List<Award> awards = mNotification.getAwards();
        verify(mWriter).write(eq(awards), anyLong());
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventKey() {
        mData.remove("event_key");
        mNotification = new AwardsPostedNotification(mData.toString(), mWriter, TBAAndroidModule.getGson());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoEventName() {
        mData.remove("event_name");
        mNotification = new AwardsPostedNotification(mData.toString(), mWriter,  TBAAndroidModule.getGson());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoAwards() {
        mData.remove("awards");
        mNotification = new AwardsPostedNotification(mData.toString(), mWriter,  TBAAndroidModule.getGson());
        mNotification.parseMessageData();
    }

    @Test
    public void testBuildNotification() {
        mNotification.parseMessageData();
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertEquals(stored.getType(), NotificationTypes.AWARDS);
        assertEquals(stored.getTitle(), mContext.getString(R.string.notification_awards_updated_title, "NECMP"));
        assertEquals(stored.getBody(), mContext.getString(R.string.notification_awards_updated, "New England"));
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
        assertEquals(intent.getIntExtra(ViewEventActivity.TAB, -1), ViewEventFragmentPagerAdapter.TAB_AWARDS);
    }
}