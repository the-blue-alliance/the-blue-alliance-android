package com.thebluealliance.androidclient.datafeed.framework;

import com.google.common.base.Preconditions;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

import javax.annotation.Nullable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class SubscriberTestController<API, VIEW> {

    private BaseAPISubscriber<API, VIEW> mSubscriber;
    private API mApiData;
    private DataConsumer<VIEW> mConsumer;
    private boolean hasParsed;
    private boolean hasBound;
    private boolean isComplete;

    public SubscriberTestController() {
        hasParsed = false;
        hasBound = false;
        isComplete = false;
    }

    public SubscriberTestController(BaseAPISubscriber<API, VIEW> subscriber) {
        this();
        forSubscriber(subscriber);
    }

    public SubscriberTestController<API, VIEW> forSubscriber(
      BaseAPISubscriber<API, VIEW> subscriber) {
        mSubscriber = spy(subscriber);
        mSubscriber.setRefreshController(mock(RefreshController.class));
        return this;
    }

    public SubscriberTestController<API, VIEW> withApiData(@Nullable API data) {
        checkPreconditions();
        mSubscriber.setApiData(data);
        return this;
    }

    public SubscriberTestController<API, VIEW> withConsumer(DataConsumer<VIEW> consumer) {
        checkPreconditions();
        mSubscriber.setConsumer(consumer);
        return this;
    }

    public SubscriberTestController<API, VIEW> parse()  {
        checkPreconditions();
        if (mSubscriber.isDataValid()) {
            mSubscriber.parseData();
        }
        hasParsed = true;
        return this;
    }

    public SubscriberTestController<API, VIEW> bind() {
        checkPreconditions();
        Preconditions.checkState(hasParsed, "You must parse data before you can bind");
        mSubscriber.bindData();
        hasBound = true;
        return this;
    }

    public SubscriberTestController<API, VIEW> complete() {
        checkPreconditions();
        Preconditions.checkState(hasParsed, "You must parse data before you can complete");
        Preconditions.checkState(hasBound, "You must bind data before you can bind");
        mSubscriber.onCompleted();
        isComplete = true;
        return this;
    }

    public VIEW getParsedData() {
        checkPreconditions();
        Preconditions.checkState(hasParsed, "You must parse data before you can complete");
        return mSubscriber.getBoundData();
    }

    public SubscriberTestController<API, VIEW> onNext(@Nullable API data) {
        checkPreconditions();
        mSubscriber.onNext(data);
        hasParsed = true;
        hasBound = true;
        return this;
    }

    public BaseAPISubscriber<API, VIEW> getSubscriber() {
        Preconditions.checkState(mSubscriber != null, "Must set a subscriber with .forSubscriber");
        return mSubscriber;
    }

    private void checkPreconditions() {
        Preconditions.checkState(mSubscriber != null, "Must set a subscriber with .forSubscriber");
        Preconditions.checkState(!isComplete);
    }
}
