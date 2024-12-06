package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.views.MatchView;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScoreNotification extends BaseNotification<ScoreNotificationViewModel> {

    private final MatchWriter mWriter;
    private final MatchRenderer mRenderer;
    private final Gson mGson;
    private String eventName, eventKey, matchKey;
    private Match match;

    public ScoreNotification(String messageData, MatchWriter writer, MatchRenderer matchRenderer, Gson gson) {
        super(NotificationTypes.MATCH_SCORE, messageData);
        mWriter = writer;
        mRenderer = matchRenderer;
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
        this.match = mGson.fromJson(match, Match.class);
        this.matchKey = this.match.getKey();
        this.eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();

        matchKey = match.getKey();

        String matchTitle = MatchHelper.getMatchTitleFromMatchKey(context, matchKey);
        String matchAbbrevTitle = MatchHelper.getAbbrevMatchTitleFromMatchKey(context, matchKey);

        IMatchAlliancesContainer alliances = match.getAlliances();

        int redScore = Match.getRedScore(alliances);
        int blueScore = Match.getBlueScore(alliances);

        // Boldify the team numbers that the user is following
        ArrayList<String> redTeams = Match.teamNumbers(Match.getRedTeams(alliances));
        ArrayList<String> blueTeams = Match.teamNumbers(Match.getBlueTeams(alliances));
        Predicate<String> isFollowing = teamNumber -> followsChecker.followsTeam(context,
          teamNumber, matchKey, NotificationTypes.MATCH_SCORE);
        CharSequence firstTeams = Utilities.boldNameList(redTeams, isFollowing);
        CharSequence secondTeams = Utilities.boldNameList(blueTeams, isFollowing);

        // Make sure the score string is formatted properly with the winning score first
        String scoreString;
        if (blueScore > redScore) {
            scoreString = blueScore + "-" + redScore;
            CharSequence temp = firstTeams;
            firstTeams = secondTeams;
            secondTeams = temp;
        } else {
            scoreString = redScore + "-" + blueScore;
        }

        MatchType matchType = MatchType.fromShortType(match.getCompLevel());
        boolean useSpecial2015Format = match.getYear() == 2015 && matchType != MatchType.FINAL;

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
        stored.setSystemId(getNotificationId());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationBody);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally() {
        if (match != null) {
            //TODO need last-modified time in notifications
            mWriter.write(match, new Date().getTime());
        }
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

        MatchListElement renderedMatch = mRenderer.renderFromModel(match, MatchRenderer.RENDER_NOTIFICATION);
        if (renderedMatch != null) {
            renderedMatch.getView(c, inflater, holder.matchView);
        }

        return convertView;
    }

    @Nullable
    @Override
    public ScoreNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context, EventHelper.shortName(eventName), eventKey);
        String title = context.getString(R.string.notification_score_gameday_title, MatchHelper.getMatchTitleFromMatchKey(context, matchKey));
        return new ScoreNotificationViewModel(header, title, getNotificationTimeString(context), getIntent(context), match);
    }

    private static class ViewHolder {
        public TextView header;
        public TextView title;
        public MatchView matchView;
        public TextView time;
        private View summaryContainer;
    }
}
