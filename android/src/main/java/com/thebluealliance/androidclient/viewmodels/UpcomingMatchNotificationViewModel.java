package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

import java.util.Arrays;

public class UpcomingMatchNotificationViewModel extends BaseViewModel {

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

    @Override public boolean equals(Object o) {
        if (!(o instanceof UpcomingMatchNotificationViewModel)) {
            return false;
        }

        UpcomingMatchNotificationViewModel model = (UpcomingMatchNotificationViewModel) o;

        return mHeader.equals(model.getHeader())
                && mTitle.equals(model.getTitle())
                && mNotificationTime.equals(model.getNotificationTime())
                && mMatchKey.equals(model.getMatchKey())
                && mMatchTime == model.getMatchTime()
                && Arrays.equals(mRedTeams, model.getRedTeams())
                && Arrays.equals(mBlueTeams, model.getBlueTeams());
    }

    @Override public int hashCode() {
        return hashFromValues(mHeader, mTitle, mNotificationTime, mMatchKey, mMatchTime, mRedTeams, mBlueTeams);
    }
}
