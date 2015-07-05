package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.listeners.EventClickListener;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;

import java.util.List;

import rx.Observable;

public class DistrictEventsFragment extends ListviewFragment<List<Event>, EventListSubscriber> {

    public static final String KEY = "districtKey";

    private String mKey, mShort;
    private int mYear;

    public static DistrictEventsFragment newInstance(String key) {
        DistrictEventsFragment f = new DistrictEventsFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKey = getArguments().getString(KEY);
        if (!DistrictHelper.validateDistrictKey(mKey)) {
            throw new IllegalArgumentException("Invalid district key + " + mKey);
        }
        mShort = mKey.substring(4);
        mYear = Integer.parseInt(mKey.substring(0, 4));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(new EventClickListener(getActivity(), null));
        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable() {
        return mDatafeed.fetchDistrictEvents(mShort, mYear);
    }
}
