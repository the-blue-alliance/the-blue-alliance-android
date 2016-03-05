package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

public class MatchBreakdownSubscriber extends BaseAPISubscriber<Match, JsonObject> {

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        if (mAPIData.getYear() == 2016) {
            // Currently only support 2016 matches
            mDataToBind = mAPIData.getBreakdown();
        } else {
            mDataToBind = null;
        }
    }
}
