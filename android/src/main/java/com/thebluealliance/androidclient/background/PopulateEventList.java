package com.thebluealliance.androidclient.background;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndDateComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.EventListElement;
import com.thebluealliance.androidclient.datatypes.EventWeekHeader;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventList extends AsyncTask<Void, Void, Void> {

    private Fragment mFragment;
    private int mYear = -1, mWeek = -1;
    private String mTeamKey = null;
    private ArrayList<String> eventKeys;
    private ArrayList<ListItem> events;
    private ListViewAdapter adapter;

    public PopulateEventList(EventListFragment fragment, int year, int week, String teamKey) {
        mFragment = fragment;
        mYear = year;
        mWeek = week;
        mTeamKey = teamKey;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mFragment == null) {
            throw new IllegalArgumentException("Fragment must not be null!");
        }
        /* Here, we would normally check if the events are stored locally, and fetch/store them if not.
         * Also, here is where we check if the remote data set has changed and update accordingly
         * Then, we'd go through the data and build the listview adapters
         * For now, it'll just be static data for demonstrative purposes
         */

        eventKeys = new ArrayList<String>();
        events = new ArrayList<ListItem>();

        if (mYear != -1 && mWeek == -1 && mTeamKey == null) {
            // Return a list of all events for a year
        } else if (mYear != -1 && mWeek != -1 && mTeamKey == null) {
            // Return a list of all events for a week in a given year
            switch (mWeek) {
                default:
                case 1:
                    eventKeys.add("regionals");
                    events.add(new EventWeekHeader("Regional Competitions"));
                    eventKeys.add("2014scmb");
                    events.add(new EventListElement("2014scmb", "Palmetto Regional", "Feb 27th to Mar 1st, 2014", "Myrtle Beach, SC"));
                    eventKeys.add("2014ilil");
                    events.add(new EventListElement("2014ilil", "Central Illinois Regional", "Feb 27th to Mar 1st, 2014", "Pekin, IL"));
                    eventKeys.add("2014casb");
                    events.add(new EventListElement("2014casb", "Inland Empire Regional", "Feb 27th to Mar 1st, 2014", "Grand Terrace, CA"));

                    eventKeys.add("discrict_ne");
                    events.add(new EventWeekHeader("New England District Events"));
                    eventKeys.add("2014nhnas");
                    events.add(new EventListElement("2014nhnas", "Granite State District Event", "Feb 28th to Mar 1st, 2014", "Nashua, NH"));

                    eventKeys.add("discrict_fim");
                    events.add(new EventWeekHeader("Michigan District Events"));
                    eventKeys.add("2014misou");
                    events.add(new EventListElement("2014misou", "Southfield District Event", "Feb 28th to Mar 1st, 2014", "Southfield, MI"));
                    break;
                case 2:
                    eventKeys.add("regionals");
                    events.add(new EventWeekHeader("Regional Competitions"));
                    eventKeys.add("2014arfa");
                    events.add(new EventListElement("2014arfa", "Arkansas Regional", "Mar 6th to Mar 8th, 2014", "Search, AK"));
                    eventKeys.add("2014casd");
                    events.add(new EventListElement("2014casd", "San Diego Regional", "Mar 6th to Mar 8th, 2014", "San Diego, CA"));
                    eventKeys.add("2014inth");
                    events.add(new EventListElement("2014inth", "Crossroads Regional", "Mar 6th to Mar 8th, 2014", "Terre Haute, IN"));

                    eventKeys.add("discrict_ne");
                    events.add(new EventWeekHeader("New England District Events"));
                    eventKeys.add("2014nhdur");
                    events.add(new EventListElement("2014nhdur", "UNH District Event", "Mar 6th to Mar 7th, 2014", "Durham, NH"));
                    eventKeys.add("2014ctgro");
                    events.add(new EventListElement("2014ctgro", "Groton District Event", "Mar 8th to Mar 9th, 2014", "Groton, CT"));

                    eventKeys.add("discrict_fim");
                    events.add(new EventWeekHeader("Michigan District Events"));
                    eventKeys.add("2014migul");
                    events.add(new EventListElement("2014migul", "Gull Lake District Event", "Mar 7th to Mar 8th, 2014", "Richland, MI"));
                    eventKeys.add("2014miket");
                    events.add(new EventListElement("2014miket", "Kettering District Event", "Mar 7th to Mar 8th, 2014", "Flint, MI"));
                    break;
                case 3:
                    eventKeys.add("regionals");
                    events.add(new EventWeekHeader("Regional Competitions"));
                    eventKeys.add("2014flor");
                    events.add(new EventListElement("2014casa", "Orlando Regional", "Mar 13th to Mar 15th, 2014", "Orlando, FL"));
                    eventKeys.add("2014casa");
                    events.add(new EventListElement("2014casa", "Sacramento Regional", "Mar 13th to Mar 15th, 2014", "Sacramento, CA"));
                    eventKeys.add("2014mokc");
                    events.add(new EventListElement("2014mokc", "Greater Kansas City Regional", "Mar 13th to Mar 15th, 2014", "Kansas City, MO"));

                    eventKeys.add("discrict_ne");
                    events.add(new EventWeekHeader("New England District Events"));
                    eventKeys.add("2014mawor");
                    events.add(new EventListElement("2014mawor", "WPI District Event", "Mar 13th to Mar 14th, 2014", "Worcester, MA"));

                    eventKeys.add("discrict_fim");
                    events.add(new EventWeekHeader("Michigan District Events"));
                    eventKeys.add("2014miesc");
                    events.add(new EventListElement("2014miesc", "Escanaba District Event", "Mar 14th to Mar 15th, 2014", "Escanaba, MI"));
                    eventKeys.add("2014mihow");
                    events.add(new EventListElement("2014mihow", "Howell District Event", "Mar 14th to Mar 15th, 2014", "Howell, MI"));
                    break;
            }
            adapter = new ListViewAdapter(mFragment.getActivity(), events, eventKeys);
            return null;
        } else if (mYear != -1 && mWeek == -1 && mTeamKey != null) {
            try {
                ArrayList<SimpleEvent> eventsArray = DataManager.getEventsForTeamInYear(mFragment.getActivity(), mTeamKey, mYear);
                Collections.sort(eventsArray, new EventSortByTypeAndDateComparator());
                Event.TYPE lastType = null;
                for (SimpleEvent event : eventsArray) {
                    Event.TYPE currentType = event.getEventType();
                    // TODO: finish implementing this once we have event type info available
                    if (currentType != lastType) {
                        eventKeys.add(currentType.toString());
                        events.add(new EventWeekHeader(currentType.toString()));
                    }
                    eventKeys.add(event.getEventKey());
                    events.add(event.render());
                    lastType = currentType;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new ListViewAdapter(mFragment.getActivity(), events, eventKeys);
            return null;
        } else if (mYear != -1 && mWeek != -1 && mTeamKey != null) {
            // Return a list of all events for a given team in a given week in a given year
        }
        eventKeys.add("regionals");
        events.add(new EventWeekHeader("Regional Competitions"));
        eventKeys.add("2014scmb");
        events.add(new EventListElement("2014scmb", "Palmetto Regional", "Feb 27th to Mar 1st, 2014", "Myrtle Beach, SC"));
        eventKeys.add("2014ilil");
        events.add(new EventListElement("2014ilil", "Central Illinois Regional", "Feb 27th to Mar 1st, 2014", "Pekin, IL"));
        eventKeys.add("2014casb");
        events.add(new EventListElement("2014casb", "Inland Empire Regional", "Feb 27th to Mar 1st, 2014", "Grand Terrace, CA"));
        adapter = new ListViewAdapter(mFragment.getActivity(), events, eventKeys);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView) mFragment.getView().findViewById(R.id.event_list);
        eventList.setAdapter(adapter);

        //set to open basic event view. More static data to be removed later...
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFragment.getActivity().startActivity(new Intent(mFragment.getActivity(), ViewEventActivity.class));
            }
        });
    }
}
