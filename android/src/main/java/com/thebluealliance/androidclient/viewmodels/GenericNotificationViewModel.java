package com.thebluealliance.androidclient.viewmodels;

import android.content.Intent;

public class GenericNotificationViewModel extends BaseViewModel {

    private String mHeader;
    private String mTitle;
    private String mSummary;
    private String mTimeString;
    private Intent mIntent;

    public GenericNotificationViewModel(String header, String title, String summary, String timeString, Intent intent) {
        mHeader = header;
        mTitle = title;
        mSummary = summary;
        mTimeString = timeString;
        mIntent = intent;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getTimeString() {
        return mTimeString;
    }

    public Intent getIntent() {
        return mIntent;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof GenericNotificationViewModel)) {
            return false;
        }

        GenericNotificationViewModel model = (GenericNotificationViewModel) o;

        return mHeader.equals(model.getHeader())
                && mTitle.equals(model.getTitle())
                && mSummary.equals(model.getSummary())
                && mTimeString.equals(model.getTimeString());
    }

    @Override public int hashCode() {
        return hashFromValues(mHeader, mTitle, mSummary, mTimeString);
    }
}
