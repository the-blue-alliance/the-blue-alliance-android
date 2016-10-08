package com.thebluealliance.androidclient.fragments.framework;

import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

/**
 * A very simple {@link BaseAPISubscriber} to test framework bindings
 */
public class SimpleSubscriber extends BaseAPISubscriber<String, String> {

    @Override
    public void parseData()  {
        mDataToBind = mAPIData + ":" + mAPIData;
    }
}
