package com.thebluealliance.androidclient.viewmodels;

public class EventViewModel extends BaseViewModel {

    private String mKey;
    private int mYear;
    private String mShortName;
    private String mDateString;
    private String mLocation;
    private boolean mShowMyTbaSettings = false;

    public EventViewModel(String key, int year, String shortName, String dateString, String location) {
        mKey = key;
        mYear = year;
        mShortName = shortName;
        mDateString = dateString;
        mLocation = location;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getDateString() {
        return mDateString;
    }

    public void setDateString(String dateString) {
        mDateString = dateString;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public boolean shouldShowMyTbaSettings() {
        return mShowMyTbaSettings;
    }

    public void setShowMyTbaSettings(boolean showMyTbaSettings) {
        mShowMyTbaSettings = showMyTbaSettings;
    }

    @Override public boolean equals(Object o) {
        return false;
    }

    @Override public int hashCode() {
        return hashFromValues(mKey, mYear, mShortName, mDateString, mLocation, mShowMyTbaSettings);
    }
}
