package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.models.EventDetail;
import com.thebluealliance.androidclient.models.RankingResponseObject;

import rx.functions.Func1;

public class AddEventKeyToRankings implements Func1<RankingResponseObject, EventDetail> {

    private final String mEventKey;
    private final Gson mGson;

    public AddEventKeyToRankings(String eventKey, Gson gson) {
        mEventKey = eventKey;
        mGson = gson;
    }

    @Override
    public EventDetail call(RankingResponseObject rankingResponseObject) {
        rankingResponseObject.setEventKey(mEventKey);
        return rankingResponseObject.toEventDetail(mGson);
    }
}
