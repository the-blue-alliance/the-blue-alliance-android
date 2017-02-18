package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IMatchAlliance;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import java.util.List;

import javax.annotation.Nullable;

public class MatchAlliancesContainer implements IMatchAlliancesContainer {

    private IMatchAlliance red;
    private IMatchAlliance blue;

    @Override public IMatchAlliance getRed() {
        return red;
    }

    @Override public void setRed(IMatchAlliance red) {
        this.red = red;
    }

    @Override public IMatchAlliance getBlue() {
        return blue;
    }

    @Override public void setBlue(IMatchAlliance blue) {
        this.blue = blue;
    }

    public static class MatchAlliance implements IMatchAlliance {
        private Integer score;
        private List<String> teamKeys;
        private @Nullable List<String> surrogateTeamKeys;

        @Override public Integer getScore() {
            return score;
        }

        @Override public void setScore(Integer score) {
            this.score = score;
        }

        @Override @Nullable public List<String> getSurrogateTeamKeys() {
            return surrogateTeamKeys;
        }

        public void setSurrogateTeamKeys(@Nullable List<String> surrogateTeamKeys) {
            this.surrogateTeamKeys = surrogateTeamKeys;
        }

        @Override public List<String> getTeamKeys() {
            return teamKeys;
        }

        @Override public void setTeamKeys(List<String> teamKeys) {
            this.teamKeys = teamKeys;
        }

    }
}
