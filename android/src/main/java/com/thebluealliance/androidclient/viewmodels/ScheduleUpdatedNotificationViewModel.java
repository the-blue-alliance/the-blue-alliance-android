package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class ScheduleUpdatedNotificationViewModel {

    private String mTitle;
    private String mDetails;
    private String mTimeString;
    private Intent mIntent;

    public ScheduleUpdatedNotificationViewModel(String title, String details, String timeString, Intent intent) {
        mTitle = title;
        mDetails = details;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
