package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class EventTeamWriter extends BaseDbWriter<EventTeam> {
    @Inject
    public EventTeamWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(EventTeam eventTeam) {
        mDb.getEventTeamsTable().add(eventTeam);
    }
}