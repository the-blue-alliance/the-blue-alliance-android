package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import java.util.List;

import rx.functions.Func1;

public class DistrictTeamExtractor implements Func1<List<DistrictTeam>, DistrictTeam> {

    private String mTeamKey;

    public DistrictTeamExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public DistrictTeam call(List<DistrictTeam> districtTeams) {
        for (int i = 0; i < districtTeams.size(); i++) {
            DistrictTeam districtTeam = districtTeams.get(i);
            try {
                if (districtTeam.getTeamKey().equals(mTeamKey)) {
                    return districtTeam;
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
