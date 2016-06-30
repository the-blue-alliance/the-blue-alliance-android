package com.thebluealliance.androidclient.subscribers;

import com.google.android.gms.analytics.Tracker;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APISubscriber;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.BasicModel;

import org.greenrobot.eventbus.EventBus;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Base class for a concrete API Subscriber.
 * This class takes an input of data directly from Retrofit (using {@link rx.Subscriber} and
 * provides a callback to
 *
 * Subclasses should implement {@link #parseData()} to take the data set in {@link #mAPIData} and
 * produce something bindable and store it in {@link #mDataToBind}. Subclasses should also
 * initialize {@link #mDataToBind} in their constructors, as it's possible that the variable
 * is accessed before {@link #parseData()} is called
 *
 * @param <APIType>  Datatype to be returned from the API (one from
 * {@link com.thebluealliance.androidclient.datafeed.retrofit.APIv2}
 * @param <BindType> Datatype to be returned for binding to views
 */
public abstract class BaseAPISubscriber<APIType, BindType>
        implements Observer<APIType>, APISubscriber<BindType> {

    DataConsumer<BindType> mConsumer;
    protected APIType mAPIData;
    protected BindType mDataToBind;
    RefreshController mRefreshController; //TODO hook up to DI
    String mRefreshTag;
    Tracker mAnalyticsTracker;
    boolean hasBinderBoundViews;
    boolean shouldBindImmediately;
    boolean shouldBindOnce;

    private long mRefreshStart;

    public BaseAPISubscriber() {
        shouldBindImmediately = true;
        hasBinderBoundViews = false;
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

    public void setTracker(Tracker tracker) {
        mAnalyticsTracker = tracker;
    }

    /**
     * Called when a refresh begins
     */
    public void onRefreshStart(@RefreshController.RefreshType int refreshType) {
        mRefreshController.notifyRefreshingStateChanged(mRefreshTag, true);
        mRefreshStart = System.nanoTime();
        if (refreshType == RefreshController.REQUESTED_BY_USER) {
            sendRefreshUpdate();
        }
    }

    @UiThread
    public void onParentStop() {
        hasBinderBoundViews = false;
    }

    @Override
    @WorkerThread
    public void onNext(APIType data) {
        setApiData(data);
        postToEventBus(EventBus.getDefault());
        try {
            if (isDataValid()) {
                parseData();
            }
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
                    bindViewsIfNeeded();
                    mConsumer.onComplete();
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, "UNABLE TO COMPLETE RENDER");
                    e.printStackTrace();
                    mConsumer.onError(e);
                }
            }
        });

        long totalRefreshTime = System.nanoTime() - mRefreshStart;
        sendTimingUpdate(totalRefreshTime / 1000); // Convert to ms
    }

    @Override
    public void onError(Throwable throwable) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            mRefreshController.notifyRefreshingStateChanged(mRefreshTag, false);
            if (mConsumer != null) {
                bindViewsIfNeeded();
                mConsumer.onError(throwable);
            }
        });
        sendExceptionUpdate(throwable);
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
                    bindViewsIfNeeded();
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
     *
     */
    public void bindViewsIfNeeded() {
        if (!hasBinderBoundViews && mConsumer != null) {
            mConsumer.bindViews();
            hasBinderBoundViews = true;
        }
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

    /**
     * Subclasses can override this method to determine if {@link #mAPIData} is valid.
     * Default to simply checking if null
     */
    @VisibleForTesting
    public boolean isDataValid() {
        return mAPIData != null;
    }

    private void sendTimingUpdate(long timeSpent) {
        if (mAnalyticsTracker != null) {
            mAnalyticsTracker.send(AnalyticsHelper.getTimingHit(timeSpent, this.getClass().getSimpleName(), mRefreshTag));
        }
    }

    private void sendRefreshUpdate() {
        if (mAnalyticsTracker != null) {
            mAnalyticsTracker.send(AnalyticsHelper.getRefreshHit(mRefreshTag));
        }
    }

    private void sendExceptionUpdate(Throwable throwable) {
        if (mAnalyticsTracker != null) {
            mAnalyticsTracker.send(AnalyticsHelper.getErrorHit(throwable));
        }
    }
}
