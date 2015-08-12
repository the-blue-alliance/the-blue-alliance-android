package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;
import java.util.List;

public class TeamStatsSubscriber extends BaseAPISubscriber<JsonObject, List<ListItem>> {

    private Context mContext;
    private String mTeamKey;

    public TeamStatsSubscriber(Context context) {
        super();
        mContext = context;
        mDataToBind = new ArrayList<>();
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData.has("opr")) {
            mDataToBind.add(new LabelValueListItem(
              mContext.getString(R.string.opr_no_colon),
              Stat.displayFormat.format(mAPIData.get("opr").getAsDouble())));
        }
        if (mAPIData.has("dpr")) {
            mDataToBind.add(new LabelValueListItem(
              mContext.getString(R.string.dpr_no_colon),
              Stat.displayFormat.format(mAPIData.get("dpr").getAsDouble())));
        }
        if (mAPIData.has("ccwm")) {
            mDataToBind.add(new LabelValueListItem(
              mContext.getString(R.string.ccwm_no_colon),
              Stat.displayFormat.format(mAPIData.get("ccwm").getAsDouble())));
        }
    }
}
