package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.EventAlliance;

import java.util.List;

import rx.functions.Func1;

public class AllianceEventKeyAdder implements Func1<List<EventAlliance>, List<EventAlliance>> {

    private final String mEventKey;

    public AllianceEventKeyAdder(String eventKey) {
        mEventKey = eventKey;
    }

    @Override
    public List<EventAlliance> call(List<EventAlliance> eventAlliances) {
        if (eventAlliances == null) {
            return null;
        }
        for (int i = 0; i < eventAlliances.size(); i++) {
            eventAlliances.get(i).setEventKey(mEventKey);
        }
        return eventAlliances;
    }
}
