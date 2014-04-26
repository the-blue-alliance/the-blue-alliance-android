package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.datatypes.MatchGroup;
import com.thebluealliance.androidclient.models.Match;

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

        //more static data!
        //match constructor:
        //public Match(String matchKey, MATCH_TYPES matchType, int matchNumber, int setNumber, int blue1, int blue2, int blue3, int red1, int red2, int red3, int blueScore, int redScore) {
        SparseArray<MatchGroup> groups = new SparseArray<>();
        MatchGroup qualMatches = new MatchGroup("Qualification Matches");
        qualMatches.children.add(new Match("2014ctgro_qm1", Match.MATCH_TYPES.QUAL, 1, 1, 181, 5044, 237, 2182, 3634, 2168, 120, 23));
        qualMatches.children_keys.add("2014ctgro_qm1");
        qualMatches.children.add(new Match("2014ctgro_qm2", Match.MATCH_TYPES.QUAL, 2, 1, 175, 4557, 125, 3718, 230, 5112, 121, 60));
        qualMatches.children_keys.add("2014ctgro_qm2");
        qualMatches.children.add(new Match("2014ctgro_qm3", Match.MATCH_TYPES.QUAL, 3, 1, 195, 1991, 228, 3654, 5142, 1699, 82, 48));
        qualMatches.children_keys.add("2014ctgro_qm3");
        qualMatches.children.add(new Match("2014ctgro_qm4", Match.MATCH_TYPES.QUAL, 4, 1, 236, 571, 1099, 558, 2064, 3555, 136, 77));
        qualMatches.children_keys.add("2014ctgro_qm4");

        MatchGroup finals = new MatchGroup("Finals");
        finals.children.add(new Match("2014ctgro_f1m1", Match.MATCH_TYPES.FINAL, 1, 1, 1991, 230, 1699, 236, 237, 2064, 113, 120));
        finals.children_keys.add("2014ctgro_f1m1");
        finals.children.add(new Match("2014ctgro_f1m2", Match.MATCH_TYPES.FINAL, 2, 1, 1699, 1991, 230, 237, 236, 2064, 107, 105));
        finals.children_keys.add("2014ctgro_f1m2");
        finals.children.add(new Match("2014ctgro_f1m3", Match.MATCH_TYPES.FINAL, 3, 1, 1991, 230, 1699, 236, 237, 2064, 165, 76));
        finals.children_keys.add("2014ctgro_f1m3");

        groups.append(0, qualMatches);
        groups.append(1, finals);
        adapter = new MatchListAdapter(activity, groups);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.match_results);
        listView.setAdapter(adapter);
    }
}
