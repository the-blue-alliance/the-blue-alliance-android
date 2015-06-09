package com.thebluealliance.androidclient.fragments.team;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.background.team.PopulateTeamInfo;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.eventbus.LiveEventEventUpdateEvent;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;

import java.util.List;

import de.greenrobot.event.EventBus;

public class TeamInfoFragment extends Fragment implements View.OnClickListener, RefreshListener {

    private static final String TEAM_KEY = "team_key";

    private ViewTeamActivity mActivity;
    private String mTeamKey;
    private PopulateTeamInfo mTask;


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
        mTeamKey = getArguments().getString(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("TeamInfoFragment must be created with a team key!");
        }
        if (!(getActivity() instanceof ViewTeamActivity)) {
            throw new IllegalArgumentException("TeamMediaFragment must be hosted by a ViewTeamActivity!");
        } else {
            mActivity = (ViewTeamActivity) getActivity();
        }

        mActivity.registerRefreshListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);

        // Register this fragment as the callback for all clickable views
        view.findViewById(R.id.team_location_container).setOnClickListener(this);
        view.findViewById(R.id.team_website_container).setOnClickListener(this);
        view.findViewById(R.id.team_twitter_container).setOnClickListener(this);
        view.findViewById(R.id.team_youtube_container).setOnClickListener(this);
        view.findViewById(R.id.team_cd_container).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.startRefresh(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                Toast.makeText(getActivity(), "No app can handle that request", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + mTeamKey + " info");
        mTask = new PopulateTeamInfo(this, new RequestParams(true, actionIconPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTeamKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateTeamInfo newTask) {
        mTask = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterRefreshListener(this);
    }

    public void showCurrentEvent(final EventListElement event) {

        final ViewGroup eventLayout = (ViewGroup) getView().findViewById(R.id.team_current_event);
        final View container = getView().findViewById(R.id.team_current_event_container);

        getActivity().runOnUiThread(() -> {
            eventLayout.removeAllViews();
            eventLayout.addView(event.getView(getActivity(), getActivity().getLayoutInflater(), null));
            eventLayout.setOnClickListener(new TeamAtEventClickListener(getActivity()));
            eventLayout.setTag(mTeamKey + "@" + event.getEventKey());

            container.setVisibility(View.VISIBLE);
        });
    }

    public void onEvent(YearChangedEvent event) {
        mActivity.notifyRefreshComplete(this);
    }

    public void onEvent(LiveEventEventUpdateEvent event) {
        if (event.getEvent() != null) {
            showCurrentEvent(event.getEvent().render());
        }
    }
}
