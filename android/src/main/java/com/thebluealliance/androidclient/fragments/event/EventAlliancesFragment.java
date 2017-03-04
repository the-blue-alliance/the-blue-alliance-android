package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rx.Observable;

public class EventAlliancesFragment extends ListViewFragment<List<EventAlliance>, AllianceListSubscriber> {
    private static final String KEY = "event_key";

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
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
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
    protected boolean shouldRegisterSubscriberToEventBus() {
        return true;
    }

    @Override
    protected Observable<List<EventAlliance>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventAlliances(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventAlliances_%1$s", mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_handshake_black_48dp, R.string.no_alliance_data);
    }
}
