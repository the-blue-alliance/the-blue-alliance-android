package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.fragments.BriteDatafeedFragment;
import com.thebluealliance.androidclient.models.EventInfo;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Single;

public class EventInfoFragment
        extends BriteDatafeedFragment<EventInfo, EventInfoBinder.Model, EventInfoSubscriber, EventInfoBinder> {

    private static final String KEY = "eventKey";

    private String mEventKey;

    public static EventInfoFragment newInstance(String eventKey) {
        EventInfoFragment f = new EventInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_info, null);
        mBinder.setInflater(inflater);
        mBinder.setRootView(view);
        mBinder.setNoDataView((NoDataView) view.findViewById(R.id.no_data));

        // Only show space for the FAB if the FAB is visible
        boolean myTbaEnabled = AccountHelper.isMyTBAEnabled(getActivity());
        view.findViewById(R.id.fab_padding).setVisibility(myTbaEnabled ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<EventInfo> getObservable() {
        return mDatafeed.getEventInfo(mEventKey);
    }

    @Override
    protected List<Single<?>> beginDataUpdate(String tbaCacheHeader) {
        List<Single<?>> observables = new ArrayList<>();

        observables.add(mDatabaseUpdater.updateEvent(mEventKey, tbaCacheHeader));
        // For showing next/last match
        observables.add(mDatabaseUpdater.updateEventMatches(mEventKey, tbaCacheHeader));
        // For showing top teams by ranking
        observables.add(mDatabaseUpdater.updateEventRankings(mEventKey, tbaCacheHeader));
        // For showing top teams by OPR
        observables.add(mDatabaseUpdater.updateEventStats(mEventKey, tbaCacheHeader));

        return observables;
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventInfo_%1$s", mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_event_info);
    }
}
