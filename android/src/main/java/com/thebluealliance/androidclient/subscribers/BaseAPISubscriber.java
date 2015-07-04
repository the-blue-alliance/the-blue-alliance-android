package com.thebluealliance.androidclient.subscribers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APISubscriber;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.models.BasicModel;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Base class for a concrete API Subscriber.
 * This class takes an input of data directly from Retrofit (using {@link rx.Subscriber} and
 * provides a callback to
 * @param <T> Datatype to be returned from the API (one from {@link APIv2}
 * @param <V> Datatype to be returned for binding to views
 */
public abstract class BaseAPISubscriber<T, V> extends Subscriber<T> implements APISubscriber<V>{

    DataConsumer<V> mConsumer;
    T mAPIData;
    V mDataToBind;

    public void setConsumer(DataConsumer<V> consumer) {
        mConsumer = consumer;
    }

    // also allow data posting via event bus
    public void onEventAsync(T data) {
        onNext(data);
    }

    @Override
    public void onNext(T data) {
        mAPIData = data;
        try {
            parseData();
            bindData();
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted() {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mConsumer != null) {
                try {
                    mConsumer.onComplete();
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, "UNABLE TO COMPLETE RENDER");
                    e.printStackTrace();
                    mConsumer.onError(e);
                }
            }
        });
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
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mConsumer != null) {
                try {
                    mConsumer.updateData(mDataToBind);
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, "UNABLE TO RENDER");
                    e.printStackTrace();
                    mConsumer.onError(e);
                }
            }
        });
    }
}
