package com.thebluealliance.androidclient.eventbus;

public class ActionBarTitleEvent {

    private String mTitle;
    private String mSubtitle;

    public ActionBarTitleEvent(String title) {
        mTitle = title;
    }

    public ActionBarTitleEvent(String title, String subtitle) {
        mTitle = title;
        mSubtitle = subtitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }
}
