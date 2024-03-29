package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.binders.DistrictEventsBinder;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.itemviews.EventItemView;
import com.thebluealliance.androidclient.itemviews.ListSectionHeaderItemView;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

@AndroidEntryPoint
public class DistrictEventsFragment extends RecyclerViewFragment<List<Event>,
        EventListSubscriber, DistrictEventsBinder> {

    public static final String KEY = "districtKey";

    private String mKey;

    public static DistrictEventsFragment newInstance(String key) {
        DistrictEventsFragment f = new DistrictEventsFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String key = getArguments().getString(KEY);
        if (!DistrictHelper.validateDistrictKey(key)) {
            throw new IllegalArgumentException("Invalid district key + " + key);
        }
        mKey = key;
        super.onCreate(savedInstanceState);
        mSubscriber.setRenderMode(EventListSubscriber.MODE_DISTRICT);
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchDistrictEvents(mKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("districtEvents_%1$s", mKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_event_black_48dp, R.string.no_event_data);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(EventViewModel.class, EventItemView.class);
        creator.map(ListSectionHeaderViewModel.class, ListSectionHeaderItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.EVENT_CLICKED && item instanceof EventViewModel) {
                EventViewModel event = (EventViewModel) item;
                startActivity(ViewEventActivity.newInstance(getContext(), event.getKey()));
            }
        });
    }
}
