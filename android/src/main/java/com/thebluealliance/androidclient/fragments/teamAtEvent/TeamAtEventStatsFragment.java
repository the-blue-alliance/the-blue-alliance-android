package com.thebluealliance.androidclient.fragments.teamAtEvent;

import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.itemviews.LabelValueItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamStatsSubscriber;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;

import android.os.Bundle;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class TeamAtEventStatsFragment extends RecyclerViewFragment<JsonElement, TeamStatsSubscriber, RecyclerViewBinder> {

    public static final String TEAM_KEY = "team", EVENT_KEY = "event";

    private String mTeamKey, mEventKey;

    public static TeamAtEventStatsFragment newInstance(String teamKey, String eventKey) {
        TeamAtEventStatsFragment f = new TeamAtEventStatsFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putString(EVENT_KEY, eventKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() == null || !getArguments().containsKey(TEAM_KEY) || !getArguments().containsKey(EVENT_KEY)) {
            throw new IllegalArgumentException("TeamAtEventSummaryFragment must contain both team key and event key");
        }

        mTeamKey = getArguments().getString(TEAM_KEY);
        mEventKey = getArguments().getString(EVENT_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends JsonElement> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchTeamAtEventStats(mEventKey, mTeamKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamAtEventStats_%1$s_%2$s", mTeamKey, mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_stats_data);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(LabelValueViewModel.class, LabelValueItemView.class);
    }
}
