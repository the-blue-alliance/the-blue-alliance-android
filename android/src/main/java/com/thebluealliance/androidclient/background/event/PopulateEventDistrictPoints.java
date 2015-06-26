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
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.PointBreakdownComparater;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * File created by phil on 7/26/14.
 */
public class PopulateEventDistrictPoints extends AsyncTask<String, Void, APIResponse.CODE> {

    private EventDistrictPointsFragment mFragment;
    private LegacyRefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;
    private boolean isDistrict;
    private RequestParams requestParams;
    private long startTime;

    public PopulateEventDistrictPoints(EventDistrictPointsFragment f, RequestParams requestParams) {
        mFragment = f;
        activity = (LegacyRefreshableHostActivity) mFragment.getActivity();
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

        teams = new ArrayList<>();

        try {
            APIResponse<JsonObject> response = DataManager.Events.getDistrictPointsForEvent(activity, eventKey, requestParams);
            APIResponse<Event> eventResponse = DataManager.Events.getEventBasic(activity, eventKey, requestParams);
            JsonObject points = response.getData();
            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            String districtKey = null;
            try {
                DistrictHelper.DISTRICTS type = DistrictHelper.DISTRICTS.fromEnum(eventResponse.getData().getDistrictEnum());
                isDistrict = type != DistrictHelper.DISTRICTS.NO_DISTRICT;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFragment.updateDistrict(isDistrict);
                    }
                });
                if (isDistrict) {
                    districtKey = eventKey.substring(0, 4) + type.getAbbreviation();
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                isDistrict = false;
                Log.w(Constants.LOG_TAG, "Unable to determine if event is a district");
            }

            teams = new ArrayList<>();
            ArrayList<DistrictPointBreakdown> pointBreakdowns = new ArrayList<>();
            for (Map.Entry<String, JsonElement> teamPoints : points.entrySet()) {
                Team team = DataManager.Teams.getTeamFromDB(activity, teamPoints.getKey());
                DistrictPointBreakdown b = JSONHelper.getGson().fromJson(teamPoints.getValue(), DistrictPointBreakdown.class);
                b.setTeamKey(teamPoints.getKey());
                b.setTeamName(team != null ? team.getNickname() : "Team " + teamPoints.getKey().substring(3));
                b.setDistrictKey(districtKey);
                pointBreakdowns.add(b);
            }

            Collections.sort(pointBreakdowns, new PointBreakdownComparater());

            for (int i = 0; i < pointBreakdowns.size(); i++) {
                pointBreakdowns.get(i).setRank(i + 1);
                teams.add(pointBreakdowns.get(i).render());
            }


            return APIResponse.mergeCodes(response.getCode(), eventResponse.getCode());
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event points");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no rankings in the adapter or if we can't download info
            // off the web, display a message.
            ListViewAdapter adapter = new ListViewAdapter(activity, teams);
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_district_points);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView rankings = (ListView) view.findViewById(R.id.list);
                Parcelable state = rankings.onSaveInstanceState();
                rankings.setAdapter(adapter);
                rankings.onRestoreInstanceState(state);
                noDataText.setVisibility(View.GONE);
            }

            // Update district status
            mFragment.updateDistrict(isDistrict);

            // Display a warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress indicator and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
            } else {
                // Show notification if we've refreshed data.
                if (activity != null && mFragment instanceof RefreshListener) {
                    Log.i(Constants.REFRESH_LOG, "Event " + eventKey + " Rankings refresh complete");
                }
            }
            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "event districtPoints", eventKey);
        }
    }
}
