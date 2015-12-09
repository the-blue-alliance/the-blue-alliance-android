package com.thebluealliance.androidclient.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class ChildEvent {

    public FirebaseChildType eventType;
    public DataSnapshot snapshot;
    public String previousChildName;
    public FirebaseError error;

    public ChildEvent(FirebaseChildType eventType, DataSnapshot snapshot, String previousChildName, FirebaseError firebaseError) {
        this.eventType = eventType;
        this.snapshot = snapshot;
        this.previousChildName = previousChildName;
        this.error = firebaseError;
    }
}
