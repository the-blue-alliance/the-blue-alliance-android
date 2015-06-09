package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
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
import android.widget.Toast;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventInfo;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.eventbus.EventInfoLoadedEvent;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Event;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * File created by phil on 4/22/14.
 */
public class EventInfoFragment extends Fragment implements RefreshListener, View.OnClickListener {

    private String mEventKey;
    private static final String KEY = "eventKey";
    private PopulateEventInfo mTask;
    private Activity mActivity;
    private Event mEvent;

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
            mEventKey = getArguments().getString(KEY, "");
        }
        mActivity = getActivity();

        if (mActivity instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mActivity).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, null);
        view.findViewById(R.id.event_venue_container).setOnClickListener(this);
        view.findViewById(R.id.event_website_container).setOnClickListener(this);
        view.findViewById(R.id.event_twitter_container).setOnClickListener(this);
        view.findViewById(R.id.event_youtube_container).setOnClickListener(this);
        view.findViewById(R.id.event_cd_container).setOnClickListener(this);
        view.findViewById(R.id.top_teams_container).setOnClickListener(this);
        view.findViewById(R.id.top_oprs_container).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) mActivity).startRefresh(this);
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
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + mEventKey + " info");
        mTask = new PopulateEventInfo(this, new RequestParams(true, actionIconPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mEventKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateEventInfo newTask) {
        mTask = newTask;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.top_teams_container) {
            ((ViewEventActivity) getActivity()).scrollToTab(ViewEventFragmentPagerAdapter.TAB_RANKINGS);  // Rankings
            return;
        } else if (id == R.id.top_oprs_container) {
            ((ViewEventActivity) getActivity()).scrollToTab(ViewEventFragmentPagerAdapter.TAB_STATS);  // Stats
            return;
        }

        if (v.getTag() != null || !v.getTag().toString().isEmpty()) {
            String uri = v.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(getActivity(), uri, mEventKey);

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
        ((RefreshableHostActivity) mActivity).unregisterRefreshListener(this);
    }

    protected void showLastMatch(MatchListElement match) {
        ViewGroup lastMatchContainer = (ViewGroup) getView().findViewById(R.id.last_match_container);
        FrameLayout lastMatchView = (FrameLayout) getView().findViewById(R.id.last_match_view);
        lastMatchContainer.setVisibility(View.VISIBLE);
        lastMatchView.removeAllViews();
        lastMatchView.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    protected void hideLastMatch() {
        getView().findViewById(R.id.last_match_container).setVisibility(View.GONE);
    }

    protected void showNextMatch(MatchListElement match) {
        ViewGroup nextMatchContainer = (ViewGroup) getView().findViewById(R.id.next_match_container);
        FrameLayout nextMatchView = (FrameLayout) getView().findViewById(R.id.next_match_view);
        nextMatchContainer.setVisibility(View.VISIBLE);
        nextMatchView.removeAllViews();
        nextMatchView.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    protected void hideNextMatch() {
        getView().findViewById(R.id.next_match_container).setVisibility(View.GONE);
    }

    public void onEvent(LiveEventMatchUpdateEvent event) {
        if (event.getLastMatch() != null) {
            Log.d(Constants.LOG_TAG, "showing last match");
            showLastMatch(event.getLastMatch().render());
        } else {
            hideLastMatch();
        }

        if (event.getNextMatch() != null) {
            Log.d(Constants.LOG_TAG, "showing next match");
            showNextMatch(event.getNextMatch().render());
        } else {
            hideNextMatch();
        }
    }

    // Called when the event has been loaded. We use this to set up the calendar stuff.
    public void onEvent(EventInfoLoadedEvent eventEvent) {
        this.mEvent = eventEvent.getEvent();
    }
}
