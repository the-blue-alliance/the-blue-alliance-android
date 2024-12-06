package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.helpers.WebcastHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.types.WebcastType;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingMatchNotification extends BaseNotification<UpcomingMatchNotificationViewModel> {

    private final Gson mGson;
    private String eventName, eventKey, matchKey;
    private String[] redTeams, blueTeams;
    private JsonElement matchTime;
    private JsonArray teamKeys;
    private JsonElement webcast;

    public UpcomingMatchNotification(String messageData, Gson gson) {
        super("upcoming_match", messageData);
        mGson = gson;
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

    public JsonElement getWebcast() {
        return webcast;
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
        ArrayList<String> teamKeyList = mGson.fromJson(teamKeys, new TypeToken<List<String>>(){}.getType());
        int allianceSize = teamKeyList.size() / 2;
        redTeams = new String[allianceSize];
        blueTeams = new String[allianceSize];
        for (int i = 0; i < allianceSize; ++i) {
            redTeams[i] = teamKeyList.get(i).substring(3);
            blueTeams[i] = teamKeyList.get(i + allianceSize).substring(3);
        }
        if (jsonData.has("scheduled_time")) {
            matchTime = jsonData.get("scheduled_time");
        } else {
            matchTime = JsonNull.INSTANCE;
        }
        if (jsonData.has("webcast")) {
            webcast = jsonData.get("webcast").getAsJsonObject();
        } else {
            webcast = JsonNull.INSTANCE;
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

        // Boldify the team numbers that the user is following
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
        stored.setSystemId(getNotificationId());

        PendingIntent watchIntent = null;
        String watchTitle = null;
        if (webcast != null && webcast.isJsonObject()) {
            JsonObject webcastJson = webcast.getAsJsonObject();
            WebcastType webcastType = WebcastHelper.getType(webcastJson.get("type").getAsString());
            if (webcastType != WebcastType.NONE) {
                watchTitle = webcastType.render(context);
                Intent webcastIntent = WebcastHelper.getIntentForWebcast(context,
                                                                         matchKey,
                                                                         webcastType,
                                                                         webcastJson,
                                                                         0);
                int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
                watchIntent = PendingIntent.getActivity(
                        context,
                        (int)System.currentTimeMillis(),
                        webcastIntent,
                        flags
                );
            }
        }

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(notificationTitle)
                .setContentText(contentText);

        // Add Watch button
        if (watchIntent != null) {
            builder.addAction(R.drawable.ic_videocam_black_24dp, watchTitle, watchIntent);
        }

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
        return (getNotificationType() + ":" + matchKey).hashCode();
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
        new MatchListElement(redTeams, blueTeams, matchKey, JSONHelper.isNull(matchTime) ? -1 : matchTime.getAsLong(), null).getView(c, inflater, holder.matchView);

        return convertView;
    }

    @Nullable
    @Override
    public UpcomingMatchNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context, EventHelper.shortName(eventName), eventKey);
        String title = context.getString(R.string.notification_upcoming_match_gameday_title, MatchHelper.getMatchTitleFromMatchKey(context, matchKey));
        long time = (JSONHelper.isNull(matchTime) ? -1 : matchTime.getAsLong());
        return new UpcomingMatchNotificationViewModel(header, title, getNotificationTimeString(context), getIntent(context), matchKey, redTeams, blueTeams, time);
    }

    private static class ViewHolder {
        public TextView header;
        public TextView title;
        public MatchView matchView;
        public TextView time;
        private View summaryContainer;
    }
}
