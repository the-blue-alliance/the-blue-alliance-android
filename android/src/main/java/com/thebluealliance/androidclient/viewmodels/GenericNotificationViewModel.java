package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class GenericNotificationViewModel {

    private String mHeader;
    private String mTitle;
    private String mSummary;
    private String mTimeString;
    private Intent mIntent;

    public GenericNotificationViewModel(String header, String title, String summary, String timeString, Intent intent) {
        mHeader = header;
        mTitle = title;
        mSummary = summary;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
