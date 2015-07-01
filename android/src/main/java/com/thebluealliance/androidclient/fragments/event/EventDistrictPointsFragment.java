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

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;

import java.util.List;

import rx.Observable;

public class EventDistrictPointsFragment
  extends DatafeedFragment<JsonObject, List<ListItem>, DistrictPointsListSubscriber, ListviewBinder> {
    private static final String KEY = "event_key";

    private String mEventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    //TODO implement way to show "not part of district" warning
    private boolean isDistrict;

    public static EventDistrictPointsFragment newInstance(String eventKey) {
        EventDistrictPointsFragment f = new EventDistrictPointsFragment();
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
        isDistrict = true;
        setHasOptionsMenu(true);
        mSubscriber.setEventKey(mEventKey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.district_point_math, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_district_points, null);
        mListView = (ListView) view.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        mListView.setSelector(R.drawable.transparent);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    public void updateDistrict(boolean isDistrict) {
        this.isDistrict = isDistrict;
        if (getView() == null) {
            return;
        }

        View moreInfoContainer = getView().findViewById(R.id.more_info_container);
        if (isDistrict) {
            moreInfoContainer.setVisibility(View.GONE);
        } else {
            moreInfoContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<JsonObject> getObservable() {
        return mDatafeed.fetchEventDistrictPoints(mEventKey);
    }
}
