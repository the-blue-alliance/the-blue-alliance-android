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
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.district.PopulateDistrictList;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by phil on 7/23/14.
 */
public class DistrictListFragment extends Fragment implements RefreshListener {

    public static final String YEAR = "year";

    private Activity mParent;
    private int mYear;
    private PopulateDistrictList mTask;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static DistrictListFragment newInstance(int year) {
        DistrictListFragment f = new DistrictListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYear = getArguments().getInt(YEAR, Utilities.getCurrentYear());
        }
        mParent = getActivity();

        if (mParent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mParent).registerRefreshableActivityListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listeners
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);

        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onPause() {
        // Save the data if moving away from fragment.
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
        if (mParent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mParent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        Log.d(Constants.REFRESH_LOG, "Loading " + mYear + " districts");
        mTask = new PopulateDistrictList(this, true);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mYear);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateDistrictList newTask) {
        mTask = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) mParent).deregisterRefreshableActivityListener(this);
    }
}
