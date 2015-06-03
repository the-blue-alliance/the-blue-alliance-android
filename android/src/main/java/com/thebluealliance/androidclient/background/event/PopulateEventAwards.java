package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Retrieves awards data for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p>
 *         File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String, Void, APIResponse.CODE> {

    private EventAwardsFragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey, teamKey;
    private ArrayList<ListItem> awards;
    private RequestParams requestParams;
    private long startTime;

    public PopulateEventAwards(EventAwardsFragment f, RequestParams requestParams) {
        mFragment = f;
        if (mFragment != null) {
            activity = (RefreshableHostActivity) mFragment.getActivity();
        }
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];
        if (params.length >= 2) {
            teamKey = params[1];
        } else {
            teamKey = "";
        }

        awards = new ArrayList<>();

        APIResponse<ArrayList<Award>> response;
        try {
            response = DataManager.Events.getEventAwards(activity, eventKey, teamKey, requestParams);
            ArrayList<Award> awardList = response.getData();
            HashMap<String, Team> teams = new HashMap<>();
            for (Award a : awardList) {
                try {
                    for (JsonElement winner : a.getWinners()) {
                        if (!((JsonObject) winner).get("team_number").isJsonNull()) {
                            String teamKey = "frc" + ((JsonObject) winner).get("team_number");
                            Team team = DataManager.Teams.getTeamFromDB(activity, teamKey);
                            teams.put(teamKey, team);
                        }
                    }
                    awards.add(new CardedAwardListElement(a.getName(), eventKey, a.getWinners(), teams, teamKey));
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.w(Constants.LOG_TAG, "Unable to render awards. Missing stuff");
                }
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            Log.w(Constants.LOG_TAG, "unable to load event awards");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            ListViewAdapter adapter = new ListViewAdapter(activity, awards);
            NoDataView noData = (NoDataView) view.findViewById(R.id.no_data);

            // If there's no awards in the adapter or if we can't download info
            // off the web, display a message.
            ListView rankings = (ListView) view.findViewById(R.id.list);
            if (code == APIResponse.CODE.NODATA || (!requestParams.forceFromCache && awards.isEmpty())) {
                noData.setText(teamKey.isEmpty() ? R.string.no_awards_data : R.string.no_team_awards_data);
                noData.setVisibility(View.VISIBLE);
                view.findViewById(R.id.list).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.list).setVisibility(View.VISIBLE);
                Parcelable state = rankings.onSaveInstanceState();
                rankings.setAdapter(adapter);
                rankings.onRestoreInstanceState(state);
                noData.setVisibility(View.GONE);
            }

            // Display warning message if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateEventAwards secondLoad = new PopulateEventAwards(mFragment, requestParams);
                mFragment.updateTask(secondLoad);
                secondLoad.execute(eventKey, teamKey);
            } else {
                // Show notification if we've refreshed data.
                if (activity != null && mFragment instanceof RefreshListener) {
                    Log.i(Constants.REFRESH_LOG, "Event " + eventKey + " Awards refresh complete");
                    activity.notifyRefreshComplete(mFragment);
                }
            }

            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "event awards", eventKey);
        }
    }
}
