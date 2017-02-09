package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.models.Match;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MatchBreakdownSubscriber extends BaseAPISubscriber<Match, MatchBreakdownBinder.Model> {

    @Override
    public void parseData()  {
        JsonObject scoreBreakdown;
        JsonObject alliances;

        switch (mAPIData.getYear()) {
            case 2015:
            case 2016:
                scoreBreakdown = mAPIData.getScoreBreakdownJson();
                alliances = mAPIData.getAlliancesJson();
                if (scoreBreakdown.entrySet().isEmpty() || alliances.entrySet().isEmpty()) {
                    mDataToBind = null;
                    break;
                }

                mDataToBind = new MatchBreakdownBinder.Model(mAPIData.getType(),
                                                            mAPIData.getYear(),
                                                            alliances,
                                                            scoreBreakdown);
                break;
            default:
                mDataToBind = null;
                break;
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
