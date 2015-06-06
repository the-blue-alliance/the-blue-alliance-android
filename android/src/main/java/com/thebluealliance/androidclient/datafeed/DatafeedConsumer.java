package com.thebluealliance.androidclient.datafeed;

public interface DatafeedConsumer<T> {

    /**
     * Make calls to {@link CacheableDatafeed} to request data
     * Should subscribe to the Observables returned
     */
    void requestData();
}
