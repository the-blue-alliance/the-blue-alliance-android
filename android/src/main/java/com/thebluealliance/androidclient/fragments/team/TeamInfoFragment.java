package com.thebluealliance.androidclient.fragments.team;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.background.team.PopulateTeamInfo;
import com.thebluealliance.androidclient.intents.LiveEventBroadcast;
import com.thebluealliance.androidclient.interfaces.OnYearChangedListener;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;

import java.util.List;

public class TeamInfoFragment extends Fragment implements View.OnClickListener, RefreshListener, OnYearChangedListener {

    private static final String TEAM_KEY = "team_key";

    private ViewTeamActivity parent;

    private String mTeamKey;

    private PopulateTeamInfo task;
    private BroadcastReceiver receiver;

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
            parent = (ViewTeamActivity) getActivity();
        }

        parent.registerRefreshableActivityListener(this);
        parent.addOnYearChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_info, container, false);
        // Register this fragment as the callback for all clickable views
        v.findViewById(R.id.team_location_container).setOnClickListener(this);
        v.findViewById(R.id.team_twitter_button).setOnClickListener(this);
        v.findViewById(R.id.team_cd_button).setOnClickListener(this);
        v.findViewById(R.id.team_youtube_button).setOnClickListener(this);
        v.findViewById(R.id.team_website_button).setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        parent.startRefresh(this);
        receiver = new LiveEventBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(LiveEventBroadcast.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {
        PackageManager manager = getActivity().getPackageManager();
        if (view.getTag() != null) {

            String uri = view.getTag().toString();
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
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Loading " + mTeamKey + " info");
        task = new PopulateTeamInfo(this, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTeamKey);
    }

    @Override
    public void onRefreshStop() {
        if (task != null) {
            task.cancel(false);
        }
    }

    public void updateTask(PopulateTeamInfo newTask) {
        task = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parent.deregisterRefreshableActivityListener(this);
    }

    public void showCurrentEvent(EventListElement event){
        LinearLayout eventLayout = (LinearLayout)getView().findViewById(R.id.team_current_event);
        eventLayout.removeAllViews();
        eventLayout.addView(event.getView(getActivity(), getActivity().getLayoutInflater(), null));

        RelativeLayout container = (RelativeLayout) getView().findViewById(R.id.team_current_event_container);
        container.setVisibility(View.VISIBLE); 
        container.setTag(mTeamKey+"@"+event.getEventKey());
        container.setOnClickListener(new TeamAtEventClickListener(getActivity()));
    }

    @Override
    public void onYearChanged(int newYear) {
        parent.notifyRefreshComplete(this);
    }

    class LiveEventBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.LOG_TAG, "Received live event broadcast");
            if(intent.getAction().equals(LiveEventBroadcast.ACTION)){
                if(intent.hasExtra(LiveEventBroadcast.EVENT)){
                    EventListElement event = (EventListElement)intent.getSerializableExtra(LiveEventBroadcast.EVENT);
                    showCurrentEvent(event);
                }
            }
        }
    }
}
