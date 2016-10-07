package com.thebluealliance.androidclient.notifications;

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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DistrictPointsUpdatedNotificationTest {
    private Context mContext;
    private DistrictPointsUpdatedNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);
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
        Resources res = mock(Resources.class);
        when(mContext.getResources()).thenReturn(res);
        when(res.getString(R.string.notification_district_points_updated, "Pacific Northwest"))
          .thenReturn("District point calculations have been updated for Pacific Northwest");
        when(res.getString(R.string.notification_district_points_title, "PNW"))
          .thenReturn("District Points Updated PNW");
        Notification notification = mNotification.buildNotification(mContext, null);
        assertNotNull(notification);

        StoredNotification stored = mNotification.getStoredNotification();
        assertNotNull(stored);
        assertEquals(stored.getType(), NotificationTypes.DISTRICT_POINTS_UPDATED);
        assertEquals(stored.getTitle(), "District Points Updated PNW");
        assertEquals(stored.getBody(), "District point calculations have been updated for Pacific Northwest");
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
