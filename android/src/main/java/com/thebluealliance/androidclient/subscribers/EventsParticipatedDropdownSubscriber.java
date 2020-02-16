package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.comparators.EventSortByDateComparator;
import com.thebluealliance.androidclient.interfaces.EventsParticipatedUpdate;
import com.thebluealliance.androidclient.models.Event;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class EventsParticipatedDropdownSubscriber extends Subscriber<List<Event>> {

    private final EventsParticipatedUpdate mCallback;

    @Inject
    public EventsParticipatedDropdownSubscriber(EventsParticipatedUpdate callback) {
        mCallback = callback;
    }
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        TbaLogger.e("Error fetching team events", e);
    }

    @Override
    public void onNext(List<Event> apiEvents) {
        Collections.sort(apiEvents, new EventSortByDateComparator());

        AndroidSchedulers.mainThread().createWorker()
                .schedule(() -> mCallback.updateEventsParticipated(apiEvents));
    }
}
