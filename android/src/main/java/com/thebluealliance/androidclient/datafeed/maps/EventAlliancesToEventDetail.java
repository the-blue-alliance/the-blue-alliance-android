package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.EventDetail;

import java.util.List;

import rx.functions.Func1;

public class EventAlliancesToEventDetail implements Func1<List<EventAlliance>, EventDetail> {

    private final String mEventKey;
    private final Gson mGson;

    public EventAlliancesToEventDetail(String eventKey, Gson gson) {
        mEventKey = eventKey;
        mGson = gson;
    }

    @Override
    public EventDetail call(List<EventAlliance> eventAlliances) {
        return EventAlliance.toEventDetail(eventAlliances, mEventKey, mGson);
    }
}
