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
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.background.district.PopulateTeamAtDistrictBreakdown;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.views.ExpandableListView;

/**
 * File created by phil on 7/26/14.
 */
public class TeamAtDistrictBreakdownFragment extends Fragment implements RefreshListener {

    public static final String DISTRICT = "districtKey", TEAM = "teamKey";

    private Activity mParent;
    private String teamKey, districtKey;
    private PopulateTeamAtDistrictBreakdown mTask;
    private Parcelable mListState;
    private ExpandableListAdapter mAdapter;
    private ExpandableListView mListView;

    public static TeamAtDistrictBreakdownFragment newInstance(String teamKey, String districtKey) {
        TeamAtDistrictBreakdownFragment f = new TeamAtDistrictBreakdownFragment();
        Bundle args = new Bundle();
        args.putString(TEAM, teamKey);
        args.putString(DISTRICT, districtKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teamKey = getArguments().getString(TEAM);
            districtKey = getArguments().getString(DISTRICT);
        }
        mParent = getActivity();

        if (mParent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) mParent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listeners
        View view = inflater.inflate(R.layout.expandable_listview_with_spinner, null);
        mListView = (ExpandableListView) view.findViewById(R.id.expandable_list);

        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }
        mListView.setSelector(R.drawable.transparent);
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
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mParent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) mParent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.d(Constants.REFRESH_LOG, "Loading breakdown for " + teamKey + " at " + districtKey);
        mTask = new PopulateTeamAtDistrictBreakdown(this, new RequestParams(true, actionIconPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teamKey, districtKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateTeamAtDistrictBreakdown newTask) {
        mTask = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LegacyRefreshableHostActivity) mParent).unregisterRefreshListener(this);
    }

}
