package com.thebluealliance.androidclient.fragments.team;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.eventbus.LiveEventUpdateEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listeners.EventTeamClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;

public class TeamInfoFragment
        extends DatafeedFragment<Team, TeamInfoBinder.Model, TeamInfoSubscriber, TeamInfoBinder> {

    private static final String TEAM_KEY = "team_key";

    private String mTeamKey;

    @Inject EventBus mEventBus;
    @Inject Lazy<EventRenderer> mEventRenderer;
    @Inject AccountController mAccountController;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);

        // Only show space for the FAB if the FAB is visible
        boolean myTbaEnabled = mAccountController.isMyTbaEnabled();
        view.findViewById(R.id.fab_padding).setVisibility(myTbaEnabled ? View.VISIBLE : View.GONE);

        mBinder.setRootView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    public void showCurrentEvent(final EventListElement event) {

        final FrameLayout eventLayout = (FrameLayout) getView()
                .findViewById(R.id.team_current_event);
        final FrameLayout container = (FrameLayout) getView()
                .findViewById(R.id.team_current_event_container);

        getActivity().runOnUiThread(() -> {
            eventLayout.removeAllViews();
            eventLayout.addView(event.getView(getActivity(),
                    getActivity().getLayoutInflater(), null));
            eventLayout.setTag(EventTeamHelper.generateKey(event.getEventKey(), mTeamKey));
            eventLayout.setOnClickListener(new EventTeamClickListener(getActivity()));

            container.setVisibility(View.VISIBLE);

        });
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveEventUpdate(LiveEventUpdateEvent event) {
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

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_team_info);
    }
}
