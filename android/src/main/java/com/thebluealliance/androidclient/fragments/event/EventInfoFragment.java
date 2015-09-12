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
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class EventInfoFragment
  extends DatafeedFragment<Event, EventInfoBinder.Model, EventInfoSubscriber, EventInfoBinder>
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
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info, null);
        mBinder.setInflater(inflater);
        mBinder.view = view;
        mBinder.content = view.findViewById(R.id.content);
        mBinder.eventName = (TextView) view.findViewById(R.id.event_name);
        mBinder.eventDate = (TextView) view.findViewById(R.id.event_date);
        mBinder.eventLoc = (TextView) view.findViewById(R.id.event_location);
        mBinder.eventVenue = (TextView) view.findViewById(R.id.event_venue);
        mBinder.topTeamsContainer = view.findViewById(R.id.top_teams_container);
        mBinder.topOprsContainer = view.findViewById(R.id.top_oprs_container);
        mBinder.topTeams = (TextView) view.findViewById(R.id.top_teams);
        mBinder.topOprs = (TextView) view.findViewById(R.id.top_oprs);
        mBinder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mBinder.setNoDataView((NoDataView) view.findViewById(R.id.no_data));
        return view;
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
    protected Observable<Event> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEvent(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventInfo_%1$s", mEventKey);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_event_info);
    }
}
