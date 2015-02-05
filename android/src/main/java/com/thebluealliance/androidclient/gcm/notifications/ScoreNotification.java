package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nathan on 7/24/2014.
 */
public class ScoreNotification extends BaseNotification {

    private String eventName, matchKey;
    private Match match;

    public ScoreNotification(String messageData) {
        super("score", messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException{
        JsonObject jsonData = jsonData = JSONManager.getasJsonObject(messageData);
        if(!jsonData.has("match")){
            throw new JsonParseException("Notification data does not contain 'match");
        }
        JsonObject match = jsonData.get("match").getAsJsonObject();
        this.match = gson.fromJson(match, Match.class);
        if(!jsonData.has("event_name")){
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String matchKey;
        try {
            matchKey = match.getKey();
            this.matchKey = matchKey;
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(getLogTag(), "Incoming Match object does not have a key. Can't post score update");
            e.printStackTrace();
            return null;
        }
        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(matchKey);

        JsonObject alliances;
        try {
            alliances = match.getAlliances();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(getLogTag(), "Inciming match object does not contain alliance data. Can't post score update");
            e.printStackTrace();
            return null;
        }
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
        } else if ((blueTeams.size() > 0 && redTeams.size() > 0)) {
            // The user cares about teams on both alliances
            if (blueScore > redScore) {
                // Blue won
                notificationString = String.format(r.getString(R.string.notification_score_teams_beat_teams), eventName, Utilities.stringifyListOfStrings(context, blueTeams), Utilities.stringifyListOfStrings(context, redTeams), matchTitle, scoreString);
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
        Intent instance = ViewMatchActivity.newInstance(context, matchKey);
        PendingIntent intent = PendingIntent.getActivity(context, 0, instance, 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(r.getString(R.string.notification_score_title));
        stored.setBody(notificationString);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_score_title))
                .setContentText(notificationString)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp))
                .setContentIntent(intent)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationString);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        if(match != null){
            match.write(c);
        }
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + matchKey).hashCode();
    }
}
