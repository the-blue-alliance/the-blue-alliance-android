package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Implementation of a datafeed that returns Observables that automatically update with new
 * data thanks to SqlBrite.
 */
public class BriteDatafeed {

    private Database mDb;

    @Inject
    public BriteDatafeed(Database db) {
        mDb = db;
    }

    public Observable<List<Team>> getEventTeams(String eventKey) {
        return mDb.getEventTeamsTable().getTeamsObservable(eventKey);
    }
}
