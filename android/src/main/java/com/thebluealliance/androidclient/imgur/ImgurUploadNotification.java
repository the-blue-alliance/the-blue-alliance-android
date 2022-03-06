package com.thebluealliance.androidclient.imgur;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import com.thebluealliance.androidclient.R;

public class ImgurUploadNotification {
    private int mNotificationId;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private Resources mResources;

    public ImgurUploadNotification(Context context) {
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationId = (int) System.currentTimeMillis();
        mResources = context.getApplicationContext().getResources();
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setColor(mResources.getColor(R.color.primary));
    }

    public void onUploadStarting() {
        mBuilder.setContentTitle(mResources.getString(R.string.imgur_upload_uploading));
        mBuilder.setProgress(0, 0, true);
        postNotification();
    }

    public void onUploadSuccess() {
        mBuilder.setContentTitle(mResources.getString(R.string.imgur_upload_success_title));
        setContentText(mResources.getString(R.string.imgur_upload_success_message));
        mBuilder.setProgress(0, 0, false);
        postNotification();
    }

    public void onUploadFailure() {
        mBuilder.setContentTitle(mResources.getString(R.string.imgur_upload_failure_title));
        setContentText(mResources.getString(R.string.imgur_upload_failure_message));
        mBuilder.setProgress(0, 0, false);
        postNotification();
    }

    private void setContentText(String message) {
        mBuilder.setContentText(message);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(message);
        mBuilder.setStyle(style);
    }

    private void postNotification() {
        mManager.notify(mNotificationId, mBuilder.build());
    }
}
