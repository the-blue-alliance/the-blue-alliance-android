package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingItemRecord;

import java.util.List;

import javax.annotation.Nullable;

public class RankingItem implements IRankingItem {

    private Integer matchesPlayed;
    private Integer dq;
    private Integer rank;
    private List<Double> sortOrders;
    private String teamKey;

    private @Nullable IRankingItemRecord record;
    private @Nullable Integer qualAverage;
    private @Nullable Long lastModified;

    @Override public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    @Override public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    @Override public Integer getDq() {
        return dq;
    }

    @Override public void setDq(Integer dq) {
        this.dq = dq;
    }

    @Override public Integer getRank() {
        return rank;
    }

    @Override public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override public List<Double> getSortOrders() {
        return sortOrders;
    }

    @Override public void setSortOrders(List<Double> sortOrders) {
        this.sortOrders = sortOrders;
    }

    @Override public String getTeamKey() {
        return teamKey;
    }

    @Override public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    @Override @Nullable public IRankingItemRecord getRecord() {
        return record;
    }

    public void setRecord(@Nullable IRankingItemRecord record) {
        this.record = record;
    }

    @Override @Nullable public Integer getQualAverage() {
        return qualAverage;
    }

    @Override public void setQualAverage(@Nullable Integer qualAverage) {
        this.qualAverage = qualAverage;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }
}
