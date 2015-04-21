package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.views.MatchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nathan on 7/24/2014.
 */
public class UpcomingMatchNotification extends BaseNotification {

    private String eventName, eventKey, matchKey, redTeams[], blueTeams[];
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

        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        
        if (!jsonData.has("team_keys")) {
            throw new JsonParseException("Notification data does not contain 'team_keys");
        }
        
        eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        teamKeys = jsonData.get("team_keys").getAsJsonArray();
        redTeams = new String[teamKeys.size()/2];
        blueTeams = new String[teamKeys.size()/2];
        for(int i=0; i<teamKeys.size()/2; i++){
            redTeams[i] = teamKeys.get(i).getAsString().substring(3);
            blueTeams[i] = teamKeys.get(i+teamKeys.size()/2).getAsString().substring(3);
        }
        if (!jsonData.has("scheduled_time")) {
            throw new JsonParseException("Notification data does not contain 'scheduled_time'");
        }

        matchTime = jsonData.get("scheduled_time");
    }

    /**
     * @param context a Context object for use by the notification builder
     * @return A constructed notification
     */
    @Override
    public Notification buildNotification(Context context) {
        String scheduledStartTimeString;
        if (JSONManager.isNull(matchTime)) {
            scheduledStartTimeString = "";
        } else {
            long scheduledStartTimeUNIX = matchTime.getAsLong();
            // We multiply by 1000 because the Date constructor expects ms
            Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
            java.text.DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
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

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(context, matchKey);
        String matchAbbrevTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, matchKey);
        String eventShortName = EventHelper.shortName(eventName);

        if (scheduledStartTimeString.isEmpty()) {
            if (numFavoritedTeams == 1) {
                contentText = context.getString(R.string.notification_upcoming_match_text_single_team_no_time, eventShortName, teamsString, matchTitle);
            } else {
                contentText = context.getString(R.string.notification_upcoming_match_text_multiple_teams_no_time, eventShortName, teamsString, matchTitle);
            }
        } else {
            if (numFavoritedTeams == 1) {
                contentText = context.getString(R.string.notification_upcoming_match_text_single_team, eventShortName, teamsString, matchTitle, scheduledStartTimeString);
            } else {
                contentText = context.getString(R.string.notification_upcoming_match_text_multiple_teams, eventShortName, teamsString, matchTitle, scheduledStartTimeString);
            }
        }

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(matchKey);
        String notificationTitle = context.getString(R.string.notification_upcoming_match_title, eventCode, matchAbbrevTitle);
        stored.setTitle(notificationTitle);
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(notificationTitle)
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        /* This notification has no data that we can store locally */
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
            convertView = inflater.inflate(R.layout.list_item_notification_upcoming_match, null, false);

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
        holder.title.setText(c.getString(R.string.notification_upcoming_match_gameday_title, MatchHelper.getMatchTitleFromMatchKey(c, matchKey)));
        holder.time.setText(getNotificationTimeString(c));
        holder.summaryContainer.setOnClickListener(new GamedayTickerClickListener(c, this));
        new MatchListElement(redTeams, blueTeams, matchKey, matchTime.getAsLong(), "").getView(c, inflater, holder.matchView);

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
