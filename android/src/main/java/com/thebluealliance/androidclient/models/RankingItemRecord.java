package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingItemRecord;

public class RankingItemRecord implements IRankingItemRecord {

    private Integer losses;
    private Integer wins;
    private Integer ties;

    @Override public Integer getLosses() {
        return losses;
    }

    @Override public void setLosses(Integer losses) {
        this.losses = losses;
    }

    @Override public Integer getWins() {
        return wins;
    }

    @Override public void setWins(Integer wins) {
        this.wins = wins;
    }

    @Override public Integer getTies() {
        return ties;
    }

    @Override public void setTies(Integer ties) {
        this.ties = ties;
    }
}
