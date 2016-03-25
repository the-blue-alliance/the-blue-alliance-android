package com.thebluealliance.androidclient.subscribers;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * A class to take data from an observable and push it to the EventBus
 */
public class EventBusSubscriber extends Subscriber<Object> {
    private EventBus mEventBus;

    @Inject
    public EventBusSubscriber(EventBus eventBus) {
        mEventBus = eventBus;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onNext(Object o) {
        mEventBus.post(o);
    }
}
