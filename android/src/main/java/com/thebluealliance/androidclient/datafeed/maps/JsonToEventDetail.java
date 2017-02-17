package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.models.EventDetail;
import com.thebluealliance.androidclient.types.EventDetailType;

import rx.functions.Func1;

public class JsonToEventDetail implements Func1<JsonElement, EventDetail> {

    private final String mEventKey;
    private final EventDetailType mEventDetailType;

    public JsonToEventDetail(String eventKey, EventDetailType type) {
        mEventKey = eventKey;
        mEventDetailType = type;
    }

    @Override
    public EventDetail call(JsonElement data) {
        if (data == null || data.isJsonNull()) {
            return null;
        }
        EventDetail detail = new EventDetail(mEventKey, mEventDetailType);
        detail.setJsonData(data.toString());
        return detail;
    }
}
