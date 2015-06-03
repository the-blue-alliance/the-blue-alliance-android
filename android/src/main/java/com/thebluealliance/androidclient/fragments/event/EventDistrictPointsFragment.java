package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventDistrictPoints;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 7/26/14.
 */
public class EventDistrictPointsFragment extends Fragment implements RefreshListener {

    private Activity parent;

    private String mEventKey;
    private static final String KEY = "event_key";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    private View moreInfoContainer;

    private PopulateEventDistrictPoints mTask;

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
        parent = getActivity();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).registerRefreshListener(this);
        }
        isDistrict = true;
        setHasOptionsMenu(true);
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
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        mListView.setSelector(R.drawable.transparent);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + mEventKey + " teams");
        mTask = new PopulateEventDistrictPoints(this, new RequestParams(true, actionIconPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mEventKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateEventDistrictPoints newTask) {
        mTask = newTask;
    }

    public void updateDistrict(boolean isDistrict) {
        this.isDistrict = isDistrict;
        if (getView() == null) {
            return;
        }

        moreInfoContainer = getView().findViewById(R.id.more_info_container);
        if (isDistrict) {
            moreInfoContainer.setVisibility(View.GONE);
        } else {
            moreInfoContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).unregisterRefreshListener(this);
    }
}
