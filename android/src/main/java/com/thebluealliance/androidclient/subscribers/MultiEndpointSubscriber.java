package com.thebluealliance.androidclient.subscribers;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Subscriber;

public class MultiEndpointSubscriber extends Subscriber<Object> {

    private EventBus mEventBus;

    @Inject
    public MultiEndpointSubscriber(EventBus eventBus) {
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
