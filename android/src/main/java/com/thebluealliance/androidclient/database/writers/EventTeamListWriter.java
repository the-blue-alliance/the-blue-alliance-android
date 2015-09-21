package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventTeamListWriter implements Action1<List<EventTeam>> {
    private Database mDb;

    @Inject
    public EventTeamListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<EventTeam> eventTeams) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getEventTeamsTable().add(ImmutableList.copyOf(eventTeams)));
    }
}