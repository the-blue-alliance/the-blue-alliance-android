package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.views.MatchView;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScoreNotification extends BaseNotification {

    private final MatchWriter mWriter;
    private String eventName, eventKey, matchKey;
    private Match match;

    public ScoreNotification(String messageData, MatchWriter writer) {
        super(NotificationTypes.MATCH_SCORE, messageData);
        mWriter = writer;
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

    public Match getMatch() {
        return match;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("match")) {
            throw new JsonParseException("Notification data does not contain 'match");
        }
        JsonObject match = jsonData.get("match").getAsJsonObject();
        this.match = gson.fromJson(match, Match.class);
        this.matchKey = this.match.getKey();
        this.eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        matchKey = match.getKey();

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
        int blueScore = Match.getBlueScore(alliances);

        // TODO: Only boldify team numbers that the user is following
        ArrayList<String> redTeams = Match.teamNumbers(Match.getRedTeams(alliances));
        ArrayList<String> blueTeams = Match.teamNumbers(Match.getBlueTeams(alliances));
        CharSequence firstTeams = Utilities.boldNameList(redTeams);
        CharSequence secondTeams = Utilities.boldNameList(blueTeams);

        // Make sure the score string is formatted properly with the winning score first
        String scoreString;
        if (blueScore > redScore) {
            scoreString = blueScore + "-" + redScore;
            CharSequence temp = firstTeams;
            firstTeams        = secondTeams;
            secondTeams       = temp;
        } else {
            scoreString = redScore + "-" + blueScore;
        }

        boolean useSpecial2015Format;
        try {
            useSpecial2015Format = match.getYear() == 2015 && match.getType() != MatchType.FINAL;
        } catch (BasicModel.FieldNotDefinedException e) {
            useSpecial2015Format = false;
            Log.w(Constants.LOG_TAG, "Couldn't determine if we should use 2015 score format. Defaulting to no");
        }

        String eventShortName = EventHelper.shortName(eventName);
        String template;
        if (useSpecial2015Format) { // firstTeams played secondTeams (for 2015 non-finals matches)
            template = context.getString(R.string.notification_score_teams_played_teams);
        } else if (blueScore == redScore) { // firstTeams tied secondTeams
            template = context.getString(R.string.notification_score_teams_tied_teams);
        } else { // firstTeams beat secondTeams
            template = context.getString(R.string.notification_score_teams_beat_teams);
        }
        CharSequence notificationBody = TextUtils.expandTemplate(template,
                eventShortName, matchTitle, firstTeams, secondTeams, scoreString);

        // We can finally build the notification!
        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(matchKey);
        String notificationTitle = r.getString(R.string.notification_score_title, eventCode, matchAbbrevTitle);
        stored.setTitle(notificationTitle);
        stored.setBody(notificationBody.toString());
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        stored.setMessageData(messageData);

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationBody);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally() {
        if (match != null) {
            mWriter.write(match);
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
        /** TODO Move to {@link com.thebluealliance.androidclient.renderers.MatchRenderer} */
        match.render(false, false, false, true).getView(c, inflater, holder.matchView);

        return convertView;
    }

    private static class ViewHolder {
        public TextView header;
        public TextView title;
        public MatchView matchView;
        public TextView time;
        private View summaryContainer;
    }
}
