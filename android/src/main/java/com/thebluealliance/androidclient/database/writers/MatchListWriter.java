package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

import java.util.List;

import javax.inject.Inject;

public class MatchListWriter extends BaseDbWriter<List<Match>> {
    @Inject
    public MatchListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Match> matches, Long lastModified) {
        mDb.getMatchesTable().add(ImmutableList.copyOf(matches), lastModified);
    }
}