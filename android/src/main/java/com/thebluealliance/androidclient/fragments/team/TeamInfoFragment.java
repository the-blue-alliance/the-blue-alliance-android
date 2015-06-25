package com.thebluealliance.androidclient.fragments.team;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.eventbus.LiveEventEventUpdateEvent;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.schedulers.Schedulers;

public class TeamInfoFragment extends DatafeedFragment<Team, TeamInfoBinder.Model>
  implements View.OnClickListener {

    private static final String TEAM_KEY = "team_key";

    private String mTeamKey;

    @Inject TeamInfoSubscriber mSubscriber;
    @Inject TeamInfoBinder mBinder;

    public static TeamInfoFragment newInstance(String teamKey) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasFragmentComponent) {
            ((HasFragmentComponent) getActivity()).getComponent().inject(this);
        }

        mTeamKey = getArguments().getString(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("TeamInfoFragment must be created with a team key!");
        }

        mSubscriber.setConsumer(mBinder);
        mBinder.setContext(getActivity());
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);
        mBinder.setView(view);
        mBinder.mNoDataText = (TextView) view.findViewById(R.id.no_data);
        mBinder.mInfoContainer = view.findViewById(R.id.team_info_container);
        mBinder.mTeamName = (TextView) view.findViewById(R.id.team_name);
        mBinder.mTeamLocationContainer = view.findViewById(R.id.team_location_container);
        mBinder.mTeamLocation = (TextView) view.findViewById(R.id.team_location);

        // Register this fragment as the callback for all clickable views
        view.findViewById(R.id.team_location_container).setOnClickListener(this);
        view.findViewById(R.id.team_twitter_button).setOnClickListener(this);
        view.findViewById(R.id.team_cd_button).setOnClickListener(this);
        view.findViewById(R.id.team_youtube_button).setOnClickListener(this);
        view.findViewById(R.id.team_website_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mSubscriber.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<Team> mTeamObservable = mDatafeed.fetchTeam(mTeamKey);
        mTeamObservable
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe(mSubscriber);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View view) {
        PackageManager manager = getActivity().getPackageManager();
        if (view.getTag() != null) {

            String uri = view.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(getActivity(), uri, mTeamKey);

            Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            List<ResolveInfo> handlers = manager.queryIntentActivities(i, 0);
            if (!handlers.isEmpty()) {
                // There is an application to handle this intent intent
                startActivity(i);
            } else {
                // No application can handle this intent
                Toast.makeText(getActivity(), "No app can handle that request", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    public void showCurrentEvent(final EventListElement event) {

        final LinearLayout eventLayout = (LinearLayout) getView()
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

    //TODO kill EventBus
    public void onEvent(YearChangedEvent event) {

    }

    public void onEvent(LiveEventEventUpdateEvent event) {
        if (event.getEvent() != null) {
            showCurrentEvent(event.getEvent().render());
        }
    }
}
