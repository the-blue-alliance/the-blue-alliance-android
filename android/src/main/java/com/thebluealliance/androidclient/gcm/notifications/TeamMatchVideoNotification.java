package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.TeamMatchVideoNotificationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeamMatchVideoNotification extends BaseNotification<TeamMatchVideoNotificationViewModel> {

    private String mEventName;
    private String mEventKey;
    private String mMatchKey;
    private List<String> mMatchTeamKeys;
    private Match mMatch;
    private final MatchWriter mWriter;
    private final Gson mGson;

    public TeamMatchVideoNotification(String messageData, MatchWriter writer, Gson gson) {
        super(NotificationTypes.SCHEDULE_UPDATED, messageData);
        mWriter = writer;
        mGson = gson;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = mGson.fromJson(messageData, JsonObject.class);
        if (!jsonData.has("match_key")) {
            throw new JsonParseException("TeamMatchVideoNotification has no match key");
        }
        mMatchKey = jsonData.get("match_key").getAsString();
        mEventKey = MatchHelper.getEventKeyFromMatchKey(mMatchKey);
        mEventName = jsonData.get("event_name").getAsString();
        mMatchTeamKeys = new ArrayList<>();

        mMatch = mGson.fromJson(jsonData.get("match"), Match.class);
        if (mMatch.getAlliances() != null) {
            mMatchTeamKeys.addAll(mMatch.getAlliances().getBlue().getTeamKeys());
            mMatchTeamKeys.addAll(mMatch.getAlliances().getRed().getTeamKeys());
        }
    }

    @Nullable @Override
    public TeamMatchVideoNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context,
                                                  EventHelper.shortName(mEventName),
                                                  EventHelper.getShortCodeForEventKey(mEventKey));
        return new TeamMatchVideoNotificationViewModel();
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();

        ArrayList<String> teamNumbers = Match.teamNumbers(mMatchTeamKeys);
        CharSequence teamNumberString;
        Predicate<String> isFollowing =
          teamNumber -> followsChecker.followsTeam(context, teamNumber, mMatchKey,
            NotificationTypes.MATCH_VIDEO);
        teamNumberString = Utilities.boldNameList(teamNumbers, isFollowing);

        String matchTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, mMatchKey);
        String eventCode = EventHelper.getEventCode(mMatchKey);
        String title = r.getString(R.string.notification_team_match_video, eventCode, matchTitle);
        String notificationBody = r.getString(R.string.notification_team_match_video_content,
                                              EventHelper.shortName(mEventName),
                                              teamNumberString);

        // We can finally build the notification!
        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(title);
        stored.setBody(notificationBody);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        stored.setMessageData(messageData);
        stored.setSystemId(getNotificationId());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(notificationBody);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationBody);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally() {
        mWriter.write(mMatch, new Date().getTime());
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + mMatchKey).hashCode();
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewMatchActivity.newInstance(c, mMatchKey);
    }
}
