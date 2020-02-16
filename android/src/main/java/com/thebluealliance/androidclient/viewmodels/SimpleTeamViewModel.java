package com.thebluealliance.androidclient.viewmodels;

public class SimpleTeamViewModel extends BaseViewModel {

    private String mTeamKey;
    private String mTeamNickname;
    private String mTeamLocation;
    private int mYear;

    public SimpleTeamViewModel(String teamKey, String teamNickname, String teamLocation, int year) {
        mTeamKey = teamKey;
        mTeamNickname = teamNickname;
        mTeamLocation = teamLocation;
        mYear = year;
    }

    public String getTeamKey() {
        return mTeamKey;
    }

    public String getTeamNickname() {
        return mTeamNickname;
    }

    public String getTeamLocation() {
        return mTeamLocation;
    }

    public int getYear() {
        return mYear;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleTeamViewModel)) {
            return false;
        }

        SimpleTeamViewModel model = (SimpleTeamViewModel) o;
        return mTeamKey.equals(model.getTeamKey());
    }

    @Override
    public int hashCode() {
        return mTeamKey.hashCode();
    }
}
