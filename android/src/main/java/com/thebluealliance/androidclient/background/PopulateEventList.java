package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEvent;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.EventWeekHeader;
import com.thebluealliance.androidclient.datatypes.ListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;

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
    }

    @Override
    protected String doInBackground(String... params) {
        String year = params[0],
                      competitionWeek = params[1];
        if(activity == null) return "";
        /* Here, we would normally check if the events are stored locally, and fetch/store them if not.
         * Also, here is where we check if the remote data set has changed and update accordingly
         * Then, we'd go through the data and build the listview adapters
         * For now, it'll just be static data for demonstrative purposes
         */

        eventKeys = new ArrayList<String>();
        events = new ArrayList<ListItem>();

        switch(competitionWeek){
            default: case "week1":
                eventKeys.add("regionals");         events.add(new EventWeekHeader("Regional Competitions"));
                eventKeys.add("2014scmb");          events.add(new ListElement("Palmetto Regional","2014scmb"));
                eventKeys.add("2014ilil");          events.add(new ListElement("Central Illinios Regional","2014ilil"));
                eventKeys.add("2014casb");          events.add(new ListElement("Inland Empire Regional","2014casb"));

                eventKeys.add("discrict_ne");       events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014nhnas");         events.add(new ListElement("Granite State District Event","2014nhnas"));

                eventKeys.add("discrict_fim");      events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014misou");         events.add(new ListElement("Southfield District Event","2014misou"));
                break;
            case "week2":
                eventKeys.add("regionals");         events.add(new EventWeekHeader("Regional Competitions"));
                eventKeys.add("2014arfa");          events.add(new ListElement("Arkansas Regional","2014arfa"));
                eventKeys.add("2014casd");          events.add(new ListElement("San Diego Regional","2014casd"));
                eventKeys.add("2014inth");          events.add(new ListElement("Crossroads Regional","2014inth"));

                eventKeys.add("discrict_ne");       events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014nhdur");         events.add(new ListElement("UNH District Event","2014nhdur"));
                eventKeys.add("2014ctgro");         events.add(new ListElement("Groton District Event","2014ctgro"));

                eventKeys.add("discrict_fim");      events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014migul");         events.add(new ListElement("Gull Lake District Event","2014misou"));
                eventKeys.add("2014miket");         events.add(new ListElement("Kettering District Event","2014miket"));
                break;
            case "week3":
                eventKeys.add("regionals");         events.add(new EventWeekHeader("Regional Competitions"));
                eventKeys.add("2014flor");          events.add(new ListElement("Orlando Regional","2014casa"));
                eventKeys.add("2014casa");          events.add(new ListElement("Sacramento Regional","2014casa"));
                eventKeys.add("2014mokc");          events.add(new ListElement("Greater Kansas City Regional","2014mokc"));

                eventKeys.add("discrict_ne");       events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014mawor");         events.add(new ListElement("WPI District Event","2014mawor"));

                eventKeys.add("discrict_fim");      events.add(new EventWeekHeader("New England District Events"));
                eventKeys.add("2014miesc");         events.add(new ListElement("Escanaba District Event","2014miesc"));
                eventKeys.add("2014mihow");         events.add(new ListElement("Howell District Event","2014mihow"));
                break;
        }

        adapter = new ListViewAdapter(activity,events,eventKeys);
        adapter.notifyDataSetChanged();
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
