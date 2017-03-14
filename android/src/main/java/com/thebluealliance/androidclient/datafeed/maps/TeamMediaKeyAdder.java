package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.Media;

import java.util.List;

import rx.functions.Func1;

public class TeamMediaKeyAdder implements Func1<List<Media>,  List<Media>> {

    private final String mTeamKey;
    private final int mYear;

    public TeamMediaKeyAdder(String teamKey, int year) {
        mTeamKey = teamKey;
        mYear = year;
    }

    @Override
    public List<Media> call(List<Media> medias) {
        if (medias == null) return null;
        for (Media media : medias) {
            media.setYear(mYear);
            media.setTeamKey(mTeamKey);
        }
        return medias;
    }
}
