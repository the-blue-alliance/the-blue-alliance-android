package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventTeamWriter implements Action1<EventTeam> {
    private Database mDb;

    @Inject
    public EventTeamWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(EventTeam eventTeam) {
        Schedulers.io().createWorker().schedule(() -> mDb.getEventTeamsTable().add(eventTeam));
    }
}