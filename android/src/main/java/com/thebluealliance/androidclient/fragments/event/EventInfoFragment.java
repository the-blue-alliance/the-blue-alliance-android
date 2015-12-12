package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class EventInfoFragment
        extends DatafeedFragment<Event, EventInfoBinder.Model, EventInfoSubscriber, EventInfoBinder> {

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(mBinder);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(mBinder);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Event> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEvent(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventInfo_%1$s", mEventKey);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_event_info);
    }
}
