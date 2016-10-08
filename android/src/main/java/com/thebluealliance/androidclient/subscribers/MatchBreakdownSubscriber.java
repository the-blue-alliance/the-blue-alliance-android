package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.models.Match;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MatchBreakdownSubscriber extends BaseAPISubscriber<Match, MatchBreakdownBinder.Model> {

    @Override
    public void parseData()  {
        if (mAPIData.getYear() == 2016) {
            // Currently only support 2016 matches
            JsonObject scoreBreakdown = mAPIData.getScoreBreakdownJson();
            JsonObject alliances = mAPIData.getAlliancesJson();
            if (scoreBreakdown.entrySet().isEmpty() || alliances.entrySet().isEmpty()) {
                mDataToBind = null;
            }

            mDataToBind = new MatchBreakdownBinder.Model(alliances, scoreBreakdown);
        } else {
            mDataToBind = null;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMatchUpdated(Match match) {
        if (match == null) {
            return;
        }
        mAPIData = match;
        if (isDataValid()) {
            parseData();
            bindData();
        }
    }
}
