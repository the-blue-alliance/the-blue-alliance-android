package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.MatchGroup;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventResults extends AsyncTask<String, Void, Void> {

    private Activity activity;
    private View view;
    private String eventKey, teamKey;
    private MatchListAdapter adapter;

    public PopulateEventResults(Activity activity, View view) {
        this.activity = activity;
        this.view = view;

    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];
        if (params.length == 2) {
            teamKey = params[1];
        } else {
            teamKey = "";
        }


        SparseArray<MatchGroup> groups = new SparseArray<>();
        MatchGroup qualMatches = new MatchGroup("Qualification Matches");
        MatchGroup quarterMatches = new MatchGroup("Quarterfinal Matches");
        MatchGroup semiMatches = new MatchGroup("Semifinal Matches");
        MatchGroup finalMatches = new MatchGroup("Finals Matches");
        try {
            HashMap<Match.TYPE,ArrayList<Match>> results = DataManager.getEventResults(activity,eventKey);
            //TODO proper sorting of matches
            for(Match m:results.get(Match.TYPE.QUAL)){
                qualMatches.children.add(m);
                qualMatches.childrenKeys.add(m.getKey());
            }
            for(Match m:results.get(Match.TYPE.QUARTER)){
                quarterMatches.children.add(m);
                quarterMatches.childrenKeys.add(m.getKey());
            }
            for(Match m:results.get(Match.TYPE.SEMI)){
                semiMatches.children.add(m);
                semiMatches.childrenKeys.add(m.getKey());
            }
            for(Match m:results.get(Match.TYPE.FINAL)){
                finalMatches.children.add(m);
                finalMatches.childrenKeys.add(m.getKey());
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        int numGroups = 0;
        if(qualMatches.children.size()>0){
            groups.append(numGroups, qualMatches);
            numGroups++;
        }
        if(quarterMatches.children.size()>0){
            groups.append(numGroups, quarterMatches);
            numGroups++;
        }
        if(semiMatches.children.size()>0){
            groups.append(numGroups, semiMatches);
            numGroups++;
        }
        if(finalMatches.children.size()>0){
            groups.append(numGroups, finalMatches);
            numGroups++;
        }
        adapter = new MatchListAdapter(activity, groups);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.match_results);
        listView.setAdapter(adapter);
    }
}
