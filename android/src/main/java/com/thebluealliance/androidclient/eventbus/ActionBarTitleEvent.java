package com.thebluealliance.androidclient.eventbus;

public class ActionBarTitleEvent {

    private String mTitle;

    public ActionBarTitleEvent(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
