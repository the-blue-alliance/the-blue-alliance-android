package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class GCMMessageHandlerTest {

    @RunWith(ParameterizedRobolectricTestRunner.class)
    public static class TestRenderSingleNotifications {

        private NotificationManager mNotificationManager;

        @ParameterizedRobolectricTestRunner.Parameter(0) public String mNotificationType;
        @ParameterizedRobolectricTestRunner.Parameter(1) public String mNotificationDataFileName;

        @Before
        public void setUp() {
            Context applicationContext = ApplicationProvider.getApplicationContext();
            mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @ParameterizedRobolectricTestRunner.Parameters(name = "NotificationType = {0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {NotificationTypes.ALLIANCE_SELECTION, "notification_alliance_selection"},
                    {NotificationTypes.AWARDS, "notification_awards_posted"},
                    {NotificationTypes.DISTRICT_POINTS_UPDATED, "notification_district_points_updated"},
                    {NotificationTypes.EVENT_DOWN, "notification_event_down"},
                    {NotificationTypes.LEVEL_STARTING, "notification_level_starting"},
                    {NotificationTypes.MATCH_SCORE, "notification_match_score"},
                    {NotificationTypes.PING, "notification_ping"},
                    {NotificationTypes.SCHEDULE_UPDATED, "notification_schedule_updated"},
                    {NotificationTypes.UPCOMING_MATCH, "notification_upcoming_match"},
            });
        }

        @Test
        public void testPostNotification() {
            Intent intent = buildIntent();
            GCMMessageHandlerWithMocks service = Robolectric.setupService(GCMMessageHandlerWithMocks.class);
            service.onCreate();
            service.onHandleWork(intent);

            List<Notification> notification = Shadows.shadowOf(mNotificationManager).getAllNotifications();
            assertEquals(1, notification.size());
        }

        private Intent buildIntent() {
            JsonObject notificationData = ModelMaker.getModel(JsonObject.class, mNotificationDataFileName);
            Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
            intent.putExtra("notification_type", mNotificationType);
            intent.putExtra("message_data", notificationData.toString());
            return intent;
        }
    }
}
