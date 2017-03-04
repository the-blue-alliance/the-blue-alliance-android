package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.itemviews.TeamRankingItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;

import android.os.Bundle;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * Fragment that displays the rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 */
public class EventRankingsFragment extends RecyclerViewFragment<RankingResponseObject, RankingsListSubscriber, RecyclerViewBinder> {

    public static final String KEY = "eventKey";

    private String mEventKey;

    /**
     * Creates new rankings fragment for an event
     *
     * @param eventKey the key that represents an FRC event
     * @return new event rankings fragment
     */
    public static EventRankingsFragment newInstance(String eventKey) {
        EventRankingsFragment f = new EventRankingsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Reload key if returning from another activity/fragment
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<RankingResponseObject> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventRankings(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventRankings_%1$s", mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_ranking_data);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(TeamRankingViewModel.class, TeamRankingItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.TEAM_RANKING_CLICKED && item instanceof TeamRankingViewModel) {
                TeamRankingViewModel ranking = (TeamRankingViewModel) item;
                startActivity(TeamAtEventActivity.newInstance(getContext(), mEventKey, ranking.getTeamKey()));

                /* Track the call */
                AnalyticsHelper.sendClickUpdate(getActivity(), "team@event_click", "EventRankingsFragment", mEventKey);
            } else if (actionId == Interactions.EXPAND_TEAM_RANKING && view instanceof TeamRankingItemView) {
                TeamRankingItemView itemView = (TeamRankingItemView) view;
                itemView.toggleRankingsExpanded();
            }
        });
    }
}
