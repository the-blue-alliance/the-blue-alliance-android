package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeamStatsSubscriber extends BaseAPISubscriber<JsonElement, List<Object>> {

    private Resources mResources;

    public TeamStatsSubscriber(Resources resources) {
        super();
        mResources = resources;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        JsonObject statsData = mAPIData.getAsJsonObject();
        if (statsData.has("opr")) {
            mDataToBind.add(new LabelValueViewModel(
              mResources.getString(R.string.opr_no_colon),
              ThreadSafeFormatters.formatDoubleTwoPlaces(statsData.get("opr").getAsDouble())));
        }
        if (statsData.has("dpr")) {
            mDataToBind.add(new LabelValueViewModel(
              mResources.getString(R.string.dpr_no_colon),
              ThreadSafeFormatters.formatDoubleTwoPlaces(statsData.get("dpr").getAsDouble())));
        }
        if (statsData.has("ccwm")) {
            mDataToBind.add(new LabelValueViewModel(
              mResources.getString(R.string.ccwm_no_colon),
              ThreadSafeFormatters.formatDoubleTwoPlaces(statsData.get("ccwm").getAsDouble())));
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && mAPIData.isJsonObject();
    }
}
