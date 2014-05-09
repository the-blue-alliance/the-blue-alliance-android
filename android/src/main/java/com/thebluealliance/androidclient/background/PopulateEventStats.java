package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.StatsListElement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

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

        DecimalFormat displayFormat = new DecimalFormat("#.##");

        try {
            JsonObject stats = DataManager.getEventStats(activity, eventKey);
            ArrayList<Map.Entry<String,JsonElement>>
                    opr = new ArrayList<>(),
                    dpr = new ArrayList<>(),
                    ccwm = new ArrayList<>();
            opr.addAll(stats.get("oprs").getAsJsonObject().entrySet());
            dpr.addAll(stats.get("dprs").getAsJsonObject().entrySet());
            ccwm.addAll(stats.get("ccwms").getAsJsonObject().entrySet());

            for(int i=0;i<opr.size();i++){
                String statsString = "OPR: "+displayFormat.format(opr.get(i).getValue().getAsDouble())
                        +", DPR: "+displayFormat.format(dpr.get(i).getValue().getAsDouble())
                        +", CCWM: "+displayFormat.format(ccwm.get(i).getValue().getAsDouble());
                String teamKey = "frc"+opr.get(i).getKey();
                teamKeys.add(teamKey);
                teams.add(new StatsListElement(teamKey, Integer.parseInt(opr.get(i).getKey()), "", "", statsString));
                //TODO the blank fields above are team name and location
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        adapter = new ListViewAdapter(activity, teams, teamKeys);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (view != null) {
            ListView rankings = (ListView) view.findViewById(R.id.event_ranking);
            rankings.setAdapter(adapter);
        }
    }

}
