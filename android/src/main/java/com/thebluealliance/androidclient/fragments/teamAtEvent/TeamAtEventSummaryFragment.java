package com.thebluealliance.androidclient.fragments.teamAtEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAtEventSummaryCombiner;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber.Model;

import rx.Observable;

public class TeamAtEventSummaryFragment
        extends ListViewFragment<Model, TeamAtEventSummarySubscriber> {

    public static final String TEAM_KEY = "team", EVENT_KEY = "event";

    private String mTeamKey;
    private String mEventKey;

    public static TeamAtEventSummaryFragment newInstance(String teamKey, String eventKey) {
        TeamAtEventSummaryFragment f = new TeamAtEventSummaryFragment();
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

        mSubscriber.setTeamKey(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);
        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Model> getObservable(String cacheHeader) {
        return Observable.zip(
                mDatafeed.fetchTeamAtEventRank(mTeamKey, mEventKey, cacheHeader),
                mDatafeed.fetchEvent(mEventKey, cacheHeader),
                new TeamAtEventSummaryCombiner());
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamAtEventSummary_%1$s_%2$s", mTeamKey, mEventKey);
    }

    @Override
    protected boolean shouldRegisterSubscriberToEventBus() {
        return true;
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_team_at_event_summary_data);
    }
}
