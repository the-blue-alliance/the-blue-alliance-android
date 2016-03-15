package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class CompLevelStartingNotificationViewModel {

    private String mHeader;
    private String mDetails;
    private String mNotificationTime;
    private Intent mIntent;

    public CompLevelStartingNotificationViewModel(String header, String details, String notificationTime, Intent intent) {
        mHeader = header;
        mDetails = details;
        mNotificationTime = notificationTime;
        mIntent = intent;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getTimeString() {
        return mNotificationTime;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
