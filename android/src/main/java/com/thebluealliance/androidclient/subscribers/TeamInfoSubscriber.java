package com.thebluealliance.androidclient.subscribers;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.models.Team;

public class TeamInfoSubscriber extends BaseAPISubscriber<Team, Team>{

    private Team mTeam;

    @Override
    public void parseData() {
        // No parsing needed here
    }

    @Override
    public @Nullable Team getData() {
        return mTeam;
    }

    @Override
    public void onNext(Team team) {
        mTeam = team;
    }
}
