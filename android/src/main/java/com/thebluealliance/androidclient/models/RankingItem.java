package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingItem;

import java.util.List;

import javax.annotation.Nullable;

public class RankingItem implements IRankingItem {

    private Integer matchesPlayed;
    private Integer dq;
    private Integer rank;
    private List<Double> sortOrders;
    private String teamKey;

    private @Nullable Integer wins;
    private @Nullable Integer losses;
    private @Nullable Integer ties;
    private @Nullable Double qualAverage;
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

    @Override @Nullable public Integer getWins() {
        return wins;
    }

    @Override public void setWins(@Nullable Integer wins) {
        this.wins = wins;
    }

    @Override @Nullable public Integer getLosses() {
        return losses;
    }

    @Override public void setLosses(@Nullable Integer losses) {
        this.losses = losses;
    }

    @Override @Nullable public Integer getTies() {
        return ties;
    }

    @Override public void setTies(@Nullable Integer ties) {
        this.ties = ties;
    }

    @Override @Nullable public Double getQualAverage() {
        return qualAverage;
    }

    @Override public void setQualAverage(@Nullable Double qualAverage) {
        this.qualAverage = qualAverage;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }
}
