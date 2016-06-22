package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventInfo;
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

    /**
     * Get a list of teams attending the specified event.
     *
     * @param eventKey key of the event to fetch teams for
     * @return Observable that will emit the list of teams at an event
     */
    public Observable<List<Team>> getEventTeams(String eventKey) {
        return mDb.getEventTeamsTable().getTeamsObservable(eventKey);
    }

    /**
     * Gets the data necessary to show an "event info" screen. Includes basic event details,
     * rankings, stats, and an event's matches.
     *
     * @param eventKey the key of the event
     * @return an object wrapping the necessary details about an event
     */
    public Observable<EventInfo> getEventInfo(String eventKey) {
        return Observable.combineLatest(
                mDb.getEventsTable().getObservable(eventKey),
                mDb.getMatchesTable().getEventMatchesObservable(eventKey),
                (event, matches) -> {
                    EventInfo info = new EventInfo();
                    info.event = event;
                    info.matches = matches;
                    return info;
                });
    }
}
