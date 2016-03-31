package com.thebluealliance.androidclient.viewmodels;

public class TeamViewModel {

    private String mTeamKey;
    private int mTeamNumber;
    private String mTeamName;
    private String mTeamLocation;
    private boolean mShowLinkToTeamDetails = false;
    private boolean mShowMyTbaDetails = false;

    public TeamViewModel(String teamKey, int teamNumber, String teamName, String teamLocation) {
        mTeamKey = teamKey;
        mTeamNumber = teamNumber;
        mTeamName = teamName;
        mTeamLocation = teamLocation;

    }

    public String getTeamKey() {
        return mTeamKey;
    }

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public String getTeamLocation() {
        return mTeamLocation;
    }

    public void setShowLinkToTeamDetails(boolean show) {
        mShowLinkToTeamDetails = show;
    }

    public boolean shouldShowLinkToTeamDetails() {
        return mShowLinkToTeamDetails;
    }

    public void setShowMyTbaDetails(boolean show) {
        mShowMyTbaDetails = show;
    }

    public boolean shouldShowMyTbaDetails() {
        return mShowMyTbaDetails;
    }
}
