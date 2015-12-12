package com.thebluealliance.androidclient.firebase;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Wraps a Firebase {@link ChildEventListener} in an {@link Observable} buffer
 * that can pause and resume event delivery as required.
 * <p>
 * A problem I found with the default means of implementing ChildEventListener is that events could
 * potentially be delivered when a hosting {@link android.app.Fragment} was in a state without a
 * view, for instance, if the fragment was being retained across a configuration change. This meant
 * that we couldn't reflect any new events in our list of objects. This solves that by wrapping the
 * target listener in a buffer of sorts. Ordinarilly, events are passed straight through to the
 * target listener. However, when you call {@link #pauseDelivery()}, any
 * future events are buffered in an internal list. When you call {@link #resumeDelivery()},
 * the events received during the paused state are delivered to the listener in the order that they
 * were received (this happens synchronously).
 * <p>
 *
 * @author Nathan
 * @author Phil
 */
public class ResumeableRxFirebase implements ChildEventListener {

    /**
     * Events we've buffered so that we can deliver later
     */
    private List<ChildEvent> mEvents = new ArrayList<>();

    /**
     * {@link Observable} subject we can post to
     */
    private Subject<ChildEvent, ChildEvent> mSubject;

    private boolean isDeliveryPaused;

    @Inject
    public ResumeableRxFirebase() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
        isDeliveryPaused = false;
    }

    /**
     * Returns an {@link Observable} that acts like an "event bus" for {@link ChildEvent}s.
     * You should use {@link Observable#buffer(int)} on the result, because after a pause and
     * resume in this class, the buffered events will come through very quickly (probably too
     * quickly for your subscriber)
     *
     * @return an {@link Observable} that you can subscribe to
     */
    public Observable<ChildEvent> getObservable() {
        return mSubject;
    }

    /**
     * This method stop emitting items to the observable, but buffers them internally
     */
    public void pauseDelivery() {
        this.isDeliveryPaused = true;
    }

    public void resumeDelivery() {
        this.isDeliveryPaused = false;

        for (int i = 0; i < mEvents.size(); i++) {
            mSubject.onNext(mEvents.get(i));
        }
    }

    /**
     * Completes the observable
     */
    public void finishDelivery() {
        mSubject.onCompleted();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChildEvent event = new ChildEvent(FirebaseChildType.CHILD_ADDED, dataSnapshot, s, null);
        if (isDeliveryPaused) {
            mEvents.add(event);
        } else {
            mSubject.onNext(event);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        ChildEvent event = new ChildEvent(FirebaseChildType.CHILD_CHANGED, dataSnapshot, s, null);
        if (isDeliveryPaused) {
            mEvents.add(event);
        } else {
            mSubject.onNext(event);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        ChildEvent event = new ChildEvent(FirebaseChildType.CHILD_REMOVED, dataSnapshot, null, null);
        if (isDeliveryPaused) {
            mEvents.add(event);
        } else {
            mSubject.onNext(event);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        ChildEvent event = new ChildEvent(FirebaseChildType.CHILD_MOVED, dataSnapshot, s, null);
        if (isDeliveryPaused) {
            mEvents.add(event);
        } else {
            mSubject.onNext(event);
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        ChildEvent event = new ChildEvent(FirebaseChildType.CANCELLED, null, null, firebaseError);
        if (isDeliveryPaused) {
            mEvents.add(event);
        } else {
            mSubject.onNext(event);
        }
    }

}
