package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IDistrictRanking;
import com.thebluealliance.api.model.IDistrictRankingEventPoints;

import javax.annotation.Nullable;

public class DistrictRanking implements IDistrictRanking {

    private Integer pointTotal;
    private Integer rank;
    private Integer rookieBonus;
    private String teamKey;
    private @Nullable IDistrictRankingEventPoints eventPoints;
    private @Nullable Long lastModified;

    @Override
    public Integer getPointTotal() {
        return pointTotal;
    }

    @Override
    public void setPointTotal(Integer pointTotal) {
        this.pointTotal = pointTotal;
    }

    @Override
    public Integer getRank() {
        return rank;
    }

    @Override
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public Integer getRookieBonus() {
        return rookieBonus;
    }

    @Override
    public void setRookieBonus(Integer rookieBonus) {
        this.rookieBonus = rookieBonus;
    }

    @Override
    public String getTeamKey() {
        return teamKey;
    }

    @Override
    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    @Override @Nullable
    public IDistrictRankingEventPoints getEventPoints() {
        return eventPoints;
    }

    public void setEventPoints(@Nullable IDistrictRankingEventPoints eventPoints) {
        this.eventPoints = eventPoints;
    }

    @Override @Nullable
    public Long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }
}
