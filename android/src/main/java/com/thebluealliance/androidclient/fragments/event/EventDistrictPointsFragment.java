package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.DistrictPointsListBinder;
import com.thebluealliance.androidclient.databinding.ListViewWithSpinnerBinding;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import rx.Observable;

@AndroidEntryPoint
public class EventDistrictPointsFragment
        extends DatafeedFragment<JsonElement, List<ListItem>, ListViewWithSpinnerBinding, DistrictPointsListSubscriber, DistrictPointsListBinder> {
    private static final String KEY = "event_key";

    private String mEventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;

    private ListView mListView;

    public static EventDistrictPointsFragment newInstance(String eventKey) {
        EventDistrictPointsFragment f = new EventDistrictPointsFragment();
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
        setHasOptionsMenu(true);
        mSubscriber.setEventKey(mEventKey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.district_point_math, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        mBinder.setRootView(v);
        mListView = (ListView) v.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        TextView nonDistrictWarning = (TextView) v.findViewById(R.id.info_container);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        mBinder.nonDistrictWarning = nonDistrictWarning;

        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    protected Observable<? extends JsonElement> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventDistrictPoints(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventDistrictPoints_%1$s", mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_recent_actors_black_48dp, R.string.no_district_points);
    }
}
