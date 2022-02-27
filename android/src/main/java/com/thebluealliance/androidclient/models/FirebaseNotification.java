package com.thebluealliance.androidclient.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

public class FirebaseNotification {
    @JsonIgnore @Inject DatabaseWriter writer;
    @JsonIgnore @Inject MatchRenderer matchRenderer;

    private String time;
    private Map<String, Object> payload;

    @JsonIgnore private String jsonString;
    @JsonIgnore private BaseNotification notification;
    @JsonIgnore private static final DateFormat DATE_FORMAT;
    @JsonIgnore private final Gson gson;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public FirebaseNotification(Gson gson) {
        jsonString = "";
        notification = null;
        this.gson = gson;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public String getTime() {
        return time;
    }

    public String convertToJson() {
        if (jsonString.isEmpty()) {
            jsonString = JSONHelper.getGson().toJson(payload);
        }
        return jsonString;
    }

    public String getNotificationType() {
        JsonObject message = JSONHelper.getasJsonObject(convertToJson());
        return message.get("message_type").getAsString();
    }

    public BaseNotification getNotification() {
        if (notification != null) {
            return notification;
        }
        JsonObject message = JSONHelper.getasJsonObject(convertToJson());
        String messageType = message.get("message_type").getAsString();
        String messageData = message.get("message_data").toString();
        Date date = null;
        try {
            date = DATE_FORMAT.parse(getTime());
        } catch (ParseException e) {
            // Improper date format
            e.printStackTrace();
        }
        switch (messageType) {
            case NotificationTypes.MATCH_SCORE:
                notification = new ScoreNotification(
                        messageData,
                        writer.getMatchWriter().get(),
                        matchRenderer,
                        this.gson);
                break;
            case NotificationTypes.UPCOMING_MATCH:
                notification = new UpcomingMatchNotification(messageData, this.gson);
                break;
            case NotificationTypes.ALLIANCE_SELECTION:
                notification = new AllianceSelectionNotification(messageData, writer.getEventWriter().get(), this.gson);
                break;
            case NotificationTypes.LEVEL_STARTING:
                notification = new CompLevelStartingNotification(messageData);
                break;
            case NotificationTypes.SCHEDULE_UPDATED:
                notification = new ScheduleUpdatedNotification(messageData);
                break;
            case NotificationTypes.AWARDS:
                notification = new AwardsPostedNotification(messageData, writer.getAwardListWriter().get(), this.gson);
                break;
            case NotificationTypes.DISTRICT_POINTS_UPDATED:
                notification = new DistrictPointsUpdatedNotification(messageData);
                break;
        }

        notification.setDate(date);
        try {
            notification.parseMessageData();
        } catch (JsonParseException e) {
            // There's really no graceful way to recover from this. Let's hope for the best.
            e.printStackTrace();
            return null;
        }

        return notification;
    }
}
