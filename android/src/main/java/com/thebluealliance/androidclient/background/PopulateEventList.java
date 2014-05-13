package com.thebluealliance.androidclient.background;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndDateComparator;
import com.thebluealliance.androidclient.comparators.EventSortByTypeComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.EventWeekHeader;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.dialogs.LoadingDialog;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventList extends AsyncTask<Void, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private BaseActivity activity;
    private int mYear = -1, mWeek = -1;
    private String mTeamKey = null;
    private ArrayList<String> eventKeys;
    private ArrayList<ListItem> events;
    private ListViewAdapter adapter;
    private LoadingDialog dialog;

    public PopulateEventList(EventListFragment fragment, int year, int week, String teamKey) {
        mFragment = fragment;
        activity = (BaseActivity)mFragment.getActivity();
        mYear = year;
        mWeek = week;
        mTeamKey = teamKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = LoadingDialog.newInstance(mFragment.getString(R.string.dialog_loading_title), mFragment.getString(R.string.dialog_loading_event_list));
        dialog.show(activity.getFragmentManager(), "loading event list");
    }

    @Override
    protected APIResponse.CODE doInBackground(Void... params) {
        if (mFragment == null) {
            throw new IllegalArgumentException("Fragment must not be null!");
        }
        /* Here, we would normally check if the events are stored locally, and fetch/store them if not.
         * Also, here is where we check if the remote data set has changed and update accordingly
         * Then, we'd go through the data and build the listview adapters
         * For now, it'll just be static data for demonstrative purposes
         */

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
                Collections.sort(eventData, new EventSortByTypeComparator());
                Event.TYPE lastType = null, currentType;
                for (SimpleEvent event : eventData) {
                    currentType = event.getEventType();
                    // TODO: finish implementing this once we have event type info available
                    if (currentType != lastType) {
                        eventKeys.add(currentType.toString());
                        events.add(new EventWeekHeader(currentType.toString()));
                    }
                    eventKeys.add(event.getEventKey());
                    events.add(event.render());
                    lastType = currentType;
                }
                return response.getCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (mYear != -1 && mWeek == -1 && mTeamKey != null) {
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
                    events.add(event.render());
                    lastType = currentType;
                }
                return response.getCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (mYear != -1 && mWeek != -1 && mTeamKey != null) {
            // Return a list of all events for a given team in a given week in a given year
        }


        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here

       if (mFragment.getView() != null && mFragment.getActivity() != null) {
            adapter = new ListViewAdapter(mFragment.getActivity(), events, eventKeys);
            ListView eventList = (ListView) mFragment.getView().findViewById(R.id.event_list);
            adapter = new ListViewAdapter(mFragment.getActivity(), events, eventKeys);
            eventList.setAdapter(adapter);

            //set to open basic event view
            eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mFragment.getActivity(), ViewEventActivity.class);
                    Bundle data = intent.getExtras();
                    if (data == null) data = new Bundle();
                    if (view.getTag() != null) {
                        data.putString("eventKey", view.getTag().toString());
                        intent.putExtras(data);
                        mFragment.getActivity().startActivity(intent);
                    }
                }
            });

           if(c == APIResponse.CODE.OFFLINECACHE /* && event is current */){
               //TODO only show warning for currently competing event (there's likely missing data)
               ((BaseActivity)mFragment.getActivity()).showWarningMessage(mFragment.getString(R.string.warning_using_cached_data));
           }
        }

        dialog.dismiss();
    }
}
