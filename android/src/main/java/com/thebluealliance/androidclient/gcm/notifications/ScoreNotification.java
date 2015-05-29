package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.views.MatchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nathan on 7/24/2014.
 */
public class ScoreNotification extends BaseNotification {

    private String eventName, eventKey, matchKey;
    private Match match;

    public ScoreNotification(String messageData) {
        super("score", messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        if (!jsonData.has("match")) {
            throw new JsonParseException("Notification data does not contain 'match");
        }
        JsonObject match = jsonData.get("match").getAsJsonObject();
        this.match = gson.fromJson(match, Match.class);
        try {
            this.matchKey = this.match.getKey();
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
        this.eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        if (!jsonData.has("event_name")) {
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

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(context, matchKey);
        String matchAbbrevTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, matchKey);

        JsonObject alliances;
        try {
            alliances = match.getAlliances();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(getLogTag(), "Incoming match object does not contain alliance data. Can't post score update");
            e.printStackTrace();
            return null;
        }
        int redScore = Match.getRedScore(alliances);

        ArrayList<String> redTeamKeys = new ArrayList<>();
        JsonArray redTeamsJson = Match.getRedTeams(alliances);
        for (int i = 0; i < redTeamsJson.size(); i++) {
            redTeamKeys.add(redTeamsJson.get(i).getAsString());
        }

        int blueScore = Match.getBlueScore(alliances);

        ArrayList<String> blueTeamKeys = new ArrayList<>();
        JsonArray blueTeamsJson = Match.getBlueTeams(alliances);
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
        String scoreString;
        if (redScore > blueScore) {
            scoreString = redScore + "-" + blueScore;
        } else if (redScore < blueScore) {
            scoreString = blueScore + "-" + redScore;
        } else {
            scoreString = redScore + "-" + redScore;
        }

        String redTeamString = Utilities.stringifyListOfStrings(context, redTeams);
        String blueTeamString = Utilities.stringifyListOfStrings(context, blueTeams);

        boolean useSpecial2015Format;
        try {
            useSpecial2015Format = match.getYear() == 2015 && match.getType() != MatchHelper.TYPE.FINAL;
        } catch (BasicModel.FieldNotDefinedException e) {
            useSpecial2015Format = false;
            Log.w(Constants.LOG_TAG, "Couldn't determine if we should use 2015 score format. Defaulting to no");
        }

        String eventShortName = EventHelper.shortName(eventName);
        String notificationString;
        if (redTeams.size() == 0 && blueTeams.size() == 0) {
            // We must have gotten this GCM message by mistake
            return null;
        } else if (useSpecial2015Format) {
            /* Only for 2015 non-finals matches. Ugh */
            notificationString = context.getString(R.string.notification_score_2015_no_winner, eventShortName, matchTitle, redTeamString, redScore, blueTeamString, blueScore);
        } else if ((redTeams.size() > 0 && blueTeams.size() == 0)) {
            // The user only cares about some teams on the red alliance
            if (redScore > blueScore) {
                // Red won
                notificationString = context.getString(R.string.notification_score_teams_won, eventShortName, redTeamString, matchTitle, scoreString);
            } else if (redScore < blueScore) {
                // Red lost
                notificationString = context.getString(R.string.notification_score_teams_lost, eventShortName, redTeamString, matchTitle, scoreString);
            } else {
                // Red tied
                notificationString = context.getString(R.string.notification_score_teams_tied, eventShortName, redTeamString, matchTitle, scoreString);
            }
        } else if ((blueTeams.size() > 0 && redTeams.size() == 0)) {
            // The user only cares about some teams on the blue alliance
            if (blueScore > redScore) {
                // Blue won
                notificationString = context.getString(R.string.notification_score_teams_won, eventShortName, blueTeamString, matchTitle, scoreString);
            } else if (blueScore < redScore) {
                // Blue lost
                notificationString = context.getString(R.string.notification_score_teams_lost, eventShortName, blueTeamString, matchTitle, scoreString);
            } else {
                // Blue tied
                notificationString = context.getString(R.string.notification_score_teams_tied, eventShortName, blueTeamString, matchTitle, scoreString);
            }
        } else if ((blueTeams.size() > 0 && redTeams.size() > 0)) {
            // The user cares about teams on both alliances
            if (blueScore > redScore) {
                // Blue won
                notificationString = context.getString(R.string.notification_score_teams_beat_teams, eventShortName, blueTeamString, redTeamString, matchTitle, scoreString);
            } else if (blueScore < redScore) {
                // Blue lost
                notificationString = context.getString(R.string.notification_score_teams_beat_teams, eventShortName, redTeamString, blueTeamString, matchTitle, scoreString);
            } else {
                // Blue tied
                notificationString = context.getString(R.string.notification_score_teams_tied_with_teams, eventShortName, redTeamString, blueTeamString, matchTitle, scoreString);
            }
        } else {
            // We should never, ever get here but if we do...
            return null;
        }

        // We can finally build the notification!
        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(matchKey);
        String notificationTitle = r.getString(R.string.notification_score_title, eventCode, matchAbbrevTitle);
        stored.setTitle(notificationTitle);
        stored.setBody(notificationString);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(notificationTitle)
                .setContentText(notificationString)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationString);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        if (match != null) {
            match.write(c);
        }
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewMatchActivity.newInstance(c, matchKey);
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + matchKey).hashCode();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification_score, null, false);

            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.card_header);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.matchView = (MatchView) convertView.findViewById(R.id.match_details);
            holder.time = (TextView) convertView.findViewById(R.id.notification_time);
            holder.summaryContainer = convertView.findViewById(R.id.summary_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.header.setText(c.getString(R.string.gameday_ticker_event_title_format, EventHelper.shortName(eventName), EventHelper.getShortCodeForEventKey(eventKey).toUpperCase()));
        holder.title.setText(c.getString(R.string.notification_score_gameday_title, MatchHelper.getMatchTitleFromMatchKey(c, matchKey)));
        holder.time.setText(getNotificationTimeString(c));
        holder.summaryContainer.setOnClickListener(new GamedayTickerClickListener(c, this));
        match.render(false, false, false, true).getView(c, inflater, holder.matchView);

        return convertView;
    }

    private class ViewHolder {
        public TextView header;
        public TextView title;
        public MatchView matchView;
        public TextView time;
        private View summaryContainer;
    }
}
