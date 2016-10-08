package com.thebluealliance.androidclient.subscribers;

import javax.inject.Inject;

public class TeamTabSubscriber extends BaseAPISubscriber<Integer, Integer> {

    @Inject
    public TeamTabSubscriber() {
        super();
    }

    @Override
    public void parseData()  {
        mDataToBind = mAPIData;
    }
}
