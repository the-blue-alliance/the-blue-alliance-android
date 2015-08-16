package com.thebluealliance.androidclient.subscribers;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
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
