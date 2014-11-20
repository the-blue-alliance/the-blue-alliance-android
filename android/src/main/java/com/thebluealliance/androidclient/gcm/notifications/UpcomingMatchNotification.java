package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.helpers.MatchHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nathan on 7/24/2014.
 */
public class UpcomingMatchNotification extends BaseNotification {

    JsonObject jsonData;

    public UpcomingMatchNotification(String messageData) {
        super("upcoming_match", messageData);

        jsonData = new JsonParser().parse(messageData).getAsJsonObject();
    }

    /**
     * @param context a Context object for use by the notificatoin builder
     * @return A constructed notification
     */

    @Override
    public Notification buildNotification(Context context) {

        Resources r = context.getResources();

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(jsonData.get("match_key").getAsString());

        String matchKey = jsonData.get("match_key").getAsString();

        String eventName = jsonData.get("event_name").getAsString();

        long scheduledStartTimeUNIX = jsonData.get("scheduled_time").getAsLong();
        // We multiply by 1000 because the Date constructor expects
        Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
        DateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getDefault());
        String scheduledStartTimeString = format.format(scheduledStartTime);

        ArrayList<String> favoritedTeamsFromMatch = new ArrayList<>();
        JsonArray teamKeys = jsonData.get("team_keys").getAsJsonArray();
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
        if (numFavoritedTeams == 1) {
            contentText = String.format(r.getString(R.string.notification_upcoming_match_text_single_team), eventName, teamsString, matchTitle, scheduledStartTimeString);
        } else {
            contentText = String.format(r.getString(R.string.notification_upcoming_match_text_multiple_teams), eventName, teamsString, matchTitle, scheduledStartTimeString);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_time_light);

        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewMatchActivity.newInstance(context, matchKey), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(r.getString(R.string.notification_upcoming_match_title))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + jsonData.get("match_key").getAsString()).hashCode();
    }
}
