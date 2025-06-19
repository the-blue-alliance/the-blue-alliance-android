package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IAllianceBackup;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingSortOrder;
import com.thebluealliance.api.model.ITeamAtEventAlliance;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;
import com.thebluealliance.api.model.ITeamAtEventQual;
import com.thebluealliance.api.model.ITeamAtEventStatus;
import com.thebluealliance.api.model.ITeamRecord;

import java.util.List;

import javax.annotation.Nullable;

public class TeamAtEventStatus implements ITeamAtEventStatus {

    private String allianceStatusStr;
    private String overallStatusStr;
    private String playoffStatusStr;

    private @Nullable ITeamAtEventAlliance alliance;
    private @Nullable ITeamAtEventPlayoff playoff;
    private @Nullable ITeamAtEventQual qual;

    @Override public String getAllianceStatusStr() {
        return allianceStatusStr;
    }

    @Override public void setAllianceStatusStr(String allianceStatusStr) {
        this.allianceStatusStr = allianceStatusStr;
    }

    @Override public String getOverallStatusStr() {
        return overallStatusStr;
    }

    @Override public void setOverallStatusStr(String overallStatusStr) {
        this.overallStatusStr = overallStatusStr;
    }

    public String getPlayoffStatusStr() {
        return playoffStatusStr;
    }

    public void setPlayoffStatusStr(String playoffStatusStr) {
        this.playoffStatusStr = playoffStatusStr;
    }

    @Override @Nullable public ITeamAtEventAlliance getAlliance() {
        return alliance;
    }

    @Override public void setAlliance(@Nullable ITeamAtEventAlliance alliance) {
        this.alliance = alliance;
    }

    @Override @Nullable public ITeamAtEventPlayoff getPlayoff() {
        return playoff;
    }

    @Override public void setPlayoff(@Nullable ITeamAtEventPlayoff playoff) {
        this.playoff = playoff;
    }

    @Override @Nullable public ITeamAtEventQual getQual() {
        return qual;
    }

    @Override public void setQual(@Nullable ITeamAtEventQual qual) {
        this.qual = qual;
    }

    public static class TeamAtEventAlliance implements ITeamAtEventAlliance {
        private String name;
        private Integer number;
        private Integer pick;

        private @Nullable IAllianceBackup backup;

        @Override public String getName() {
            return name;
        }

        @Override public void setName(String name) {
            this.name = name;
        }

        @Override public Integer getNumber() {
            return number;
        }

        @Override public void setNumber(Integer number) {
            this.number = number;
        }

        @Override public Integer getPick() {
            return pick;
        }

        @Override public void setPick(Integer pick) {
            this.pick = pick;
        }

        @Override @Nullable public IAllianceBackup getBackup() {
            return backup;
        }

        @Override public void setBackup(@Nullable IAllianceBackup backup) {
            this.backup = backup;
        }
    }

    public static class TeamAtEventPlayoff implements ITeamAtEventPlayoff {
        private String level;
        private String status;

        private @Nullable ITeamRecord currentLevelRecord;
        private @Nullable ITeamRecord record;
        private @Nullable Double playoffAverage;

        @Override public String getLevel() {
            return level;
        }

        @Override public void setLevel(String level) {
            this.level = level;
        }

        @Override public String getStatus() {
            return status;
        }

        @Override public void setStatus(String status) {
            this.status = status;
        }

        @Override @Nullable public ITeamRecord getCurrentLevelRecord() {
            return currentLevelRecord;
        }

        @Override public void setCurrentLevelRecord(@Nullable ITeamRecord currentLevelRecord) {
            this.currentLevelRecord = currentLevelRecord;
        }

        @Override @Nullable public ITeamRecord getRecord() {
            return record;
        }

        @Override public void setRecord(@Nullable ITeamRecord record) {
            this.record = record;
        }

        @Override @Nullable public Double getPlayoffAverage() {
            return playoffAverage;
        }

        @Override public void setPlayoffAverage(@Nullable Double playoffAverage) {
            this.playoffAverage = playoffAverage;
        }
    }

    public static class TeamAtEventQual implements ITeamAtEventQual {
        private IRankingItem ranking;
        private List<IRankingSortOrder> sortOrderInfo;
        private Integer numTeams;
        private String status;

        @Nullable @Override public IRankingItem getRanking() {
            return ranking;
        }

        @Override public void setRanking(IRankingItem ranking) {
            this.ranking = ranking;
        }

        @Override public List<IRankingSortOrder> getSortOrderInfo() {
            return sortOrderInfo;
        }

        @Override public void setSortOrderInfo(List<IRankingSortOrder> sortOrderInfo) {
            this.sortOrderInfo = sortOrderInfo;
        }

        @Override public Integer getNumTeams() {
            return numTeams;
        }

        public void setNumTeams(Integer numTeams) {
            this.numTeams = numTeams;
        }

        @Override public String getStatus() {
            return status;
        }

        @Override public void setStatus(String status) {
            this.status = status;
        }
    }
}
