package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.models.StoredNotification;

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
import static org.junit.Assert.assertNotNull;

@RunWith(Enclosed.class)
public class GCMMessageHandlerTest {

    @RunWith(ParameterizedRobolectricTestRunner.class)
    public static class TestRenderSingleNotifications {

        private NotificationManager mNotificationManager;

        @ParameterizedRobolectricTestRunner.Parameter(0)
        public String mNotificationType;
        @ParameterizedRobolectricTestRunner.Parameter(1)
        public String mNotificationDataFileName;
        @ParameterizedRobolectricTestRunner.Parameter(2)
        public int mExpectedPriority;

        @Before
        public void setUp() {
            Context applicationContext = ApplicationProvider.getApplicationContext();
            mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @ParameterizedRobolectricTestRunner.Parameters(name = "NotificationType = {0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {NotificationTypes.ALLIANCE_SELECTION, "notification_alliance_selection", Notification.PRIORITY_HIGH},
                    {NotificationTypes.AWARDS, "notification_awards_posted", Notification.PRIORITY_HIGH},
                    {NotificationTypes.DISTRICT_POINTS_UPDATED, "notification_district_points_updated", Notification.PRIORITY_HIGH},
                    {NotificationTypes.EVENT_DOWN, "notification_event_down", Notification.PRIORITY_HIGH},
                    {NotificationTypes.LEVEL_STARTING, "notification_level_starting", Notification.PRIORITY_HIGH},
                    {NotificationTypes.MATCH_SCORE, "notification_match_score", Notification.PRIORITY_HIGH},
                    {NotificationTypes.PING, "notification_ping", Notification.PRIORITY_LOW},
                    {NotificationTypes.SCHEDULE_UPDATED, "notification_schedule_updated", Notification.PRIORITY_HIGH},
                    {NotificationTypes.UPCOMING_MATCH, "notification_upcoming_match", Notification.PRIORITY_HIGH},
            });
        }

        @Test
        public void testPostAndDismissSingleNotificationWithSystem() {
            Intent intent = buildIntent(mNotificationType, mNotificationDataFileName);
            GCMMessageHandlerWithMocks service = Robolectric.setupService(GCMMessageHandlerWithMocks.class);
            service.onCreate();
            service.onHandleWork(intent);

            // Check we post the notification with the system
            List<Notification> notifications = Shadows.shadowOf(mNotificationManager).getAllNotifications();
            assertEquals(1, notifications.size());
            Notification notification = notifications.get(0);
            assertEquals(BaseNotification.NOTIFICATION_CHANNEL, notification.getChannelId());
            assertEquals(mExpectedPriority, notification.priority);

            // Grab the notification we rendered to check with
            BaseNotification lastPosted = service.getLastNotification();
            assertNotNull(lastPosted);

            // Cancel the notification
            mNotificationManager.cancel(lastPosted.getNotificationId());

            // Double check the system has cleared it (eg the id is right)
            notifications = Shadows.shadowOf(mNotificationManager).getAllNotifications();
            assertEquals(0, notifications.size());
        }


        @Test
        public void testPostAndDismissSingleNotificatioInDb() {
            Intent intent = buildIntent(mNotificationType, mNotificationDataFileName);
            GCMMessageHandlerWithMocks service = Robolectric.setupService(GCMMessageHandlerWithMocks.class);
            service.onCreate();
            service.onHandleWork(intent);

            // Grab the notification we rendered to check with
            BaseNotification lastPosted = service.getLastNotification();
            assertNotNull(lastPosted);

            // Check we write the notification to the local DB
            NotificationsTable dbTable = service.getDatabase().getNotificationsTable();
            List<StoredNotification> storedNotifications = dbTable.getActive();
            assertEquals(1, storedNotifications.size());
            StoredNotification stored = storedNotifications.get(0);
            assertEquals(mNotificationType, stored.getType());
            assertEquals(lastPosted.getNotificationId(), stored.getSystemId());

            // Cancel the notification
            mNotificationManager.cancel(lastPosted.getNotificationId());

            // Double check we remove the notification from being active in the db
            storedNotifications = dbTable.getActive();
            assertEquals(0, storedNotifications.size());
        }
    }

    private static Intent buildIntent(String notificationType, String dataFileName) {
        JsonObject notificationData = ModelMaker.getModel(JsonObject.class, dataFileName);
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
        intent.putExtra("notification_type", notificationType);
        intent.putExtra("message_data", notificationData.toString());
        return intent;
    }
}
