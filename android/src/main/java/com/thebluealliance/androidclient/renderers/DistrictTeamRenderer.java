package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.Nullable;
import com.thebluealliance.androidclient.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DistrictTeamRenderer implements ModelRenderer<DistrictTeam, Void> {

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
    public DistrictTeamListElement renderFromModel(DistrictTeam districtTeam, Void aVoid) {
        try {
            return new DistrictTeamListElement(
              districtTeam.getTeamKey(),
              districtTeam.getDistrictKey(),
              districtTeam.getRank(),
              districtTeam.getTotalPoints());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to render districtTeam. Missing fields");
            e.printStackTrace();
            return null;
        }
    }
}
