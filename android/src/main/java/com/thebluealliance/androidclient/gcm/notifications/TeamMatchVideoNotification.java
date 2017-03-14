package com.thebluealliance.androidclient.gcm.notifications;

import com.google.common.base.Predicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.TeamMatchVideoNotificationViewModel;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeamMatchVideoNotification extends BaseNotification<TeamMatchVideoNotificationViewModel> {

    private String eventName;
    private String eventKey;
    private String matchKey;
    private List<String> matchTeamKeys;

    public TeamMatchVideoNotification(String messageData) {
        super(NotificationTypes.SCHEDULE_UPDATED, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = gson.fromJson(messageData, JsonObject.class);
        if (!jsonData.has("match_key")) {
            throw new JsonParseException("TeamMatchVideoNotification has no match key");
        }
        matchKey = jsonData.get("match_key").getAsString();
        eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        eventName = jsonData.get("event_name").getAsString();
        matchTeamKeys = new ArrayList<>();

        JsonArray matchTeams = jsonData.get("team_keys").getAsJsonArray();
        for (JsonElement team : matchTeams) {
            matchTeamKeys.add(team.getAsString());
        }
    }

    @Nullable @Override
    public TeamMatchVideoNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context,
                                                  EventHelper.shortName(eventName),
                                                  EventHelper.getShortCodeForEventKey(eventKey));
        return new TeamMatchVideoNotificationViewModel();
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();

        Predicate<String> isFollowing =
                teamNumber -> followsChecker.followsTeam(context, teamNumber, matchKey,
                                                         NotificationTypes.MATCH_VIDEO);
        ArrayList<String> teamNumbers = Match.teamNumbers(matchTeamKeys);
        CharSequence teamNumberString = Utilities.boldNameList(teamNumbers, isFollowing);

        String matchTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, matchKey);
        String eventCode = EventHelper.getEventCode(matchKey);
        String title = r.getString(R.string.notification_team_match_video, eventCode, matchTitle);
        String notificationBody = r.getString(R.string.notification_team_match_video_content,
                                              EventHelper.shortName(eventName),
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

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(notificationBody);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationBody);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally() {
        // Nothing to write here
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + matchKey).hashCode();
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewMatchActivity.newInstance(c, matchKey);
    }
}
