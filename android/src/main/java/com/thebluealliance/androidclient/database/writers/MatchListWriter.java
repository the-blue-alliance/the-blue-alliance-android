package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;

public class MatchListWriter extends BaseDbWriter<List<Match>> {
    @Inject
    public MatchListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Match> matches) {
        mDb.getMatchesTable().add(ImmutableList.copyOf(matches));
    }
}