package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class EventInfoFragment extends DatafeedFragment<EventInfoSubscriber, EventInfoBinder>
  implements View.OnClickListener {

    private static final String KEY = "eventKey";

    private String mEventKey;

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
        mComponent.inject(this);
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        mSubscriber.setConsumer(mBinder);
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<Event> mEventObservable = mDatafeed.fetchEvent(mEventKey);
        mEventObservable
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.computation())
          .subscribe(mSubscriber);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubscriber != null) {
            mSubscriber.unsubscribe();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, null);
        mBinder.setView(view);
        mBinder.mEventName = (TextView) view.findViewById(R.id.event_name);
        mBinder.mEventDate = (TextView) view.findViewById(R.id.event_date);
        mBinder.mEventLoc = (TextView) view.findViewById(R.id.event_location);
        mBinder.mEventVenue = (TextView) view.findViewById(R.id.event_venue);
        mBinder.mTopTeamsContainer = view.findViewById(R.id.event_top_teams_container);
        mBinder.mTopOprsContainer = view.findViewById(R.id.event_top_oprs_container);
        mBinder.mTopTeams = (TextView) view.findViewById(R.id.event_top_teams);
        mBinder.mTopOprs = (TextView) view.findViewById(R.id.event_top_oprs);
        mBinder.mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        return view;
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
            //TODO intent to add to calendar
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

    protected void showLastMatch(MatchListElement match) {
        LinearLayout lastLayout = (LinearLayout) getView().findViewById(R.id.event_last_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    protected void showNextMatch(MatchListElement match) {
        LinearLayout nextLayout = (LinearLayout) getView().findViewById(R.id.event_next_match_container);
        nextLayout.setVisibility(View.VISIBLE);
        if (nextLayout.getChildCount() > 1) {
            nextLayout.removeViewAt(1);
        }
        nextLayout.addView(match.getView(getActivity(), getActivity().getLayoutInflater(), null));
    }

    public void onEvent(LiveEventMatchUpdateEvent event) {
        if (event.getLastMatch() != null) {
            Log.d(Constants.LOG_TAG, "showing last match");
            showLastMatch(event.getLastMatch().render());
        }
        if (event.getNextMatch() != null) {
            Log.d(Constants.LOG_TAG, "showing next match");
            showNextMatch(event.getNextMatch().render());
        }
    }
}
