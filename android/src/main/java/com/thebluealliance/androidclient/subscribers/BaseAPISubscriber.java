package com.thebluealliance.androidclient.subscribers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APISubscriber;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.models.BasicModel;

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

    // If we should have the consumer update data in onNext (e.g. render cached data)
    // or wait until all inputs have arrived
    boolean mAlwaysBind;

    public BaseAPISubscriber(DataConsumer<V> consumer, boolean alwaysBind){
        mConsumer = consumer;
        mAlwaysBind = alwaysBind;
    }

    @Override
    public void onNext(T data) {
        mAPIData = data;
        parseData();
        if (mAlwaysBind) {
            bindData();
        }
    }

    @Override
    public void onCompleted() {
        if (!mAlwaysBind) {
            bindData();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (mConsumer != null) {
            mConsumer.onError(throwable);
        }
    }

    @Override
    public @Nullable V getData() {
        return mDataToBind;
    }

    private void bindData() {
        if (mConsumer != null) {
            try {
                mConsumer.updateData(mDataToBind);
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "UNABLE TO RENDER");
                e.printStackTrace();
            }
        }
    }
}
