package com.thebluealliance.androidclient.viewmodels;

public class EventViewModel extends BaseViewModel {

    private String mKey;
    private int mYear;
    private String mShortName;
    private String mDateString;
    private String mLocation;
    private String mDistrictString;
    private boolean mShowMyTbaSettings = false;

    public EventViewModel(String key, int year, String shortName, String dateString, String
            location, String districtString) {
        mKey = key;
        mYear = year;
        mShortName = shortName;
        mDateString = dateString;
        mLocation = location;
        mDistrictString = districtString;
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

    public String getDistrictString() {
        return mDistrictString;
    }

    public void setDistrictString(String districtString) {
        mDistrictString = districtString;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof EventViewModel)) {
            return false;
        }

        EventViewModel model = (EventViewModel) o;

        return mKey.equals(model.getKey())
                && mYear == model.getYear()
                && mShortName.equals(model.getShortName())
                && mDateString.equals(model.getDateString())
                && mLocation.equals(model.getLocation())
                && mShowMyTbaSettings == shouldShowMyTbaSettings();
    }

    @Override
    public int hashCode() {
        return hashFromValues(mKey, mYear, mShortName, mDateString, mLocation, mShowMyTbaSettings);
    }
}
