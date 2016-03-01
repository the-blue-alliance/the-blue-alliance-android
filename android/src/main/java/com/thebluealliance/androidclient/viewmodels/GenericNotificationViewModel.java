package com.thebluealliance.androidclient.viewmodels;

public class GenericNotificationViewModel {

    private String mTitle;
    private String mSummary;

    public GenericNotificationViewModel(String title, String summary) {
        mTitle = title;
        mSummary = summary;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSummary() {
        return mSummary;
    }
}
