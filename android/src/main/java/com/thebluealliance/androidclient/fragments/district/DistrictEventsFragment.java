package com.thebluealliance.androidclient.fragments.district;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.itemviews.EventItemView;
import com.thebluealliance.androidclient.itemviews.ListSectionHeaderItemView;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListRecyclerSubscriber;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import android.os.Bundle;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class DistrictEventsFragment extends RecyclerViewFragment<List<Event>, EventListRecyclerSubscriber, RecyclerViewBinder> {

    public static final String KEY = "districtKey";

    private String mShort;
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
        String key = getArguments().getString(KEY);
        if (!DistrictHelper.validateDistrictKey(key)) {
            throw new IllegalArgumentException("Invalid district key + " + key);
        }
        mShort = key.substring(4);
        mYear = Integer.parseInt(key.substring(0, 4));
        super.onCreate(savedInstanceState);
        mSubscriber.setRenderMode(EventListRecyclerSubscriber.MODE_DISTRICT);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchDistrictEvents(mShort, mYear, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("districtEvents_%1$s_%2$d", mShort, mYear);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_event_black_48dp, R.string.no_event_data);
    }

    @Override public void initializeMaps(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(EventViewModel.class, EventItemView.class);
        creator.map(ListSectionHeaderViewModel.class, ListSectionHeaderItemView.class);
    }
}
