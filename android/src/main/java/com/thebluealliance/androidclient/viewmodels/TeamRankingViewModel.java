package com.thebluealliance.androidclient.viewmodels;

public class TeamRankingViewModel {

    private String mTeamKey;
    private String mTeamNickname;
    private int mTeamNumber;
    private int mRank;
    private String mRecord;
    private String mRankingBreakdown;

    public TeamRankingViewModel(String teamKey, String teamNickname, int teamNumber, int rank, String record, String rankingBreakdown) {
        mTeamKey = teamKey;
        mTeamNickname = teamNickname;
        mTeamNumber = teamNumber;
        mRank = rank;
        mRecord = record;
        mRankingBreakdown = rankingBreakdown;
    }

    public String getTeamKey() {
        return mTeamKey;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    public String getTeamNickname() {
        return mTeamNickname;
    }

    public void setTeamNickname(String teamNickname) {
        mTeamNickname = teamNickname;
    }

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        mTeamNumber = teamNumber;
    }

    public int getRank() {
        return mRank;
    }

    public void setRank(int rank) {
        mRank = rank;
    }

    public String getRecord() {
        return mRecord;
    }

    public void setRecord(String record) {
        mRecord = record;
    }

    public String getRankingBreakdown() {
        return mRankingBreakdown;
    }

    public void setRankingBreakdown(String rankingBreakdown) {
        mRankingBreakdown = rankingBreakdown;
    }
}
