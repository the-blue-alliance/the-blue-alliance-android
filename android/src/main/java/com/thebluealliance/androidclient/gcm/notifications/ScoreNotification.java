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
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Nathan on 7/24/2014.
 */
public class ScoreNotification extends BaseNotification {

    JsonObject jsonData;

    public ScoreNotification(String messageData) {
        super("score", messageData);

        jsonData = new JsonParser().parse(messageData).getAsJsonObject();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        JsonObject match = jsonData.get("match").getAsJsonObject();

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(match.get("key").getAsString());

        String matchKey = match.get("key").getAsString();

        String eventName = jsonData.get("event_name").getAsString();

        JsonObject alliances = match.get("alliances").getAsJsonObject();

        JsonObject redAlliance = alliances.get("red").getAsJsonObject();

        int redScore = redAlliance.get("score").getAsInt();

        ArrayList<String> redTeamKeys = new ArrayList<>();
        JsonArray redTeamsJson = redAlliance.getAsJsonArray("teams");
        for (int i = 0; i < redTeamsJson.size(); i++) {
            redTeamKeys.add(redTeamsJson.get(i).getAsString());
        }

        JsonObject blueAlliance = alliances.get("blue").getAsJsonObject();

        int blueScore = blueAlliance.get("score").getAsInt();

        ArrayList<String> blueTeamKeys = new ArrayList<>();
        JsonArray blueTeamsJson = blueAlliance.getAsJsonArray("teams");
        for (int i = 0; i < blueTeamsJson.size(); i++) {
            blueTeamKeys.add(blueTeamsJson.get(i).getAsString());
        }

        // TODO filter out teams that the user doesn't want notifications about

        // These arrays hold the numbers of teams that the user cares about
        ArrayList<String> redTeams = new ArrayList<>();
        ArrayList<String> blueTeams = new ArrayList<>();

        for (String key : redTeamKeys) {
            redTeams.add(key.replace("frc", ""));
        }

        for (String key : blueTeamKeys) {
            blueTeams.add(key.replace("frc", ""));
        }

        // Make sure the score string is formatted properly with the winning score first
        String scoreString = "";
        if (redScore > blueScore) {
            scoreString = redScore + "-" + blueScore;
        } else if (redScore < blueScore) {
            scoreString = blueScore + "-" + redScore;
        } else {
            scoreString = redScore + "-" + redScore;
        }

        String notificationString = "";
        if (redTeams.size() == 0 && blueTeams.size() == 0) {
            // We must have gotten this GCM message by mistake
            return null;
        } else if ((redTeams.size() > 0 && blueTeams.size() == 0)) {
            // The user only cares about some teams on the red alliance
            if (redScore > blueScore) {
                // Red won
                notificationString = String.format(r.getString(R.string.notification_score_teams_won), eventName, Utilities.stringifyListOfStrings(context, redTeams), matchTitle, scoreString);
            } else if (redScore < blueScore) {
                // Red lost
                notificationString = String.format(r.getString(R.string.notification_score_teams_lost), eventName, Utilities.stringifyListOfStrings(context, redTeams), matchTitle, scoreString);
            } else {
                // Red tied
                notificationString = String.format(r.getString(R.string.notification_score_teams_tied), eventName, Utilities.stringifyListOfStrings(context, redTeams), matchTitle, scoreString);
            }
        } else if ((blueTeams.size() > 0 && redTeams.size() == 0)) {
            // The user only cares about some teams on the blue alliance
            if (blueScore > redScore) {
                // Blue won
                notificationString = String.format(r.getString(R.string.notification_score_teams_won), eventName, Utilities.stringifyListOfStrings(context, blueTeams), matchTitle, scoreString);
            } else if (blueScore < redScore) {
                // Blue lost
                notificationString = String.format(r.getString(R.string.notification_score_teams_lost), eventName, Utilities.stringifyListOfStrings(context, blueTeams), matchTitle, scoreString);
            } else {
                // Blue tied
                notificationString = String.format(r.getString(R.string.notification_score_teams_tied), eventName, Utilities.stringifyListOfStrings(context, blueTeams), matchTitle, scoreString);
            }
        }  else if ((blueTeams.size() > 0 && redTeams.size() > 0)) {
            // The user cares about teams on both alliances
            if (blueScore > redScore) {
                // Blue won
                notificationString = String.format(r.getString(R.string.notification_score_teams_beat_teams), eventName, Utilities.stringifyListOfStrings(context, blueTeams), Utilities.stringifyListOfStrings(context, redTeams),matchTitle, scoreString);
            } else if (blueScore < redScore) {
                // Blue lost
                notificationString = String.format(r.getString(R.string.notification_score_teams_beat_teams), eventName, Utilities.stringifyListOfStrings(context, redTeams), Utilities.stringifyListOfStrings(context, blueTeams), matchTitle, scoreString);
            } else {
                // Blue tied
                notificationString = String.format(r.getString(R.string.notification_score_teams_tied_with_teams), eventName, Utilities.stringifyListOfStrings(context, redTeams), Utilities.stringifyListOfStrings(context, blueTeams), matchTitle, scoreString);
            }
        } else {
            // We should never, ever get here but if we do...
            return null;
        }

        // We can finally build the notification!
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_about_light);

        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewMatchActivity.newInstance(context, matchKey), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(r.getString(R.string.notification_score_title))
                .setContentText(notificationString)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setContentIntent(intent)
                .setAutoCancel(true);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationString);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + jsonData.get("match").getAsJsonObject().get("key").getAsString()).hashCode();
    }
}
