package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;

import javax.inject.Inject;

public class EventTeamWriter extends BaseDbWriter<EventTeam> {
    @Inject
    public EventTeamWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(EventTeam eventTeam, Long lastModified) {
        mDb.getEventTeamsTable().add(eventTeam, lastModified);
    }
}