package com.thebluealliance.androidclient.background.district;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.district.DistrictRankingsFragment;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by phil on 7/24/14.
 */
public class PopulateDistrictRankings extends AsyncTask<String, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private DistrictRankingsFragment fragment;
    private RefreshableHostActivity activity;
    private String districtKey;
    private ArrayList<ListItem> rankings;

    public PopulateDistrictRankings(DistrictRankingsFragment fragment, boolean forceFromCache){
        this.forceFromCache = forceFromCache;
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        districtKey = params[0];

        APIResponse<ArrayList<DistrictTeam>> response;
        try {
            response = DataManager.Districts.getDistrictRankings(activity, districtKey, forceFromCache);
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to get district rankings for " + districtKey);
            return APIResponse.CODE.NODATA;
        }

        rankings = new ArrayList<>();
        for(DistrictTeam team: response.getData()){
            try {
                Team teamData = DataManager.Teams.getTeamFromDB(activity, team.getTeamKey());
                String nickname;
                if(teamData != null){
                    nickname = teamData.getNickname();
                }else{
                    Log.w(Constants.LOG_TAG, "Couldn't find "+team.getTeamKey()+" in db");
                    nickname = "Team "+team.getTeamKey().substring(3);
                }
                rankings.add(new DistrictTeamListElement(team.getTeamKey(), team.getDistrictKey(), nickname, team.getRank(), team.getTotalPoints()));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to render district rankings");
                e.printStackTrace();
            }
        }
        return response.getCode();
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        View view = fragment.getView();
        if (view != null && activity != null) {
            ListViewAdapter adapter = new ListViewAdapter(activity, rankings);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            ListView list = (ListView) view.findViewById(R.id.list);

            // If there's no data in the adapter or if we can't download info
            // off the web, display a message.
            Log.e(Constants.LOG_TAG, ""+rankings.isEmpty());
            if (rankings.isEmpty() || (code == APIResponse.CODE.NODATA && !ConnectionDetector.isConnectedToInternet(activity))) {
                Log.e(Constants.LOG_TAG, "hiding");
                noDataText.setText(R.string.no_district_rankings);
                noDataText.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
            } else {
                Parcelable state = list.onSaveInstanceState();
                list.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                list.onRestoreInstanceState(state);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(fragment.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                PopulateDistrictRankings second = new PopulateDistrictRankings(fragment, false);
                fragment.updateTask(second);
                second.execute(districtKey);
            } else {
                // Show notification if we've refreshed data.
                Log.d(Constants.REFRESH_LOG, "District rankings refresh complete");
                activity.notifyRefreshComplete(fragment);
            }

        }
    }
}
