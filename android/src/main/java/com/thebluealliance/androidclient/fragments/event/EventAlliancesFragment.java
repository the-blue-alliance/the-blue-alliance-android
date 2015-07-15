package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;

import rx.Observable;

public class EventAlliancesFragment extends ListviewFragment<Event, AllianceListSubscriber> {
    private static final String KEY = "event_key";
    public static final String DATAFEED_TAG_FORMAT = "event_alliances_%1$s";

    private String mDatafeedTag;
    private String mEventKey;

    public static EventAlliancesFragment newInstance(String eventKey) {
        EventAlliancesFragment f = new EventAlliancesFragment();
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
            mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mEventKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Event> getObservable() {
        return mDatafeed.fetchEvent(mEventKey);
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
