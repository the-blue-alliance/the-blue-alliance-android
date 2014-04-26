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
public class PopulateEventRankings extends AsyncTask<String, Void, Void> {

    private Activity activity;
    private View view;
    private String eventKey;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventRankings(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        //add some temp data
        teamKeys.add("frc3824");
        teams.add(new RankingListElement("frc3824", "3824", "#1 (9-0-0)", "HVA RoHAWKtics", "18 QS, 200 Assist, 401 Auto, 240 T&C, 312 Teleop"));
        teamKeys.add("frc1876");
        teams.add(new RankingListElement("frc1876", "1876", "#2 (9-0-0)", "Beachbotics", "18 QS, 120 Assist, 276 Auto, 100 T&C, 218 Teleop"));
        teamKeys.add("frc2655");
        teams.add(new RankingListElement("frc2655", "2655", "#3 (8-1-0)", "Flying Platypi", "16 QS, 170 Assist, 291 Auto, 80 T&C, 176 Teleop"));
        teamKeys.add("frc1261");
        teams.add(new RankingListElement("frc1261", "1261", "#4 (7-2-0)", "Robo Lions", "14 QS, 260 Assist, 386 Auto, 190 T&C, 237 Teleop"));

        adapter = new ListViewAdapter(activity, teams, teamKeys);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ListView rankings = (ListView) view.findViewById(R.id.event_ranking);
        rankings.setAdapter(adapter);
    }
}
