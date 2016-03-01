package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class AwardsPostedNotificationViewModel {

    private String mEventKey;
    private String mEventName;
    private String mTimeString;
    private Intent mIntent;

    public AwardsPostedNotificationViewModel(String eventKey, String eventName, String timeString, Intent intent) {
        mEventKey = eventKey;
        mEventName = eventName;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getEventKey() {
        return mEventKey;
    }

    public String getEventName() {
        return mEventName;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
