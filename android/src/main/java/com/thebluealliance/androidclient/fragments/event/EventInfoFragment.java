package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class EventInfoFragment
  extends DatafeedFragment<Event, EventInfoBinder.Model, EventInfoSubscriber, EventInfoBinder>
  implements View.OnClickListener {

    private static final String KEY = "eventKey";
    public static final String DATAFEED_TAG_FORMAT = "event_info_%1$s";

    private String mEventKey;
    private String mDatafeedTag;

    public static EventInfoFragment newInstance(String eventKey) {
        EventInfoFragment f = new EventInfoFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
            mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mEventKey);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, null);
        mBinder.setInflator(inflater);
        mBinder.mView = view;
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

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(mBinder);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(mBinder);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Event> getObservable() {
        return mDatafeed.fetchEvent(mEventKey);
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
