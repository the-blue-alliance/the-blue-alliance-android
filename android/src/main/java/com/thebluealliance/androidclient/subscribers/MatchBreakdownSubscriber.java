package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MatchBreakdownSubscriber extends BaseAPISubscriber<Match, MatchBreakdownBinder.Model> {

    private final Gson mGson;

    @Inject
    public MatchBreakdownSubscriber(Gson gson) {
        mGson = gson;
    }

    @Override
    public void parseData()  {
        JsonObject scoreBreakdown;
        IMatchAlliancesContainer alliances;

        switch (mAPIData.getYear()) {
            case 2015:
            case 2016:
            case 2017:
                scoreBreakdown = mGson.fromJson(mAPIData.getScoreBreakdown(), JsonObject.class);
                alliances = mAPIData.getAlliances();
                if (scoreBreakdown == null
                        || scoreBreakdown.entrySet().isEmpty()
                        || alliances == null) {
                    mDataToBind = null;
                    break;
                }

                mDataToBind = new MatchBreakdownBinder.Model(mAPIData.getType(),
                                                             mAPIData.getYear(),
                                                             mAPIData.getWinningAlliance(),
                                                             alliances,
                                                             scoreBreakdown);
                break;
            default:
                mDataToBind = null;
                break;
        }
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid()
               && mAPIData.getScoreBreakdown() != null
               && mAPIData.getAlliances() != null;
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
