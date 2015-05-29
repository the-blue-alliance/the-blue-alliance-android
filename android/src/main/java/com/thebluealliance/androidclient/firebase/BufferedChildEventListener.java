package com.thebluealliance.androidclient.firebase;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a Firebase {@link com.firebase.client.ChildEventListener} in a buffer that can pause and
 * resume event delivery as required.
 * <p>
 * A problem I found with the default means of implementing ChildEventListener is that events could
 * potentially be delivered when a hosting {@link android.app.Fragment} was in a state without a
 * view, for instance, if the fragment was being retained across a configuration change. This meant
 * that we couldn't reflect any new events in our list of objects. This solves that by wrapping the
 * target listener in a buffer of sorts. Ordinarilly, events are passed straight through to the
 * target listener. However, when you call {@link BufferedChildEventListener#pauseDelivery()}, any
 * future events are buffered in an internal list. When you call {@link
 * BufferedChildEventListener#resumeDelivery()}, the events received during the paused state are
 * delivered to the listener in the order that they were received (this happens synchronously).
 * <p>
 * Created by Nathan on 4/17/2015.
 */
public class BufferedChildEventListener implements ChildEventListener {

    private List<ChildEvent> events = new ArrayList<>();

    private boolean isDeliveryPaused = false;

    private ChildEventListener listener;

    public BufferedChildEventListener(ChildEventListener listener) {
        this.listener = listener;
    }

    public void pauseDelivery() {
        this.isDeliveryPaused = true;
    }

    public void resumeDelivery() {
        this.isDeliveryPaused = false;

        if (listener == null) {
            return;
        }

        for (ChildEvent event : events) {
            switch (event.eventType) {
                case CHILD_ADDED:
                    listener.onChildAdded(event.snapshot, event.previousChildName);
                    break;
                case CHILD_CHANGED:
                    listener.onChildChanged(event.snapshot, event.previousChildName);
                    break;
                case CHILD_REMOVED:
                    listener.onChildRemoved(event.snapshot);
                    break;
                case CHILD_MOVED:
                    listener.onChildMoved(event.snapshot, event.previousChildName);
                    break;
                case CANCELLED:
                    listener.onCancelled(event.error);
                    break;
            }
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (isDeliveryPaused) {
            events.add(new ChildEvent(ChildEvent.Type.CHILD_ADDED, dataSnapshot, s, null));
        } else {
            if (listener != null) {
                listener.onChildAdded(dataSnapshot, s);
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (isDeliveryPaused) {
            events.add(new ChildEvent(ChildEvent.Type.CHILD_CHANGED, dataSnapshot, s, null));
        } else {
            if (listener != null) {
                listener.onChildChanged(dataSnapshot, s);
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (isDeliveryPaused) {
            events.add(new ChildEvent(ChildEvent.Type.CHILD_REMOVED, dataSnapshot, null, null));
        } else {
            if (listener != null) {
                listener.onChildRemoved(dataSnapshot);
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        if (isDeliveryPaused) {
            events.add(new ChildEvent(ChildEvent.Type.CHILD_MOVED, dataSnapshot, s, null));
        } else {
            if (listener != null) {
                listener.onChildMoved(dataSnapshot, s);
            }
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        if (isDeliveryPaused) {
            events.add(new ChildEvent(ChildEvent.Type.CANCELLED, null, null, firebaseError));
        } else {
            if (listener != null) {
                listener.onCancelled(firebaseError);
            }
        }
    }

    private static class ChildEvent {
        private enum Type {
            CHILD_ADDED,
            CHILD_CHANGED,
            CHILD_REMOVED,
            CHILD_MOVED,
            CANCELLED
        }

        public Type eventType;
        public DataSnapshot snapshot;
        public String previousChildName;
        public FirebaseError error;

        public ChildEvent(Type eventType, DataSnapshot snapshot, String previousChildName, FirebaseError firebaseError) {
            this.eventType = eventType;
            this.snapshot = snapshot;
            this.previousChildName = previousChildName;
            this.error = firebaseError;
        }
    }
}
