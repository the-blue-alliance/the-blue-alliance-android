package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndDateComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.EventWeekHeader;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventList extends AsyncTask<Void, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private int mYear = -1, mWeek = -1;
    private String mTeamKey = null, mHeader;
    private ArrayList<String> eventKeys;
    private ArrayList<ListItem> events;
    private static HashMap<Integer, HashMap<String, ArrayList<SimpleEvent>>> allEvents = new HashMap<>();
    private RefreshableHostActivity activity;

    public PopulateEventList(EventListFragment fragment, int year, String weekHeader, String teamKey) {
        mFragment = fragment;
        mYear = year;
        mTeamKey = teamKey;
        mHeader = weekHeader;
        activity = (RefreshableHostActivity) mFragment.getActivity();
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
            if (!allEvents.containsKey(mYear)) {
                try {
                    allEvents.put(mYear, DataManager.getEventsByYear(mFragment.getActivity(), mYear).getData());
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to get any events in " + mYear);
                    return APIResponse.CODE.NODATA;
                }
            }
            mWeek = Event.weekNumFromLabel(allEvents.get(mYear), mHeader);
        }

        eventKeys = new ArrayList<>();
        events = new ArrayList<>();

        APIResponse<ArrayList<SimpleEvent>> response;

        if (mYear != -1 && mWeek == -1 && mTeamKey == null) {
            // Return a list of all events for a year
        } else if (mYear != -1 && mWeek != -1 && mTeamKey == null) {
            // Return a list of all events for a week in a given year
            try {
                response = DataManager.getSimpleEventsInWeek(mFragment.getActivity(), mYear, mWeek);
                ArrayList<SimpleEvent> eventData = response.getData();
                if (eventData != null && !eventData.isEmpty()) {
                    Collections.sort(eventData, new EventSortByTypeAndDateComparator());
                    Event.TYPE lastType = null, currentType;
                    for (SimpleEvent event : eventData) {
                        currentType = event.getEventType();
                        // TODO: finish implementing this once we have event type info available
                        if (currentType != lastType) {
                            eventKeys.add(currentType.toString());
                            events.add(new EventWeekHeader(currentType.toString()));
                        }
                        eventKeys.add(event.getEventKey());
                        if (event.getShortName().isEmpty() || event.getShortName() == null)
                        {
                            events.add(event.render());
                        }
                        else {
                            events.add(event.renderWithShortName());
                        }
                        lastType = currentType;
                    }
                }
                return response.getCode();
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "unable to find events for week" + mWeek + " " + mYear);
                e.printStackTrace();
            }
        } else if (mYear != -1 && mWeek == -1 && mTeamKey != null) {
            // Return a list of all events for a team for a given year
            try {
                response = DataManager.getSimpleEventsForTeamInYear(mFragment.getActivity(), mTeamKey, mYear);
                ArrayList<SimpleEvent> eventsArray = response.getData();
                Collections.sort(eventsArray, new EventSortByTypeAndDateComparator());
                Event.TYPE lastType = null, currentType;
                for (SimpleEvent event : eventsArray) {
                    currentType = event.getEventType();
                    // TODO: finish implementing this once we have event type info available
                    if (currentType != lastType) {
                        eventKeys.add(currentType.toString());
                        events.add(new EventWeekHeader(currentType.toString()));
                    }
                    eventKeys.add(event.getEventKey());
                    events.add(event.renderWithShortName());
                    lastType = currentType;
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
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        View view = mFragment.getView();
        if (view != null && activity != null) {
            ListView eventList = (ListView) view.findViewById(R.id.list);
            ListViewAdapter adapter = new ListViewAdapter(activity, events);
            eventList.setAdapter(adapter);

            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            if (adapter.values.isEmpty()){
                noDataText.setText(R.string.no_event_data);
                noDataText.setVisibility(View.VISIBLE);
            }

            if (c == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(mFragment.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}