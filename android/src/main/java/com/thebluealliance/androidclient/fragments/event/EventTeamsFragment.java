package com.thebluealliance.androidclient.fragments.event;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.fragments.BriteRecyclerViewFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.itemviews.TeamItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListRecyclerSubscriber;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class EventTeamsFragment extends BriteRecyclerViewFragment<List<Team>, TeamListRecyclerSubscriber, RecyclerViewBinder> {

    private static final String KEY = "event_key";

    private String mEventKey;

    public static EventTeamsFragment newInstance(String eventKey) {
        EventTeamsFragment f = new EventTeamsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
        mSubscriber.setRenderMode(Team.RENDER_DETAILS_BUTTON);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override protected Observable<? extends List<Team>> getObservable() {
        return mDatafeed.getEventTeams(mEventKey);
    }

    @Override protected void beginDataUpdate(String tbaCacheHeader) {
        Log.d(Constants.LOG_TAG, "BEGINNING DATA UPDATE FOR " + getClass().getName());
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventTeams_%1$s", mEventKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_group_black_48dp, R.string.no_team_data);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(TeamViewModel.class, TeamItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.TEAM_ITEM_CLICKED && item instanceof TeamViewModel) {
                TeamViewModel team = (TeamViewModel) item;
                Intent intent = TeamAtEventActivity.newInstance(getActivity(), mEventKey, team.getTeamKey());
                startActivity(intent);

                // Track the call
                AnalyticsHelper.sendClickUpdate(getActivity(), "team@event_click", "EventTeamsFragment", EventTeamHelper.generateKey(mEventKey, team.getTeamKey()));

            }
        });
    }
}
