package com.thebluealliance.androidclient.fragments.district;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateEventList;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listeners.EventClickListener;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictEventsFragment extends Fragment implements RefreshListener {

    public static final String KEY = "districtKey";

    private Activity mParent;
    private String mKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private PopulateEventList mTask;

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

        mParent = getActivity();
        if (getArguments() == null || !getArguments().containsKey(KEY)) {
            throw new IllegalArgumentException("DistrictEventsFragment must be constructed with district key");
        }
        mKey = getArguments().getString(KEY);

        if (mParent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mParent).registerRefreshableActivityListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        ProgressBar mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener(new EventClickListener(getActivity(), null));
        return v;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mParent != null && mParent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mParent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        Log.d(Constants.REFRESH_LOG, "Loading events for district " + mKey);
        mTask = new PopulateEventList(this, mKey, true);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mParent != null && mParent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mParent).deregisterRefreshableActivityListener(this);
        }
    }
}
