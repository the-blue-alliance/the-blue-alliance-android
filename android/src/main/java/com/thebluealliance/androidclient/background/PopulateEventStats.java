package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.RankingListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventStats extends AsyncTask<String, Void, Void> {

    private Activity activity;
    private View view;
    private String eventKey;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventStats(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        //add some temp data
        teamKeys.add("frc1261");
        teams.add(new RankingListElement("frc1261", "1261", "", "Robo Lions", "88.88 OPR"));
        teamKeys.add("frc1772");
        teams.add(new RankingListElement("frc1772", "1772", "", "The Brazilian Trail Blazers", "83.84 OPR"));
        teamKeys.add("frc3824");
        teams.add(new RankingListElement("frc3824", "3824", "", "HVA RoHAWKtics", "71.54 OPR"));
        teamKeys.add("frc1024");
        teams.add(new RankingListElement("frc1024", "1024", "", "Kil-A-Bytes", "63.76 OPR"));

        adapter = new ListViewAdapter(activity, teams, teamKeys);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ListView rankings = (ListView) view.findViewById(R.id.event_ranking);
        rankings.setAdapter(adapter);
    }

}
