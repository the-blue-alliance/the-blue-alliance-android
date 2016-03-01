package com.thebluealliance.androidclient.viewmodels;

public class AllianceSelectionNotificationViewModel {

    private String mTitle;
    private String mTimeString;

    public AllianceSelectionNotificationViewModel(String title, String timeString) {
        mTitle = title;
        mTimeString = timeString;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTimeString() {
        return mTimeString;
    }
}
