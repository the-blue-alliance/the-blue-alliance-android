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

    private String eventKey;
    private static final String KEY = "eventKey";
    private PopulateEventInfo task;
    private Activity parent;
    private Event event;

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
        info.findViewById(R.id.event_date_container).setOnClickListener(this);
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
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + eventKey + " info");
        task = new PopulateEventInfo(this, new RequestParams(true, actionIconPressed));
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
            ((ViewEventActivity) getActivity()).scrollToTab(ViewEventFragmentPagerAdapter.TAB_RANKINGS);  // Rankings
            return;
        } else if (id == R.id.event_top_oprs_container) {
            ((ViewEventActivity) getActivity()).scrollToTab(ViewEventFragmentPagerAdapter.TAB_STATS);  // Stats
            return;
        } else if (id == R.id.event_date_container) {
            if(event == null) {
                return;
            }

            // Calendar stuff isn't working propberly, the intent isn't setting the proper date
            // on the calendar entry. This is disabled for now.

            // Launch the calendar app with the event's info pre-filled
            /*try {
                long startTime = event.getStartDate().getTime();
                long endTime = event.getEndDate().getTime();

                Log.d(Constants.LOG_TAG, "Calendar: " + startTime + " - " + endTime);

                Intent i = new Intent(Intent.ACTION_INSERT);
                i.setData(CalendarContract.Events.CONTENT_URI);
                i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
                i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
                i.putExtra(CalendarContract.Events.TITLE, event.getShortName());
                i.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getVenue());
                i.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                startActivity(i);
                return;
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
                return;
            }*/
            return;
        }

        if (v.getTag() != null || !v.getTag().toString().isEmpty()) {
            String uri = v.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(getActivity(), uri, eventKey);

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

    protected void showLastMatch(MatchListElement match) {
        LinearLayout lastLayout = (LinearLayout) getView().findViewById(R.id.event_last_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    protected void showNextMatch(MatchListElement match){
        LinearLayout nextLayout = (LinearLayout) getView().findViewById(R.id.event_next_match_container);
        nextLayout.setVisibility(View.VISIBLE);
        if (nextLayout.getChildCount() > 1) {
            nextLayout.removeViewAt(1);
        }
        nextLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
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

    // Called when the event has been loaded. We use this to set up the calendar stuff.
    public void onEvent(EventInfoLoadedEvent eventEvent) {
        this.event = eventEvent.getEvent();
    }
}
