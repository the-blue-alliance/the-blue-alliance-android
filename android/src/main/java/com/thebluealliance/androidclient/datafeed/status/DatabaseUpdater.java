package com.thebluealliance.androidclient.datafeed.status;

import com.thebluealliance.androidclient.database.DatabaseWriter;
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
            @Named("tba_api")APIv2 retrofitAPI,
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
}
