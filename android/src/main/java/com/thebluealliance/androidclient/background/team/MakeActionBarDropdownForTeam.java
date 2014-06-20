package com.thebluealliance.androidclient.background.team;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 6/18/14.
 */
public class MakeActionBarDropdownForTeam extends AsyncTask<String, Void, APIResponse.CODE> {

    private ViewTeamActivity activity;
    private String teamKey;

    private int[] years;

    public MakeActionBarDropdownForTeam(Activity activity) {
        if (activity instanceof ViewTeamActivity) {
            this.activity = ((ViewTeamActivity) activity);
        } else {
            throw new IllegalArgumentException("You must pass an instance of ViewTeamActivity here");
        }
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        if (params.length < 1 || !TeamHelper.validateTeamKey(params[0])) {
            throw new IllegalArgumentException("You must pass a valid team key to create the action bar");
        }
        teamKey = params[0];

        try {
            APIResponse<ArrayList<Integer>> yearsResponse = DataManager.Teams.getYearsParticipated(activity, teamKey, false);
            Collections.reverse(yearsResponse.getData());
            Integer[] integerYears = yearsResponse.getData().toArray(new Integer[yearsResponse.getData().size()]);
            years = new int[integerYears.length];
            for(int i = 0; i < years.length; i++) {
                years[i] = integerYears[i].intValue();
            }
            return yearsResponse.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch years participated for " + teamKey);
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if (activity != null && years.length > 0) {
            activity.onYearsParticipatedLoaded(years);
        }
    }
}
