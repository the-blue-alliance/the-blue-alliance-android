package com.thebluealliance.androidclient.fragments.event;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.subscribers.StatsListSubscriber;

import java.util.Arrays;

import rx.Observable;

/**
 * Fragment that displays the team statistics for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 */
public class EventStatsFragment
  extends DatafeedFragment<JsonObject, ListViewAdapter, StatsListSubscriber, ListviewBinder> {

    private static final String KEY = "eventKey", SORT = "sort";

    private AlertDialog mStatsDialog;
    private String[] mItems;
    private String mEventKey;
    private Parcelable mListState;
    private EventStatsFragmentAdapter mAdapter;
    private ListView mListView;
    private String mStatSortCategory;
    private int mSelectedStatSort = -1;

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

        if (savedInstanceState != null) {
            mSelectedStatSort = savedInstanceState.getInt(SORT, -1);
        }
        if (mSelectedStatSort == -1) {
            /* Sort has not yet been set. Default to OPR */
            mSelectedStatSort = Arrays.binarySearch(getResources().getStringArray(R.array.statsDialogArray),
              getString(R.string.dialog_stats_sort_opr));
        }

        // Setup stats sort dialog box
        mItems = getResources().getStringArray(R.array.statsDialogArray);
        mStatSortCategory = getSortTypeFromPosition(mSelectedStatSort);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_stats_title)
          .setSingleChoiceItems(mItems, mSelectedStatSort, (dialogInterface, i) -> {
              mSelectedStatSort = i;
              mStatSortCategory = getSortTypeFromPosition(mSelectedStatSort);

              dialogInterface.dismiss();

              mAdapter = (EventStatsFragmentAdapter) mListView.getAdapter();
              if (mAdapter != null && mStatSortCategory != null) {
                  mAdapter.sortStats(mStatSortCategory);
              }
          }).setNegativeButton(R.string.dialog_cancel, (dialog, id) -> {
            dialog.cancel();
        });

        mStatsDialog = builder.create();
        setHasOptionsMenu(true);
        mSubscriber.setEventKey(mEventKey);
        mSubscriber.setStatToSortBy(mStatSortCategory);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stats_sort_menu, menu);
        inflater.inflate(R.menu.stats_help_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listeners
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);

        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = mProgressBar;
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
            mStatsDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mSelectedStatSort);
    }

    @Override
    public void onPause() {
        // Save the data if moving away from fragment.
        super.onPause();
        if (mListView != null) {
            mAdapter = (EventStatsFragmentAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<JsonObject> getObservable() {
        return mDatafeed.fetchEventStats(mEventKey);
    }

    private String getSortTypeFromPosition(int position) {
        if (mItems[position].equals(getString(R.string.dialog_stats_sort_opr))) {
            return "opr";
        } else if (mItems[position].equals(getString(R.string.dialog_stats_sort_dpr))) {
            return "dpr";
        } else if (mItems[position].equals(getString(R.string.dialog_stats_sort_ccwm))) {
            return "ccwm";
        } else if (mItems[position].equals(getString(R.string.dialog_stats_sort_team))) {
            return "team";
        }
        return "";
    }

}
