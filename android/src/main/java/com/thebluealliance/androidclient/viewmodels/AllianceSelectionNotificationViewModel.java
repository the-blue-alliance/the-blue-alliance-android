package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class AllianceSelectionNotificationViewModel {

    private String mTitle;
    private String mTimeString;
    private Intent mIntent;

    public AllianceSelectionNotificationViewModel(String title, String timeString, Intent intent) {
        mTitle = title;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
