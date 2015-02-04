package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nathan on 7/24/2014.
 */
public class UpcomingMatchNotification extends BaseNotification {

    private String eventName, matchTitle, matchKey;
    private JsonElement matchTime;
    private JsonArray teamKeys;

    public UpcomingMatchNotification(String messageData) {
        super("upcoming_match", messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = new JsonParser().parse(messageData).getAsJsonObject();
        if (!jsonData.has("match_key")) {
            throw new JsonParseException("Notification data does not contain 'match_key'");
        }
        matchKey = jsonData.get("match_key").getAsString();
        matchTitle = MatchHelper.getMatchTitleFromMatchKey(matchKey);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("team_keys")) {
            throw new JsonParseException("Notification data does not contain 'team_keys");
        }
        teamKeys = jsonData.get("team_keys").getAsJsonArray();
        if (!jsonData.has("scheduled_time")) {
            throw new JsonParseException("Notification data does not contain 'scheduled_time'");
        }
        matchTime = jsonData.get("scheduled_time");
    }

    /**
     * @param context a Context object for use by the notificatoin builder
     * @return A constructed notification
     */
    @Override
    public Notification buildNotification(Context context) {

        Resources r = context.getResources();

        String scheduledStartTimeString;
        if (matchTime.isJsonNull()) {
            scheduledStartTimeString = "";
        } else {
            long scheduledStartTimeUNIX = matchTime.getAsLong();
            // We multiply by 1000 because the Date constructor expects
            Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
            DateFormat format = new SimpleDateFormat("HH:mm");
            format.setTimeZone(TimeZone.getDefault());
            scheduledStartTimeString = format.format(scheduledStartTime);
        }

        ArrayList<String> favoritedTeamsFromMatch = new ArrayList<>();
        for (int i = 0; i < teamKeys.size(); i++) {
            // TODO determine if team is favorited by checking local database

            // strip the "frc" from each team key to get the number
            favoritedTeamsFromMatch.add(teamKeys.get(i).getAsString().replace("frc", ""));
        }

        String teamsString = Utilities.stringifyListOfStrings(context, favoritedTeamsFromMatch);

        int numFavoritedTeams = favoritedTeamsFromMatch.size();

        String contentText = "";
        if (numFavoritedTeams == 0) {
            // Looks like we got this GCM notification by mistake
            return null;
        }
        if (scheduledStartTimeString.isEmpty()) {
            if (numFavoritedTeams == 1) {
                contentText = String.format(r.getString(R.string.notification_upcoming_match_text_single_team_no_time), eventName, teamsString, matchTitle);
            } else {
                contentText = String.format(r.getString(R.string.notification_upcoming_match_text_multiple_teams_no_time), eventName, teamsString, matchTitle);
            }
        } else {
            if (numFavoritedTeams == 1) {
                contentText = String.format(r.getString(R.string.notification_upcoming_match_text_single_team), eventName, teamsString, matchTitle, scheduledStartTimeString);
            } else {
                contentText = String.format(r.getString(R.string.notification_upcoming_match_text_multiple_teams), eventName, teamsString, matchTitle, scheduledStartTimeString);
            }
        }

        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewMatchActivity.newInstance(context, matchKey), 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(r.getString(R.string.notification_score_title));
        stored.setBody(contentText);
        stored.setIntent(ViewMatchActivity.newInstance(context, matchKey).toString());
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_upcoming_match_title))
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp))
                .setContentIntent(intent)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        /* This notification has no data that we can store locally */
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + matchKey).hashCode();
    }
}
