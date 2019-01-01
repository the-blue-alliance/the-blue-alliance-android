package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventDetailsTable;
import com.thebluealliance.androidclient.types.EventDetailType;

import java.util.List;

import javax.annotation.Nullable;

public class EventDetail implements TbaDatabaseModel {

    private final String key;
    private final String eventKey;
    private final EventDetailType type;

    private @Nullable String jsonData;
    private @Nullable Long lastModified;

    public EventDetail(String eventKey, EventDetailType type) {
        this.eventKey = eventKey;
        this.type = type;
        this.key = buildKey(eventKey, type);
    }

    public EventDetail(String eventKey, int typeOrdinal) {
        this(eventKey, EventDetailType.values()[typeOrdinal]);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override @Nullable
    public Long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }

    @Nullable
    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(@Nullable String jsonData) {
        this.jsonData = jsonData;
    }

    public @Nullable RankingResponseObject getDataForRankings(Gson gson) {
        if (jsonData == null || jsonData.isEmpty()) return null;
        RankingResponseObject res = gson.fromJson(jsonData, RankingResponseObject.class);
        if (res != null) res.setEventKey(eventKey);
        return res;
    }

    public @Nullable List<EventAlliance> getDataForAlliances(Gson gson) {
        if (jsonData == null || jsonData.isEmpty()) return null;
        return gson.fromJson(jsonData, new TypeToken<List<EventAlliance>>(){}.getType());
    }

    public @Nullable JsonElement getDataAsJson(Gson gson) {
        if (jsonData == null || jsonData.isEmpty()) return null;
        return gson.fromJson(jsonData, JsonElement.class);
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues params = new ContentValues();
        params.put(EventDetailsTable.KEY, key);
        params.put(EventDetailsTable.EVENT_KEY, eventKey);
        params.put(EventDetailsTable.DETAIL_TYPE, type.ordinal());
        params.put(EventDetailsTable.JSON_DATA, getJsonData());
        params.put(EventDetailsTable.LAST_MODIFIED, getLastModified());
        return params;
    }

    public static String buildKey(String eventKey, EventDetailType type) {
        return eventKey + "_" + type.getKeySuffix();
    }
}
