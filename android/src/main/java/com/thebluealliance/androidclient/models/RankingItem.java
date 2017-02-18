package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.ITeamRecord;

import java.util.List;

import javax.annotation.Nullable;

public class RankingItem implements IRankingItem {

    private Integer matchesPlayed;
    private Integer dq;
    private Integer rank;
    private List<Double> sortOrders;
    private String teamKey;

    private @Nullable ITeamRecord record;
    private @Nullable Double qualAverage;

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

    @Override @Nullable public ITeamRecord getRecord() {
        return record;
    }

    @Override public void setRecord(@Nullable ITeamRecord record) {
        this.record = record;
    }

    @Override @Nullable public Double getQualAverage() {
        return qualAverage;
    }

    @Override public void setQualAverage(@Nullable Double qualAverage) {
        this.qualAverage = qualAverage;
    }

    public static class TeamRecord implements ITeamRecord {
        private Integer wins;
        private Integer losses;
        private Integer ties;

        @Override public Integer getWins() {
            return wins;
        }

        @Override public void setWins(Integer wins) {
            this.wins = wins;
        }

        @Override public Integer getLosses() {
            return losses;
        }

        @Override public void setLosses(Integer losses) {
            this.losses = losses;
        }

        @Override public Integer getTies() {
            return ties;
        }

        @Override public void setTies(Integer ties) {
            this.ties = ties;
        }

        public static String buildRecordString(ITeamRecord record) {
            if (record.getWins() == null
                || record.getLosses() == null
                || record.getTies() == null) {
                return "";
            }
            StringBuilder recordBuilder = new StringBuilder();
            recordBuilder.append("(");
            recordBuilder.append(record.getWins());
            recordBuilder.append("-");
            recordBuilder.append(record.getLosses());
            if (record.getTies() > 0) {
                recordBuilder.append("-");
                recordBuilder.append(record.getTies());
            }
            recordBuilder.append(")");
            return recordBuilder.toString();
        }

        public static boolean isEmpty(ITeamRecord record) {
            if (record.getWins() == null
                || record.getLosses() == null
                || record.getTies() == null) {
                return true;
            }

            return record.getWins() == 0
                    && record.getLosses() == 0
                    && record.getTies() == 0;
        }
    }
}
