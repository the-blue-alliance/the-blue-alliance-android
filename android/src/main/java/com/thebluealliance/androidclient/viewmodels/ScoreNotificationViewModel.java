package com.thebluealliance.androidclient.viewmodels;

import com.thebluealliance.androidclient.models.Match;

import android.content.Intent;

public class ScoreNotificationViewModel {

    private String mHeader;
    private String mTitle;
    private String mNotificationTime;
    private Intent mIntent;

    private Match mMatch;

    public ScoreNotificationViewModel(String header, String title, String notificationTime, Intent intent, Match match) {
        mHeader = header;
        mTitle = title;
        mNotificationTime = notificationTime;
        mIntent = intent;
        mMatch = match;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getNotificationTime() {
        return mNotificationTime;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public Match getMatch() {
        return mMatch;
    }

}
