package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.RankingListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventRankings extends AsyncTask<String, Void, APIResponse.CODE> implements AdapterView.OnItemClickListener {

    private Fragment mFragment;
    private BaseActivity activity;
    private String eventKey;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventRankings(Fragment f) {
        mFragment = f;
        activity = (BaseActivity)mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teamKeys = new ArrayList<>();
        teams = new ArrayList<>();

        try {
            APIResponse<ArrayList<JsonArray>> response = DataManager.getEventRankings(activity,eventKey);
            ArrayList<JsonArray> rankList = response.getData();
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
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && mFragment.getActivity() != null) {
            adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
            ListView rankings = (ListView) view.findViewById(R.id.event_ranking);
            adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
            rankings.setAdapter(adapter);
            rankings.setOnItemClickListener(this);

            if(code == APIResponse.CODE.OFFLINECACHE /* && event is current */){
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFragment.startActivity(ViewTeamActivity.newInstance(activity, view.getTag().toString()));
    }
}
