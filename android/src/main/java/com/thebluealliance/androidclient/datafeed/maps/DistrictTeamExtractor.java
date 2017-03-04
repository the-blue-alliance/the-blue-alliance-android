package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.DistrictRanking;

import java.util.List;

import rx.functions.Func1;

public class DistrictTeamExtractor implements Func1<List<DistrictRanking>, DistrictRanking> {

    private String mTeamKey;

    public DistrictTeamExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public DistrictRanking call(List<DistrictRanking> districtTeams) {
        for (int i = 0; i < districtTeams.size(); i++) {
            DistrictRanking districtTeam = districtTeams.get(i);
            if (districtTeam.getTeamKey().equals(mTeamKey)) {
                return districtTeam;
            }
        }
        return null;
    }
}
