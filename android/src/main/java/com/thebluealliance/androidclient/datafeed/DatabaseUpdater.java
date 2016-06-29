package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.combiners.JsonAndKeyCombiner;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAndEventTeamCombiner;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class DatabaseUpdater {

    private APIv2 mRetrofitAPI;
    private DatabaseWriter mWriter;
    private RetrofitResponseMap mResponseMap;

    @Inject
    public DatabaseUpdater(
            @Named("tba_api") APIv2 retrofitAPI,
            DatabaseWriter writer,
            RetrofitResponseMap responseMap) {
        mRetrofitAPI = retrofitAPI;
        mWriter = writer;
        mResponseMap = responseMap;
    }

    public void updateEventTeams(String eventKey, String cacheHeader) {
        mResponseMap.writeMappedResponseBody(
                mRetrofitAPI.fetchEventTeams(eventKey, cacheHeader),
                new TeamAndEventTeamCombiner(eventKey),
                mWriter.getEventTeamAndTeamListWriter().get());
    }

    public void updateEvent(String eventKey, String cacheHeader) {
        mResponseMap.writeResponseBody(
                mRetrofitAPI.fetchEvent(eventKey, cacheHeader),
                mWriter.getEventWriter().get());
    }

    public void updateEventMatches(String eventKey, String cacheHeader) {
        mResponseMap.writeResponseBody(
                mRetrofitAPI.fetchEventMatches(eventKey, cacheHeader),
                mWriter.getMatchListWriter().get(),
                Database.TABLE_MATCHES, MatchesTable.EVENT + " = ?", new String[]{eventKey});
    }

    public void updateEventRankings(String eventKey, String cacheHeader) {
        mResponseMap.getAndWriteMappedResponseBody(
                mRetrofitAPI.fetchEventRankings(eventKey, cacheHeader),
                new JsonAndKeyCombiner(eventKey),
                mWriter.getEventRankingsWriter().get());
    }

    public void updateEventStats(String eventKey, String cacheHeader) {
        mResponseMap.getAndWriteMappedResponseBody(
                mRetrofitAPI.fetchEventStats(eventKey, cacheHeader),
                new JsonAndKeyCombiner(eventKey),
                mWriter.getEventStatsWriter().get());
    }
}
