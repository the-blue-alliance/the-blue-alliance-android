package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.models.DistrictRanking;

import java.util.List;

import rx.functions.Func1;

public class AddDistrictTeamKey implements Func1<List<DistrictRanking>, List<DistrictRanking>> {

    private String districtKey;

    public AddDistrictTeamKey(String districtKey) {
        this.districtKey = districtKey;
    }

    @Override
    public List<DistrictRanking> call(List<DistrictRanking> districtTeams) {
        if (districtTeams == null) return null;
        for (int i = 0; i < districtTeams.size(); i++) {
            DistrictRanking dt = districtTeams.get(i);
            String dtKey;
            dtKey = DistrictTeamHelper.generateKey(dt.getTeamKey(), districtKey);
            dt.setKey(dtKey);
            dt.setDistrictKey(districtKey);
        }
        return districtTeams;
    }
}
