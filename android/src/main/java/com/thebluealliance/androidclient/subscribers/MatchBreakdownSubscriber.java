package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.api.model.IMatchAlliancesContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MatchBreakdownSubscriber extends BaseAPISubscriber<Match, MatchBreakdownBinder.Model> {

    static final String SHOW_2017_KEY = "show_2017_breakdowns";
    static final String SHOW_2018_KEY = "show_2018_breakdowns";
    static final String SHOW_2019_KEY = "show_2019_breakdowns";
    static final String SHOW_2020_KEY = "show_2020_breakdowns";
    static final String SHOW_2022_KEY = "show_2022_breakdowns";
    static final String SHOW_2023_KEY = "show_2023_breakdowns";
    static final String SHOW_2024_KEY = "show_2024_breakdowns";
    static final String SHOW_2025_KEY = "show_2025_breakdowns";

    private final Gson mGson;
    private final AppConfig mConfig;

    @Inject
    public MatchBreakdownSubscriber(Gson gson, AppConfig config) {
        mGson = gson;
        mConfig = config;
    }

    @Override
    public void parseData()  {
        JsonObject scoreBreakdown;
        IMatchAlliancesContainer alliances;
        boolean shouldShowBreakdown = false;

        switch (mAPIData.getYear()) {
            case 2015:
            case 2016:
                shouldShowBreakdown = true;
                break;
            case 2017:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2017_KEY);
                TbaLogger.i("Showing 2017 breakdowns? " + shouldShowBreakdown);
                break;
            case 2018:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2018_KEY);
                TbaLogger.i("Showing 2018 breakdowns? " + shouldShowBreakdown);
                break;
            case 2019:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2019_KEY);
                TbaLogger.i("Showing 2019 breakdowns? " + shouldShowBreakdown);
                break;
            case 2020:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2020_KEY);
                TbaLogger.i("Showing 2020 breakdown? " + shouldShowBreakdown);
                break;
            case 2022:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2022_KEY);
                TbaLogger.i("Showing 2022 breakdown? " + shouldShowBreakdown);
                break;
            case 2023:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2023_KEY);
                TbaLogger.i("Showing 2023 breakdown? " + shouldShowBreakdown);
                break;
            case 2024:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2024_KEY);
                TbaLogger.i("Showing 2024 breakdown? " + shouldShowBreakdown);
                break;
            case 2025:
                shouldShowBreakdown = mConfig.getBoolean(SHOW_2025_KEY);
                TbaLogger.i("Showing 2025 breakdown? " + shouldShowBreakdown);
                break;
        }

        mDataToBind = null;
        if (shouldShowBreakdown) {
            scoreBreakdown = mGson.fromJson(mAPIData.getScoreBreakdown(), JsonObject.class);
                alliances = mAPIData.getAlliances();
                if (scoreBreakdown == null
                        || scoreBreakdown.entrySet().isEmpty()
                        || alliances == null) {
                    mDataToBind = null;
                    return;
                }

                mDataToBind = new MatchBreakdownBinder.Model(mAPIData.getType(),
                                                             mAPIData.getYear(),
                                                             mAPIData.getWinningAlliance(),
                                                             alliances,
                                                             scoreBreakdown);
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
