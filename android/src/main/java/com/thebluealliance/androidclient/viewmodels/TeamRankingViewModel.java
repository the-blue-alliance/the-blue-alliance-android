package com.thebluealliance.androidclient.viewmodels;

public class TeamRankingViewModel extends BaseViewModel {

    private String mTeamKey;
    private String mTeamNickname;
    private String mTeamNumber;
    private int mRank;
    private String mRecord;
    private String mRankingBreakdown;

    public TeamRankingViewModel(String teamKey, String teamNickname, String teamNumber, int rank, String record, String rankingBreakdown) {
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

    public String getTeamNumber() {
        return mTeamNumber;
    }

    public void setTeamNumber(String teamNumber) {
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

    @Override public boolean equals(Object o) {
        if (!(o instanceof TeamRankingViewModel)) {
            return false;
        }

        TeamRankingViewModel model = (TeamRankingViewModel) o;

        return mTeamKey.equals(model.getTeamKey())
                && mTeamNickname.equals(model.getTeamNickname())
                && mTeamNumber.equals(model.getTeamNumber())
                && mRank == model.getRank()
                && mRecord.equals(model.getRecord())
                && mRankingBreakdown.equals(model.getRankingBreakdown());
    }

    @Override public int hashCode() {
        return hashFromValues(mTeamKey, mTeamNickname, mTeamNumber, mRank, mRecord, mRankingBreakdown);
    }
}
