package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DistrictPointsUpdatedNotificationTest {
    private Context mContext;
    private DistrictPointsUpdatedNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
        mData = ModelMaker.getModel(JsonObject.class, "notification_district_points_updated");
        mNotification = new DistrictPointsUpdatedNotification(mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mNotification.getDistrictKey(), "2014pnw");
        assertEquals(mNotification.getDistrictName(), "Pacific Northwest");
    }

    @Test(expected = JsonParseException.class)
    public void testNoDistrictKey() {
        mData.remove("district_key");
        mNotification = new DistrictPointsUpdatedNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test(expected = JsonParseException.class)
    public void testNoDistrictName() {
        mData.remove("district_name");
        mNotification = new DistrictPointsUpdatedNotification(mData.toString());
        mNotification.parseMessageData();
    }

    @Test
    public void testBuildNotification() {
        mNotification.parseMessageData();
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(stored.getType(), NotificationTypes.DISTRICT_POINTS_UPDATED);
        assertEquals(stored.getTitle(), mContext.getString(R.string.notification_district_points_title, "PNW"));
        assertEquals(stored.getBody(), mContext.getString(R.string.notification_district_points_updated,"Pacific Northwest"));
        assertEquals(stored.getMessageData(), mData.toString());
        assertEquals(stored.getIntent(), MyTBAHelper.serializeIntent(mNotification.getIntent(mContext)));
        assertNotNull(stored.getTime());
    }

    @Test
    public void testGetIntent() {
        mNotification.parseMessageData();
        Intent intent = mNotification.getIntent(mContext);
        assertNotNull(intent);
        assertEquals(intent.getComponent().getClassName(), "com.thebluealliance.androidclient.activities.ViewDistrictActivity");
        assertEquals(intent.getStringExtra(ViewDistrictActivity.DISTRICT_ABBREV), "pnw");
        assertEquals(intent.getIntExtra(ViewDistrictActivity.YEAR, -1), 2014);
    }

    @Test
    public void testRenderToViewModel() {
        mNotification.parseMessageData();
        GenericNotificationViewModel viewModel = mNotification.renderToViewModel(mContext, null);
        assertNotNull(viewModel);
    }
}
