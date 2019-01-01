package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

import com.thebluealliance.androidclient.models.Match;

public class ScoreNotificationViewModel extends BaseViewModel {

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

    @Override public boolean equals(Object o) {
        if (!(o instanceof ScoreNotificationViewModel)) {
            return false;
        }

        ScoreNotificationViewModel model = (ScoreNotificationViewModel) o;

        new int[]{}.hashCode();

        return mHeader.equals(model.getHeader())
                && mTitle.equals(model.getTitle())
                && mNotificationTime.equals(model.getNotificationTime())
                && mMatch.equals(model.getMatch());
    }

    @Override public int hashCode() {
        return hashFromValues(mHeader, mTitle, mNotificationTime, mMatch);
    }
}
