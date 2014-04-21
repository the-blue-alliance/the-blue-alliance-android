package com.thebluealliance.androidtest.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.activities.ViewEvent;
import com.thebluealliance.androidtest.adapters.ListViewAdapter;
import com.thebluealliance.androidtest.datatypes.EventWeekHeader;
import com.thebluealliance.androidtest.datatypes.ListElement;
import com.thebluealliance.androidtest.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventList extends AsyncTask<String,String,String> {

    private Activity activity;
    private View view;
    private ArrayList<String> eventKeys;
    private ArrayList<ListItem> events;
    private ListViewAdapter adapter;

    public PopulateEventList(Activity activity, View view){
        this.activity = activity;
        this.view = view;

        eventKeys = new ArrayList<String>();
        events = new ArrayList<ListItem>();
    }

    @Override
    protected String doInBackground(String... params) {

        /* Here, we would normally check if the events are stored locally, and fetch/store them if not.
         * Also, here is where we check if the remote data set has changed and update accordingly
         * Then, we'd go through the data and build the listview adapters
         * For now, it'll just be static data for demonstrative purposes
         */

        eventKeys.add("week1");         events.add(new EventWeekHeader("Week 1"));
        eventKeys.add("2014nhnas");     events.add(new ListElement("Granite State District Event","2014nhnas"));

        eventKeys.add("week2");         events.add(new EventWeekHeader("Week 2"));
        eventKeys.add("2014nhdur");     events.add(new ListElement("UNH District Event","2014nhdur"));
        eventKeys.add("2014ctgro");     events.add(new ListElement("Groton District Event","2014ctgro"));

        eventKeys.add("week3");         events.add(new EventWeekHeader("Week 3"));
        eventKeys.add("2014mawor");     events.add(new ListElement("WPI District Event","2014mawor"));

        eventKeys.add("week4");         events.add(new EventWeekHeader("Week 4"));
        eventKeys.add("2014rismi");     events.add(new ListElement("Rhode Island District Event","2014rismi"));
        eventKeys.add("2014ctsou");     events.add(new ListElement("Southington District Event","2014ctsou"));

        eventKeys.add("week5");         events.add(new EventWeekHeader("Week 5"));
        eventKeys.add("2014mabos");     events.add(new ListElement("Northeastern University District Event","2014mabos"));
        eventKeys.add("2014cthar");     events.add(new ListElement("Hartford District Event","2014cthar"));

        eventKeys.add("week6");         events.add(new EventWeekHeader("Week 6"));
        eventKeys.add("2014melew");     events.add(new ListElement("Pine Tree District Event","2014melew"));

        eventKeys.add("week7");         events.add(new EventWeekHeader("Week 7"));
        eventKeys.add("2014necmp");     events.add(new ListElement("New England FRC Region Championship","20124necmp"));

        adapter = new ListViewAdapter(activity,events,eventKeys);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView)view.findViewById(R.id.event_list);
        eventList.setAdapter(adapter);

        //set to open basic event view. More static data to be removed later...
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.startActivity(new Intent(activity,ViewEvent.class));
            }
        });
    }
}
