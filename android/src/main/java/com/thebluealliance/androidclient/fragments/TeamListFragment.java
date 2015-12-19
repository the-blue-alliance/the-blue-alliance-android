package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.datafeed.combiners.TeamPageCombiner;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
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
        mListView.setOnItemClickListener(new TeamClickListener(getActivity(), mSubscriber));

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
    protected Observable<List<Team>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchTeamPage(mPageStart, tbaCacheHeader)
                .zipWith(mDatafeed.fetchTeamPage(mPageStart + 1, tbaCacheHeader), mCombiner);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamList_%1$d", mPageStart);
    }
}
