package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventList extends AsyncTask<Void, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private int mYear = -1, mWeek = -1;
    private String mTeamKey = null, mHeader;
    private ArrayList<ListItem> events;
    private RefreshableHostActivity activity;
    private RefreshableHost host;
    private boolean forceFromCache;

    public PopulateEventList(Fragment fragment, int year, String weekHeader, String teamKey, boolean forceFromCache) {
        mFragment = fragment;
        mYear = year;
        mTeamKey = teamKey;
        mHeader = weekHeader;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.host = activity;
        this.forceFromCache = forceFromCache;
    }

    public PopulateEventList(Fragment fragment, RefreshableHost host, int year, String weekHeader, String teamKey, boolean forceFromCache) {
        mFragment = fragment;
        mYear = year;
        mTeamKey = teamKey;
        mHeader = weekHeader;
        this.host = host;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(Void... params) {
        if (mFragment == null) {
            throw new IllegalArgumentException("Fragment must not be null!");
        }

        //first, let's generate the event week based on its header (event weeks aren't constant over the years)
        if (mHeader.equals("")) {
            mWeek = -1;
        } else {
            mWeek = EventHelper.weekNumFromLabel(mYear, mHeader);
        }

        events = new ArrayList<>();

        APIResponse<ArrayList<Event>> response;

        if (mYear != -1 && mWeek == -1 && mTeamKey == null) {
            // Return a list of all events for a year
        } else if (mYear != -1 && mWeek != -1 && mTeamKey == null) {
            // Return a list of all events for a week in a given year
            try {
                response = DataManager.Events.getSimpleEventsInWeek(mFragment.getActivity(), mYear, mWeek, forceFromCache);

                if (isCancelled()) {
                    return APIResponse.CODE.NODATA;
                }

                ArrayList<Event> eventData = response.getData();
                if (eventData != null && !eventData.isEmpty()) {
                    events = EventHelper.renderEventListForWeek(eventData);
                }
                return response.getCode();
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "unable to find events for week " + mWeek + " " + mYear);
                e.printStackTrace();
            }
        } else if (mYear != -1 && mWeek == -1 && mTeamKey != null) {
            // Return a list of all events for a team for a given year
            Log.d(Constants.LOG_TAG, "Loading events for team " + mTeamKey + " in " + mYear);
            try {
                response = DataManager.Teams.getEventsForTeam(activity, mTeamKey, mYear, forceFromCache);
                ArrayList<Event> eventsArray = response.getData();
                if (eventsArray != null && !eventsArray.isEmpty()) {
                    events = EventHelper.renderEventListForTeam(activity, eventsArray, true);
                }
                return response.getCode();
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "unable to load event list");
                e.printStackTrace();
            }
        } else if (mYear != -1 && mWeek != -1 && mTeamKey != null) {
            // Return a list of all events for a given team in a given week in a given year
        }


        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        View view = mFragment.getView();
        if (view != null && activity != null) {
            ListViewAdapter adapter = new ListViewAdapter(activity, events);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no data in the adapter or if we can't download info
            // off the web, display a message.
            if ((code == APIResponse.CODE.NODATA && !ConnectionDetector.isConnectedToInternet(activity)) || (!forceFromCache && adapter.values.isEmpty())) {
                noDataText.setText(R.string.no_event_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView eventList = (ListView) view.findViewById(R.id.list);
                Parcelable state = eventList.onSaveInstanceState();
                eventList.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                eventList.onRestoreInstanceState(state);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(mFragment.getString(R.string.warning_using_cached_data));
            }

            if(!mHeader.equals("") || mTeamKey != null) {
                view.findViewById(R.id.progress).setVisibility(View.GONE);
                view.findViewById(R.id.list).setVisibility(View.VISIBLE);
            }

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                new PopulateEventList(mFragment, host, mYear, mHeader, mTeamKey, false).execute();
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, "Event list refresh complete");
                host.notifyRefreshComplete((RefreshListener) mFragment);
            }

        }
    }
}