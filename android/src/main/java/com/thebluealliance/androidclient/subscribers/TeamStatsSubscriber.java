package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;
import java.util.List;

public class TeamStatsSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>> {

    private Resources mResources;

    public TeamStatsSubscriber(Resources resources) {
        super();
        mResources = resources;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        if (mAPIData == null || !mAPIData.isJsonObject()) {
            return;
        }

        mDataToBind.clear();
        JsonObject statsData = mAPIData.getAsJsonObject();
        if (statsData.has("opr")) {
            mDataToBind.add(new LabelValueListItem(
              mResources.getString(R.string.opr_no_colon),
              Stat.displayFormat.format(statsData.get("opr").getAsDouble())));
        }
        if (statsData.has("dpr")) {
            mDataToBind.add(new LabelValueListItem(
              mResources.getString(R.string.dpr_no_colon),
              Stat.displayFormat.format(statsData.get("dpr").getAsDouble())));
        }
        if (statsData.has("ccwm")) {
            mDataToBind.add(new LabelValueListItem(
              mResources.getString(R.string.ccwm_no_colon),
              Stat.displayFormat.format(statsData.get("ccwm").getAsDouble())));
        }
    }
}
