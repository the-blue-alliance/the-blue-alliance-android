package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.StoredNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class RecentNotificationsSubscriberTest {

    @Mock public Database mDb;

    private RecentNotificationsSubscriber mSubscriber;
    private List<StoredNotification> mNotifications;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockNotificationsTable(mDb);
        mSubscriber = new RecentNotificationsSubscriber();
        List<JsonObject> notificationData = ModelMaker.getMultiModelList(JsonObject.class,
          "notification_alliance_selection",
          "notification_awards_posted",
          // Not implemented yet
          // "notification_district_points_updated",
          "notification_level_starting",
          "notification_match_score",
          "notification_ping",
          "notification_schedule_updated",
          "notification_upcoming_match");
        String[] types = {
          NotificationTypes.ALLIANCE_SELECTION,
          NotificationTypes.AWARDS,
          // Not implemented yet
          //NotificationTypes.DISTRICT_POINTS_UPDATED,
          NotificationTypes.LEVEL_STARTING,
          NotificationTypes.MATCH_SCORE,
          NotificationTypes.PING,
          NotificationTypes.SCHEDULE_UPDATED,
          NotificationTypes.UPCOMING_MATCH};
        mNotifications = mockStoredNotificationList(notificationData, types);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mNotifications);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> parsedData = DatafeedTestDriver.getParsedData(mSubscriber, mNotifications);

        assertNotNull(parsedData);
        assertEquals(parsedData.size(), 6);
        assertTrue(parsedData.get(0) instanceof AllianceSelectionNotification);
        assertTrue(parsedData.get(1) instanceof AwardsPostedNotification);
        assertTrue(parsedData.get(2) instanceof CompLevelStartingNotification);
        assertTrue(parsedData.get(3) instanceof ScoreNotification);
        assertTrue(parsedData.get(4) instanceof ScheduleUpdatedNotification);
        assertTrue(parsedData.get(5) instanceof UpcomingMatchNotification);
    }

    private static List<StoredNotification> mockStoredNotificationList(List<JsonObject> dataList, String[] types) {
        List<StoredNotification> notifications = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            JsonObject data = dataList.get(i);

            StoredNotification notification = spy(new StoredNotification());
            when(notification.getType()).thenReturn(types[i]);
            when(notification.getMessageData()).thenReturn(data.toString());
            when(notification.getTime()).thenReturn(new Date());
            notifications.add(notification);
        }
        return notifications;
    }
}