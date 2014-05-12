package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.StatsListElement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventStats extends AsyncTask<String, Void, APIResponse.CODE> implements AdapterView.OnItemClickListener {

    private Fragment mFragment;
    private BaseActivity activity;
    private String eventKey;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventStats(Fragment f) {
        mFragment = f;
        activity = (BaseActivity)mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teamKeys = new ArrayList<>();
        teams = new ArrayList<>();

        DecimalFormat displayFormat = new DecimalFormat("#.##");

        try {
            APIResponse<JsonObject> response = DataManager.getEventStats(activity, eventKey);
            JsonObject stats = response.getData();
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
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && mFragment != null) {
            adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
            ListView stats = (ListView) view.findViewById(R.id.event_ranking);
            stats.setAdapter(adapter);
            stats.setOnItemClickListener(this);

            if(code == APIResponse.CODE.OFFLINECACHE /* && event is current */){
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFragment.startActivity(ViewTeamActivity.newInstance(mFragment.getActivity(), view.getTag().toString()));
    }
}
