package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

public class DistrictPointsUpdatedNotification
        extends BaseNotification<GenericNotificationViewModel> {

    private String districtName, districtKey;

    public DistrictPointsUpdatedNotification(String messageData) {
        super(NotificationTypes.DISTRICT_POINTS_UPDATED, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("district_name")) {
            throw new JsonParseException("district_name required");
        }
        districtName = jsonData.get("district_name").getAsString();

        if (!jsonData.has("district_key")) {
            throw new JsonParseException("district_key is required");
        }
        districtKey = jsonData.get("district_key").getAsString();
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getDistrictKey() {
        return districtKey;
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();

        String contentText = r.getString(R.string.notification_district_points_updated, districtName);

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String districtCode = EventHelper.getEventCode(districtKey);
        String title = r.getString(R.string.notification_district_points_title, districtCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setMessageData(messageData);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(contentText);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewDistrictActivity.newInstance(c, districtKey);
    }

    @Override
    public void updateDataLocally() {
        /* This notification has no data that we can store locally */
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + districtKey).hashCode();
    }

    @Nullable
    @Override
    public GenericNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        Resources r = context.getResources();
        String header = getNotificationCardHeader(context, districtName, districtKey);
        String districtCode = EventHelper.getEventCode(districtKey);
        String title = r.getString(R.string.notification_district_points_title, districtCode);
        String contentText = r.getString(R.string.notification_district_points_updated, districtName);

        return new GenericNotificationViewModel(header, title, contentText,
                getNotificationTimeString(context), getIntent(context));
    }
}
