package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.eventbus.LiveEventEventUpdateEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Lazy;
import rx.Observable;

public class TeamInfoFragment
        extends DatafeedFragment<Team, TeamInfoBinder.Model, TeamInfoSubscriber, TeamInfoBinder> {

    private static final String TEAM_KEY = "team_key";

    private String mTeamKey;

    @Inject
    Lazy<EventRenderer> mEventRenderer;

    public static TeamInfoFragment newInstance(String teamKey) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTeamKey = getArguments().getString(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("TeamInfoFragment must be created with a team key!");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);

        mBinder.setRootView(view);

        return view;
    }

    public void showCurrentEvent(final EventListElement event) {

        final FrameLayout eventLayout = (FrameLayout) getView()
                .findViewById(R.id.team_current_event);
        final RelativeLayout container = (RelativeLayout) getView()
                .findViewById(R.id.team_current_event_container);

        getActivity().runOnUiThread(() -> {
            eventLayout.removeAllViews();
            eventLayout.addView(event.getView(getActivity(),
                    getActivity().getLayoutInflater(), null));

            container.setVisibility(View.VISIBLE);
            container.setTag(mTeamKey + "@" + event.getEventKey());
            container.setOnClickListener(new TeamAtEventClickListener(getActivity()));
        });
    }

    @SuppressWarnings("unused")
    public void onEvent(LiveEventEventUpdateEvent event) {
        if (event.getEvent() != null) {
            showCurrentEvent(mEventRenderer.get().renderFromModel(event.getEvent(), null));
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Team> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchTeam(mTeamKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamInfo_%1$s", mTeamKey);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_team_info);
    }
}
