package com.thebluealliance.androidclient.fragments.teamAtEvent;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAtEventSummaryCombiner;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.itemviews.LabelValueItemView;
import com.thebluealliance.androidclient.itemviews.LabeledMatchItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;
import com.thebluealliance.androidclient.viewmodels.LabeledMatchViewModel;

import javax.inject.Inject;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class TeamAtEventSummaryFragment extends RecyclerViewFragment<TeamAtEventSummarySubscriber.Model, TeamAtEventSummarySubscriber, RecyclerViewBinder> {

    public static final String TEAM_KEY = "team", EVENT_KEY = "event";

    private String mTeamKey;
    private String mEventKey;

    @Inject AccountController mAccountController;

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

        mSubscriber.setTeamAndEventKeys(mTeamKey, mEventKey);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Add padding to the bottom of the list for the myTBA FAB if it is visible
        if (mAccountController.isMyTbaEnabled()) {
            mRecyclerView.setPadding(
                    mRecyclerView.getPaddingLeft(),
                    mRecyclerView.getPaddingTop(),
                    mRecyclerView.getPaddingRight(),
                    getResources().getDimensionPixelSize(R.dimen.fab_list_padding));
            mRecyclerView.setClipToPadding(false);
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<TeamAtEventSummarySubscriber.Model> getObservable(String cacheHeader) {
        return Observable.zip(
                mDatafeed.fetchTeamAtEventStatus(mTeamKey, mEventKey, cacheHeader),
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

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_team_at_event_summary_data);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(LabelValueViewModel.class, LabelValueItemView.class);
        creator.map(LabeledMatchViewModel.class, LabeledMatchItemView.class);
    }
}
