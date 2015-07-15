package com.thebluealliance.androidclient.fragments.teamAtEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;

import rx.Observable;

public class TeamAtEventSummaryFragment
  extends ListviewFragment<JsonArray, TeamAtEventSummarySubscriber> {

    public static final String TEAM_KEY = "team";
    public static final String EVENT_KEY = "event";
    public static final String DATAFEED_TAG_FORMAT = "team_at_event_summary_%1$s+%2$s";

    private String mTeamKey;
    private String mEventKey;
    private String mDatafeedTag;
    private String[] mExtraTags;

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
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(TEAM_KEY) || !getArguments().containsKey(EVENT_KEY)) {
            throw new IllegalArgumentException("TeamAtEventSummaryFragment must contain both team key and event key");
        }

        mTeamKey = getArguments().getString(TEAM_KEY);
        mEventKey = getArguments().getString(EVENT_KEY);
        mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mTeamKey, mEventKey);
        mExtraTags = new String[]{String.format(EventInfoFragment.DATAFEED_TAG_FORMAT, mEventKey)};

        mSubscriber.setEventKey(mEventKey);
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
    protected Observable<JsonArray> getObservable() {
        return mDatafeed.fetchTeamAtEventRank(mTeamKey, mEventKey);
    }

    @Override
    protected Observable[] getExtraObservables() {
        return new Observable[]{mDatafeed.fetchEvent(mEventKey)};
    }

    @Override
    protected String[] getExtraDatafeedTags() {
        return mExtraTags;
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
