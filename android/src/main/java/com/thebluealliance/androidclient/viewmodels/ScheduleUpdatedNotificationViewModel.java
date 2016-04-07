package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class ScheduleUpdatedNotificationViewModel extends BaseViewModel{

    private String mTitle;
    private String mDetails;
    private String mTimeString;
    private Intent mIntent;

    public ScheduleUpdatedNotificationViewModel(String title, String details, String timeString, Intent intent) {
        mTitle = title;
        mDetails = details;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ScheduleUpdatedNotificationViewModel)) {
            return false;
        }

        ScheduleUpdatedNotificationViewModel model = (ScheduleUpdatedNotificationViewModel) o;

        return mTitle.equals(model.getTitle())
                && mDetails.equals(model.getDetails())
                && mTimeString.equals(model.getTimeString());
    }

    @Override public int hashCode() {
        return hashFromValues(mTitle, mDetails, mTimeString);
    }
}
