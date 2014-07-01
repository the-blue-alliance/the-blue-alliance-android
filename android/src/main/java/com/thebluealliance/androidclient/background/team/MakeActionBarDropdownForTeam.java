package com.thebluealliance.androidclient.background.team;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * File created by phil on 6/18/14.
 */
public class MakeActionBarDropdownForTeam extends AsyncTask<String, Void, APIResponse.CODE> {

    private ViewTeamActivity activity;
    private String teamKey;

    private static HashMap<String, String[]> yearsByTeam;

    public MakeActionBarDropdownForTeam(Activity activity) {
        if (activity instanceof ViewTeamActivity) {
            this.activity = ((ViewTeamActivity) activity);
        } else {
            throw new IllegalArgumentException("You must pass an instance of ViewTeamActivity here");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        if (params.length < 1 || !TeamHelper.validateTeamKey(params[0])) {
            throw new IllegalArgumentException("You must pass a valid team key to create the action bar");
        }
        teamKey = params[0];

        if(!yearsByTeam.containsKey(teamKey)) {
            try {
                APIResponse<Team> yearsResponse = DataManager.Teams.getYearsParticipated(activity, teamKey, false);
                JsonArray yearList = yearsResponse.getData().getYearsParticipated();
                ArrayList<String> years = new ArrayList<>();
                for (JsonElement e : yearList) {
                    years.add(e.getAsString());
                }
                Collections.reverse(years);
                String[] yearsArray = years.toArray(new String[years.size()]);
                yearsByTeam.put(teamKey, yearsArray);
                return yearsResponse.getCode();
            } catch (DataManager.NoDataException e) {
                Log.w(Constants.LOG_TAG, "Unable to fetch years participated for " + teamKey);
                return APIResponse.CODE.NODATA;
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Team doesn't contain years participated.");
                return APIResponse.CODE.NODATA;
            }
        }
        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if(activity != null && yearsByTeam.get(teamKey).length > 0) {
            ActionBar bar = activity.getActionBar();
            ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(bar.getThemedContext(), R.layout.actionbar_spinner_team, R.id.year, yearsByTeam.get(teamKey));
            actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
            String teamNumber = teamKey.replace("frc", "");
            activity.setActionBarTitle(String.format(activity.getString(R.string.team_actionbar_title), teamNumber) + " - ");
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            bar.setListNavigationCallbacks(actionBarAdapter, activity);
            bar.setSelectedNavigationItem(activity.getCurrentSelectedYearPosition());
        }
    }
}
