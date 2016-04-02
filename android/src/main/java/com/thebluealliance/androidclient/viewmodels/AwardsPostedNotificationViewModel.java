package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class AwardsPostedNotificationViewModel extends BaseViewModel {

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

    @Override public boolean equals(Object o) {
        if(!(o instanceof  AwardsPostedNotificationViewModel)) {
            return false;
        }

        AwardsPostedNotificationViewModel model = (AwardsPostedNotificationViewModel) o;

        return mEventKey.equals(model.getEventKey())
                && mEventName.equals(model.getEventName())
                && mTimeString.equals(model.getTimeString());
    }

    @Override public int hashCode() {
        return hashFromValues(mEventKey, mEventName, mTimeString);
    }
}
