package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.APISubscriber;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.DataConsumer;

import rx.Subscriber;

/**
 * Base class for a concrete API Subscriber.
 * This class takes an input of data directly from Retrofit (using {@link rx.Subscriber} and
 * provides a callback to
 * @param <T> Datatype to be returned from the API (one from {@link APIv2}
 * @param <V> Datatype to be returned for binding to views
 */
abstract class BaseAPISubscriber<T, V> extends Subscriber<T> implements APISubscriber<V>{

    DataConsumer<V> mConsumer;
    T mAPIData;
    V mDataToBind;

    public BaseAPISubscriber(DataConsumer<V> consumer){
        mConsumer = consumer;
    }

    @Override
    public void onCompleted() {
        if (mConsumer != null) {
            mConsumer.updateData(mDataToBind);
        }
    }

    @Override public void onError(Throwable throwable) {
        if (mConsumer != null) {
            mConsumer.onError(throwable);
        }
    }
}
