package com.thebluealliance.androidclient.subscribers;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APISubscriber;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.models.BasicModel;

import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Base class for a concrete API Subscriber.
 * This class takes an input of data directly from Retrofit (using {@link rx.Subscriber} and
 * provides a callback to
 *
 * @param <APIType>  Datatype to be returned from the API (one from {@link APIv2}
 * @param <BindType> Datatype to be returned for binding to views
 */
public abstract class BaseAPISubscriber<APIType, BindType>
        implements Observer<APIType>, APISubscriber<BindType> {

    DataConsumer<BindType> mConsumer;
    APIType mAPIData;
    BindType mDataToBind;
    RefreshController mRefreshController; //TODO hook up to DI
    String mRefreshTag;
    boolean shouldBindImmediately;
    boolean shouldBindOnce;

    public BaseAPISubscriber() {
        shouldBindImmediately = true;
    }

    public void setShouldBindImmediately(boolean shouldBind) {
        shouldBindImmediately = shouldBind;
    }

    /**
     * If set, bind immediately once until onComplete is called
     */
    public void setShouldBindOnce(boolean shouldBind) {
        shouldBindOnce = shouldBind;
    }

    public void setConsumer(DataConsumer<BindType> consumer) {
        mConsumer = consumer;
    }

    public void setRefreshController(RefreshController refreshController) {
        mRefreshController = refreshController;
    }

    public void setRefreshTag(String refreshTag) {
        mRefreshTag = refreshTag;
    }

    /**
     * Called when a refresh begins
     */
    public void onRefreshStart() {
        mRefreshController.notifyRefreshingStateChanged(mRefreshTag, true);
    }

    @Override
    @WorkerThread
    public void onNext(APIType data) {
        setApiData(data);
        postToEventBus(EventBus.getDefault());
        try {
            parseData();
            if (shouldBindImmediately || shouldBindOnce) {
                bindData();
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted() {
        shouldBindOnce = false;
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            mRefreshController.notifyRefreshingStateChanged(mRefreshTag, false);
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
        mRefreshController.notifyRefreshingStateChanged(mRefreshTag, false);
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mConsumer != null) {
                mConsumer.onError(throwable);
            }
        });
    }

    @Override
    public @Nullable BindType getBoundData() {
        return mDataToBind;
    }

    @VisibleForTesting
    public void setApiData(APIType data) {
        mAPIData = data;
    }

    public @Nullable APIType getApiData() {
        return mAPIData;
    }

    public void bindData() {
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

    /**
     * Post {@link #mAPIData} to the given {@link EventBus}
     */
    protected void postToEventBus(EventBus eventBus) {
        if (shouldPostToEventBus()) {
            eventBus.post(mAPIData);
        }
    }

    /**
     * Do we post {@link #mAPIData} to the EventBus?
     * Override and return true to do so
     */
    protected boolean shouldPostToEventBus() {
        return false;
    }
}
