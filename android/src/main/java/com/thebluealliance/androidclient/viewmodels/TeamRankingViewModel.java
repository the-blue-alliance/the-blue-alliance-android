package com.thebluealliance.androidclient.viewmodels;

public class TeamRankingViewModel extends BaseViewModel {

    private String mTeamKey;
    private String mTeamNickname;
    private String mTeamNumber;
    private int mRank;
    private String mRecord;
    private String mRankingSummary;
    private String mRankingBreakdown;

    public TeamRankingViewModel(String teamKey, String teamNickname, String teamNumber, int rank,
                                String record, String rankingSummary, String rankingBreakdown) {
        mTeamKey = teamKey;
        mTeamNickname = teamNickname;
        mTeamNumber = teamNumber;
        mRank = rank;
        mRecord = record;
        mRankingSummary = rankingSummary;
        mRankingBreakdown = rankingBreakdown;
    }

    public String getTeamKey() {
        return mTeamKey;
    }

    public String getTeamNickname() {
        return mTeamNickname;
    }

    public String getTeamNumber() {
        return mTeamNumber;
    }

    public int getRank() {
        return mRank;
    }

    public String getRecord() {
        return mRecord;
    }

    public String getRankingBreakdown() {
        return mRankingBreakdown;
    }

    public String getRankingSummary() {
        return mRankingSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TeamRankingViewModel)) {
            return false;
        }

        TeamRankingViewModel model = (TeamRankingViewModel) o;

        return mTeamKey.equals(model.getTeamKey())
                && mTeamNickname.equals(model.getTeamNickname())
                && mTeamNumber.equals(model.getTeamNumber())
                && mRank == model.getRank()
                && mRecord.equals(model.getRecord())
                && mRankingSummary.equals(model.getRankingSummary())
                && mRankingBreakdown.equals(model.getRankingBreakdown());
    }

    @Override
    public int hashCode() {
        return hashFromValues(mTeamKey, mTeamNickname, mTeamNumber, mRank, mRecord,
                              mRankingSummary, mRankingBreakdown);
    }
}
