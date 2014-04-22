package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.MatchListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateEventMatches extends AsyncTask<String,String,String> {

    private Activity activity;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventMatches(Activity activity){
        this.activity = activity;

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
    }

    @Override
    protected String doInBackground(String... params) {
        //some more temp data
        teamKeys.add("2014ctgro_qm1");      teams.add(new MatchListElement(true,"Quals 1",new String[]{"3182","3634","2168"},new String[]{"181","4055","237"},23,120,"2014ctgro_qm1"));
        teamKeys.add("2014ctgro_qm2");      teams.add(new MatchListElement(true,"Quals 2",new String[]{"3718","230","5112"},new String[]{"175","4557","125"},60,121,"2014ctgro_qm2"));

        adapter = new ListViewAdapter(activity,teams,teamKeys);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        //ListView eventList = (ListView)activity.findViewById(R.id.match_list);
        //eventList.setAdapter(adapter);
    }

}
