package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Func2;

public class TeamPageCombiner implements Func2<List<Team>, List<Team>, List<Team>> {

    @Inject
    public TeamPageCombiner() {

    }

    @Override
    public List<Team> call(List<Team> teams, List<Team> teams2) {
        teams.addAll(teams2);
        return teams;
    }
}
