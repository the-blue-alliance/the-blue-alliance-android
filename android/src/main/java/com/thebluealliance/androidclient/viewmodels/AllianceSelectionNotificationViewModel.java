package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class AllianceSelectionNotificationViewModel extends BaseViewModel {

    private String mTitle;
    private String mTimeString;
    private Intent mIntent;

    public AllianceSelectionNotificationViewModel(String title, String timeString, Intent intent) {
        mTitle = title;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof AllianceSelectionNotificationViewModel)) {
            return false;
        }

        AllianceSelectionNotificationViewModel model = (AllianceSelectionNotificationViewModel) o;

        return mTitle.equals(model.getTitle())
                && mTimeString.equals(model.getTimeString());
    }

    @Override public int hashCode() {
        return hashFromValues(mTitle, mTimeString);
    }
}
