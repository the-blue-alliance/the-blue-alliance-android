package com.thebluealliance.androidclient.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.datafeed.maps.TeamPageCombiner;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * Displays 1000 team numbers starting with {@link #START}
 */
public class TeamListFragment
  extends DatafeedFragment<List<Team>, List<ListItem>, TeamListSubscriber, ListviewBinder> {

    private static final String START = "START";
    private int mPageStart;
    private TeamPageCombiner mCombiner;

    public static TeamListFragment newInstance(int startTeamNumber) {
        TeamListFragment f = new TeamListFragment();
        Bundle args = new Bundle();
        args.putInt(START, startTeamNumber);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int teamNumberStart = getArguments().getInt(START);

        mPageStart = teamNumberStart/500;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setFastScrollAlwaysVisible(true);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String teamKey = ((TeamCursorAdapter) adapterView.getAdapter()).getKey(position);
            Intent i = new Intent(getActivity(), ViewTeamActivity.class);
            i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);

            AnalyticsHelper.sendClickUpdate(getActivity(), "team_click", i.getDataString(), teamKey);

            startActivity(i);
        });
        mBinder.mListView = listView;
        mBinder.mProgressBar = progressBar;
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
        mCombiner = new TeamPageCombiner();
    }

    @Override
    protected Observable<List<Team>> getObservable() {
        return mDatafeed.fetchTeamPage(mPageStart)
          .zipWith(mDatafeed.fetchTeamPage(mPageStart + 1), mCombiner);
    }
}
