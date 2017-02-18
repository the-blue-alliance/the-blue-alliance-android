package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DistrictPointBreakdownRenderer implements ModelRenderer<DistrictPointBreakdown, Void> {

    @Inject
    public DistrictPointBreakdownRenderer() {

    }

    @Override
    public @Nullable ListElement renderFromKey(String key, ModelType type, Void args) {
        /* Not used */
        return null;
    }

    @Override
    public @Nullable DistrictTeamListElement renderFromModel(
      DistrictPointBreakdown breakdown,
      Void aVoid) {
        return new DistrictTeamListElement(
          breakdown.getTeamKey(),
          breakdown.getDistrictKey(),
          breakdown.getTeamName(),
          breakdown.getRank(),
          breakdown.getTotal());
    }
}
