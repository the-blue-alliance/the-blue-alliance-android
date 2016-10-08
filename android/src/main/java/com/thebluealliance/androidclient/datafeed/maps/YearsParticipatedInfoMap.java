package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

import java.util.List;

import rx.functions.Func1;

public class YearsParticipatedInfoMap implements Func1<List<Integer>, YearsParticipatedInfo> {

    private final String mTeamKey;

    public YearsParticipatedInfoMap(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public YearsParticipatedInfo call(List<Integer> elements) {
        return new YearsParticipatedInfo(mTeamKey, elements);
    }
}
