package com.thebluealliance.androidclient.viewmodels;

public class CompLevelStartingNotificationViewModel {

    private String mHeader;
    private String mDetails;
    private String mNotificationTime;

    public CompLevelStartingNotificationViewModel(String header, String details, String notificationTime) {
        mHeader = header;
        mDetails = details;
        mNotificationTime = notificationTime;
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
}
