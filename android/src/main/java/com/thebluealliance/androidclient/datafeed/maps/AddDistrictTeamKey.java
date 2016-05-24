package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import java.util.List;

import rx.functions.Func1;

public class AddDistrictTeamKey implements Func1<List<DistrictTeam>, List<DistrictTeam>> {

    private String mDistrictShort;
    private int mYear;

    public AddDistrictTeamKey(String districtShort, int year) {
        mDistrictShort = districtShort;
        mYear = year;
    }

    @Override
    public List<DistrictTeam> call(List<DistrictTeam> districtTeams) {
        for (int i = 0; i < districtTeams.size(); i++) {
            DistrictTeam dt = districtTeams.get(i);
            String districtKey = DistrictHelper.generateKey(mDistrictShort, mYear);
            String dtKey;
            try {
                dtKey = DistrictTeamHelper.generateKey(dt.getTeamKey(), districtKey);
                dt.setKey(dtKey);
                dt.setDistrictEnum(DistrictHelper.districtTypeFromKey(districtKey).ordinal());
                dt.setYear(mYear);
                dt.setDistrictKey(districtKey);
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
            }
        }
        return districtTeams;
    }
}
