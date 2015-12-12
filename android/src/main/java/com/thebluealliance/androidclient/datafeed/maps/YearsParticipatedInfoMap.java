package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

import rx.functions.Func1;

public class YearsParticipatedInfoMap implements Func1<JsonArray, YearsParticipatedInfo> {

    private final String mTeamKey;

    public YearsParticipatedInfoMap(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public YearsParticipatedInfo call(JsonArray jsonElements) {
        return new YearsParticipatedInfo(mTeamKey, jsonElements);
    }
}
