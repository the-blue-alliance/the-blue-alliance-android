package com.thebluealliance.androidclient.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.maps.TeamPageCombiner;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * Displays 1000 team numbers starting with {@link #START}
 */
public class TeamListFragment extends ListViewFragment<List<Team>, TeamListSubscriber> {

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
        int teamNumberStart = getArguments().getInt(START);

        mPageStart = teamNumberStart / 500;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            List<Team> teams = mSubscriber.getApiData();
            if (teams == null) {
                return;
            }
            String teamKey = teams.get(position).getKey();
            Intent i = new Intent(getActivity(), ViewTeamActivity.class);
            i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);

            AnalyticsHelper.sendClickUpdate(getActivity(), "team_click", i.getDataString(), teamKey);

            startActivity(i);
        });

        // Enable fast scrolling
        mListView.setFastScrollEnabled(true);
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
