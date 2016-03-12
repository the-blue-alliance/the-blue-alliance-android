package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class UpcomingMatchNotification extends BaseNotification<GenericNotificationViewModel> {

    private String eventName, eventKey, matchKey, redTeams[], blueTeams[];
    private JsonElement matchTime;
    private JsonArray teamKeys;

    public UpcomingMatchNotification(String messageData) {
        super("upcoming_match", messageData);
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public String[] getRedTeams() {
        return redTeams;
    }

    public String[] getBlueTeams() {
        return blueTeams;
    }

    public JsonElement getMatchTime() {
        return matchTime;
    }

    public JsonArray getTeamKeys() {
        return teamKeys;
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
        ArrayList<String> teamNumbers = Match.teamNumbers(teamKeys);
        int allianceSize = teamNumbers.size() / 2;
        redTeams = new String[allianceSize];
        blueTeams = new String[allianceSize];
        for (int i = 0; i < allianceSize; ++i) {
            redTeams[i] = teamNumbers.get(i);
            blueTeams[i] = teamNumbers.get(i + allianceSize);
        }
        if (jsonData.has("scheduled_time")) {
            matchTime = jsonData.get("scheduled_time");
        } else {
            matchTime = JsonNull.INSTANCE;
        }
    }

    /**
     * @param context a Context object for use by the notification builder
     * @param followsChecker for checking which teams the user follows
     * @return A constructed notification
     */
    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        String scheduledStartTimeString;
        if (JSONHelper.isNull(matchTime)) {
            scheduledStartTimeString = "";
        } else {
            long scheduledStartTimeUNIX = matchTime.getAsLong();
            // We multiply by 1000 because the Date constructor expects ms
            Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
            java.text.DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
            scheduledStartTimeString = format.format(scheduledStartTime);
        }

        // Boldify the team numbers that the user is following.
        Predicate<String> isFollowing = teamNumber -> followsChecker.followsTeam(context,
                teamNumber, matchKey, NotificationTypes.UPCOMING_MATCH);
        CharSequence redTeamNumbers = Utilities.boldNameList(Arrays.asList(redTeams), isFollowing);
        CharSequence blueTeamNumbers = Utilities.boldNameList(Arrays.asList(blueTeams), isFollowing);

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(context, matchKey);
        String matchAbbrevTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, matchKey);
        String eventShortName = EventHelper.shortName(eventName);
        String template = scheduledStartTimeString.isEmpty()
                ? context.getString(R.string.notification_upcoming_match_no_time)
                : context.getString(R.string.notification_upcoming_match);
        CharSequence contentText = TextUtils.expandTemplate(template, eventShortName, matchTitle,
                redTeamNumbers, blueTeamNumbers, scheduledStartTimeString);

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(matchKey);
        String notificationTitle = context.getString(R.string.notification_upcoming_match_title, eventCode, matchAbbrevTitle);
        stored.setTitle(notificationTitle);
        stored.setBody(contentText.toString());
        stored.setMessageData(messageData);
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
    public void updateDataLocally() {
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
        new MatchListElement(redTeams, blueTeams, matchKey, JSONHelper.isNull(matchTime) ? -1 : matchTime.getAsLong(), "").getView(c, inflater, holder.matchView);

        return convertView;
    }

    @Nullable
    @Override
    public GenericNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        return new GenericNotificationViewModel(messageType, messageData);
    }

    private static class ViewHolder {
        public TextView header;
        public TextView title;
        public MatchView matchView;
        public TextView time;
        private View summaryContainer;
    }
}
