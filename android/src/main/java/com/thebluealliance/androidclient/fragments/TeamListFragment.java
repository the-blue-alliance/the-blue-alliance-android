package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.datafeed.combiners.TeamPageCombiner;
import com.thebluealliance.androidclient.itemviews.TeamItemView;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListRecyclerSubscriber;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * Displays 1000 team numbers starting with {@link #START}
 */
public class TeamListFragment extends RecyclerViewFragment<List<Team>, TeamListRecyclerSubscriber, RecyclerViewBinder> {

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

        // TODO: figure out how to implement this for RecyclerView
        // Enable fast scrolling
        // mListView.setFastScrollEnabled(true);

        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
        mCombiner = new TeamPageCombiner();
    }

    @Override
    protected Observable<List<Team>> getObservable(String tbaCacheHeader) {
        Observable<List<Team>> teamPage = mDatafeed.fetchTeamPage(mPageStart, tbaCacheHeader);
        if (teamPage != null) {
            return teamPage.zipWith(mDatafeed.fetchTeamPage(mPageStart + 1, tbaCacheHeader),
                    mCombiner);
        }
        return null;
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamList_%1$d", mPageStart);
    }

    @Override
    public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(TeamViewModel.class, TeamItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.TEAM_ITEM_CLICKED && item instanceof TeamViewModel) {
                TeamViewModel team = (TeamViewModel) item;
                startActivity(ViewTeamActivity.newInstance(getActivity(), team.getTeamKey()));
            }
        });
    }
}
