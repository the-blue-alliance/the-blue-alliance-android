package com.thebluealliance.androidclient.database.writers;

import android.support.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventTeam;

import java.util.List;

import javax.inject.Inject;

public class EventTeamListWriter extends BaseDbWriter<List<EventTeam>> {
    @Inject
    public EventTeamListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<EventTeam> eventTeams, Long lastModified) {
        mDb.getEventTeamsTable().add(ImmutableList.copyOf(eventTeams), lastModified);
    }
}