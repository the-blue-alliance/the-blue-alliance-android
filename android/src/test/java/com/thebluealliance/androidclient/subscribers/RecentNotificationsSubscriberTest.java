package com.thebluealliance.androidclient.subscribers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.CompLevelStartingNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
@Ignore
public class RecentNotificationsSubscriberTest {

    @Mock Database mDb;
    @Mock MatchRenderer mRenderer;

    private RecentNotificationsSubscriber mSubscriber;
    private List<StoredNotification> mNotifications;
    private Context mContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);

        when(mContext.getString(R.string.match_title_abbrev_format)).thenReturn("%1$s%2$s");
        when(mContext.getString(R.string.match_title_format)).thenReturn("%1$s %2$s");
        when(mContext.getString(R.string.submatch_title_abbrev_format)).thenReturn("%1$s%2$s-%3$s");
        when(mContext.getString(R.string.submatch_title_format)).thenReturn("%1$s %2$s Match %3$s");

        DatabaseMocker.mockNotificationsTable(mDb);
        DatabaseWriter writer = mockDatabaseWriter();
        mSubscriber = new RecentNotificationsSubscriber(writer, mContext, mRenderer, TBAAndroidModule.getGson());
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
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mNotifications);
    }

    @Test
    public void testParsedData()  {
        List<Object> parsedData = DatafeedTestDriver.getParsedData(mSubscriber, mNotifications);

        assertNotNull(parsedData);
        assertEquals(parsedData.size(), 7);
        assertTrue(parsedData.get(0) instanceof AllianceSelectionNotificationViewModel);
        assertTrue(parsedData.get(1) instanceof AwardsPostedNotificationViewModel);
        assertTrue(parsedData.get(2) instanceof CompLevelStartingNotificationViewModel);
        assertTrue(parsedData.get(3) instanceof ScoreNotificationViewModel);
        assertTrue(parsedData.get(4) instanceof GenericNotificationViewModel);
        assertTrue(parsedData.get(5) instanceof ScheduleUpdatedNotificationViewModel);
        assertTrue(parsedData.get(6) instanceof UpcomingMatchNotificationViewModel);
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

    private static DatabaseWriter mockDatabaseWriter() {
        return mock(DatabaseWriter.class, RETURNS_DEEP_STUBS);
    }
}