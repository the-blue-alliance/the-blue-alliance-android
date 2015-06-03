package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventStats;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.Arrays;

/**
 * Fragment that displays the team statistics for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p>
 *         File created by phil on 4/22/14.
 */
public class EventStatsFragment extends Fragment implements RefreshListener {

    private AlertDialog statsDialog;
    private String[] items;

    private Activity parent;

    private String mEventKey;
    private static final String KEY = "eventKey", SORT = "sort";

    private Parcelable mListState;
    private EventStatsFragmentAdapter mAdapter;
    private ListView mListView;
    private String statSortCategory;
    private int selectedStatSort = -1;

    private PopulateEventStats mTask;

    /**
     * Creates new event stats fragment for an event.
     *
     * @param eventKey key that represents an FRC event
     * @return new event stats fragment.
     */
    public static EventStatsFragment newInstance(String eventKey) {
        EventStatsFragment f = new EventStatsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Reload key if returning from another fragment/activity
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        parent = getActivity();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).registerRefreshListener(this);
        }

        if (savedInstanceState != null) {
            selectedStatSort = savedInstanceState.getInt(SORT, -1);
        }
        if (selectedStatSort == -1) {
            /* Sort has not yet been set. Default to OPR */
            selectedStatSort = Arrays.binarySearch(getResources().getStringArray(R.array.statsDialogArray),
                    getString(R.string.dialog_stats_sort_opr));
        }

        // Setup stats sort dialog box
        items = getResources().getStringArray(R.array.statsDialogArray);
        statSortCategory = getSortTypeFromPosition(selectedStatSort);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_stats_title)
                .setSingleChoiceItems(items, selectedStatSort, (dialogInterface, i) -> {
                    selectedStatSort = i;
                    statSortCategory = getSortTypeFromPosition(selectedStatSort);

                    dialogInterface.dismiss();

                    mAdapter = (EventStatsFragmentAdapter) mListView.getAdapter();
                    if (mAdapter != null && statSortCategory != null) {
                        mAdapter.sortStats(statSortCategory);
                    }
                }).setNegativeButton(R.string.dialog_cancel, (dialog, id) -> {
            dialog.cancel();
        });

        statsDialog = builder.create();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stats_sort_menu, menu);
        inflater.inflate(R.menu.stats_help_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listeners
        View view = inflater.inflate(R.layout.list_view_with_spinner_2, null);
        mListView = (ListView) view.findViewById(R.id.list);

        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }

        mListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
            if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
                if (TeamHelper.validateMultiTeamKey(teamKey)) {
                    // Take out extra letter at end to make team key valid.
                    teamKey = teamKey.substring(0, teamKey.length() - 1);
                }
                startActivity(TeamAtEventActivity.newInstance(getActivity(), mEventKey, teamKey));
            } else {
                throw new IllegalArgumentException("OnItemClickListener must be attached to a view with a valid team key set as the tag!");
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by) {
            statsDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, selectedStatSort);
    }

    @Override
    public void onPause() {
        // Save the data if moving away from fragment.
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            mAdapter = (EventStatsFragmentAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + mEventKey + " stats");
        mTask = new PopulateEventStats(this, new RequestParams(true, actionIconPressed), statSortCategory);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mEventKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateEventStats newTask) {
        mTask = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).unregisterRefreshListener(this);
    }

    private String getSortTypeFromPosition(int position) {
        if (items[position].equals(getString(R.string.dialog_stats_sort_opr))) {
            return "opr";
        } else if (items[position].equals(getString(R.string.dialog_stats_sort_dpr))) {
            return "dpr";
        } else if (items[position].equals(getString(R.string.dialog_stats_sort_ccwm))) {
            return "ccwm";
        } else if (items[position].equals(getString(R.string.dialog_stats_sort_team))) {
            return "team";
        }
        return "";
    }

}
