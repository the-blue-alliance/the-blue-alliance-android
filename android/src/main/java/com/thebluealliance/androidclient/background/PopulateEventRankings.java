package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.RankingListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventRankings extends AsyncTask<String, Void, Void> implements AdapterView.OnItemClickListener {

    private Fragment mFragment;
    private String eventKey;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventRankings(Fragment f) {
        mFragment = f;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        teamKeys = new ArrayList<>();
        teams = new ArrayList<>();

        try {
            ArrayList<JsonArray> rankList = DataManager.getEventRankings(mFragment.getActivity(), eventKey);
            JsonArray headerRow = rankList.remove(0);
            for(JsonArray row:rankList){
                /* Assume that the list of lists has rank first
                 * and team # second, always
                 */
                String teamKey = "frc"+row.get(1).getAsString();
                teamKeys.add(teamKey);
                String rankingString = "";
                for(int i=2;i<row.size();i++){
                    rankingString += headerRow.get(i).getAsString()+": "+row.get(i).getAsString();
                    if(i+1<row.size()){
                        rankingString += ", ";
                    }
                }
                teams.add(new RankingListElement(teamKey,row.get(1).getAsInt(),"",row.get(0).getAsInt(),"",rankingString));
                //the two columns set to "" above are 'team name' and 'record' as those are not consistently in the data
                //TODO get team name for given number
                //TODO remove record from layout (since it's not a constant parameter)
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        View view = mFragment.getView();
        if (view != null) {
            ListView rankings = (ListView) view.findViewById(R.id.event_ranking);
            rankings.setAdapter(adapter);
            rankings.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFragment.startActivity(ViewTeamActivity.newInstance(mFragment.getActivity(), view.getTag().toString()));
    }
}
