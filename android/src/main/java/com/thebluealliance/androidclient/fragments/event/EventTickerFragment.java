package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;

import com.thebluealliance.androidclient.fragments.FirebaseTickerFragment;

public class EventTickerFragment extends FirebaseTickerFragment {

    private static final String KEY = "event_key";

    private String mEventKey;

    public static EventTickerFragment newInstance(String eventKey) {
        EventTickerFragment f = new EventTickerFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }

        if (mEventKey == null || mEventKey.isEmpty()) {
            throw new IllegalArgumentException("EventTickerFragment must be created with an event key");
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected String getFirebaseUrlSuffix() {
        return "events/" + mEventKey;
    }
}
