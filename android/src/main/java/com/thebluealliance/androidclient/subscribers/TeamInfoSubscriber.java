package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.models.Team;

public class TeamInfoSubscriber extends BaseAPISubscriber<Team, Team>{

    public TeamInfoSubscriber() {
        super(true);
    }

    @Override
    public void parseData() {
        // No parsing needed here
        mDataToBind = mAPIData;
    }
}
