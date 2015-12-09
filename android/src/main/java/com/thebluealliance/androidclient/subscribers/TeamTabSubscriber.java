package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.models.BasicModel;

import javax.inject.Inject;

public class TeamTabSubscriber extends BaseAPISubscriber<Integer, Integer> {

    @Inject
    public TeamTabSubscriber() {
        super();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind = mAPIData;
    }
}
