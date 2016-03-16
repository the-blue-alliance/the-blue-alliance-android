package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class UpcomingMatchNotificationViewModel {

    private String mHeader;
    private String mTitle;
    private String mNotificationTime;
    private Intent mIntent;

    private String mMatchKey;
    private String[] mRedTeams, mBlueTeams;
    private long mMatchTime;

    public UpcomingMatchNotificationViewModel(String header, String title, String notificationTime, Intent intent, String matchKey, String[] redTeams, String[] blueTeams, long matchTime) {
        mHeader = header;
        mTitle = title;
        mNotificationTime = notificationTime;
        mIntent = intent;
        mMatchKey = matchKey;
        mRedTeams = redTeams;
        mBlueTeams = blueTeams;
        mMatchTime = matchTime;
    }


    public long getMatchTime() {
        return mMatchTime;
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

    public String getMatchKey() {
        return mMatchKey;
    }

    public String[] getRedTeams() {
        return mRedTeams;
    }

    public String[] getBlueTeams() {
        return mBlueTeams;
    }
}
