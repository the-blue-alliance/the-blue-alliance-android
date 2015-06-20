package com.thebluealliance.androidclient.subscribers;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.models.Team;

public class TeamInfoSubscriber extends BaseAPISubscriber<Team, Team>{

    public TeamInfoSubscriber(DataConsumer<Team> team) {
        super(team, true);
    }

    @Override
    public void parseData() {
        // No parsing needed here
        mDataToBind = mAPIData;
    }
}
