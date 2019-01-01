package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.types.ModelType;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DistrictTeamRenderer implements ModelRenderer<DistrictRanking, Void> {

    @Inject
    public DistrictTeamRenderer() {

    }

    @Nullable
    @Override
    public ListElement renderFromKey(String key, ModelType type, Void args) {
        return null;
    }

    @Nullable
    @Override
    public DistrictTeamListElement renderFromModel(DistrictRanking districtTeam, Void aVoid) {
        return new DistrictTeamListElement(
                districtTeam.getTeamKey(),
                districtTeam.getDistrictKey(),
                districtTeam.getRank(),
                districtTeam.getPointTotal());
    }
}
