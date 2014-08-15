package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.background.event.PopulateEventInfo;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.intents.LiveEventBroadcast;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * File created by phil on 4/22/14.
 */
public class EventInfoFragment extends Fragment implements RefreshListener, View.OnClickListener {

    private String eventKey;
    private static final String KEY = "eventKey";
    private PopulateEventInfo task;
    private Activity parent;

    public static EventInfoFragment newInstance(String eventKey) {
        EventInfoFragment f = new EventInfoFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
        }
        parent = getActivity();

        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View info = inflater.inflate(R.layout.fragment_event_info, null);
        info.findViewById(R.id.event_venue_container).setOnClickListener(this);
        info.findViewById(R.id.event_website_button).setOnClickListener(this);
        info.findViewById(R.id.event_twitter_button).setOnClickListener(this);
        info.findViewById(R.id.event_youtube_button).setOnClickListener(this);
        info.findViewById(R.id.event_cd_button).setOnClickListener(this);
        info.findViewById(R.id.event_top_teams_container).setOnClickListener(this);
        info.findViewById(R.id.event_top_oprs_container).setOnClickListener(this);
        return info;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Loading " + eventKey + " info");
        task = new PopulateEventInfo(this, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventKey);
    }

    @Override
    public void onRefreshStop() {
        if (task != null) {
            task.cancel(false);
        }
    }

    public void updateTask(PopulateEventInfo newTask) {
        task = newTask;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.event_top_teams_container) {
            ((ViewEventActivity) getActivity()).getPager().setCurrentItem(2);  // Rankings
            return;
        } else if (id == R.id.event_top_oprs_container) {
            ((ViewEventActivity) getActivity()).getPager().setCurrentItem(5);  // Stats
            return;
        }
        if (v.getTag() != null || !v.getTag().toString().isEmpty()) {
            String uri = v.getTag().toString();

            //social button was clicked. Track the call
            Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, getActivity());
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("social_click")
                    .setAction(uri)
                    .setLabel(eventKey)
                    .build());


            PackageManager manager = getActivity().getPackageManager();
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
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).unregisterRefreshListener(this);
    }

    protected void showLastMatch(MatchListElement match){
        LinearLayout lastLayout = (LinearLayout) getView().findViewById(R.id.event_last_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    protected void showNextMatch(MatchListElement match){
        LinearLayout lastLayout = (LinearLayout) getView().findViewById(R.id.event_next_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    public void onEvent(LiveEventMatchUpdateEvent event) {
        if(event.getLastMatch() != null){
            Log.d(Constants.LOG_TAG, "showing last match");
            showLastMatch(event.getLastMatch().render());
        }
        if(event.getNextMatch() != null){
            Log.d(Constants.LOG_TAG, "showing next match");
            showNextMatch(event.getNextMatch().render());
        }
    }
}
