package com.thebluealliance.androidclient.background.district;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictBreakdownFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.ArrayList;

/**
 * File created by phil on 7/26/14.
 */
public class PopulateTeamAtDistrictBreakdown extends AsyncTask<String, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private TeamAtDistrictBreakdownFragment fragment;
    private RefreshableHostActivity activity;
    private String teamKey, districtKey;
    private ArrayList<ListGroup> groups;

    public PopulateTeamAtDistrictBreakdown(TeamAtDistrictBreakdownFragment fragment, boolean forceFromCache) {
        super();
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
        if (params.length != 2) {
            throw new IllegalArgumentException("PopulateTeamAtDistrictSummary must be constructed with team key & district key");
        }
        teamKey = params[0];
        districtKey = params[1];

        if (!TeamHelper.validateTeamKey(teamKey)) {
            throw new IllegalArgumentException("Invalid team key");
        }
        if (!DistrictHelper.validateDistrictKey(districtKey)) {
            throw new IllegalArgumentException("Invalid district key");
        }

        String districtTeamKey = DistrictTeamHelper.generateKey(teamKey, districtKey);

        APIResponse<DistrictTeam> teamEvents;
        try {
            teamEvents = DataManager.Districts.getDistrictTeamEvents(activity, districtTeamKey, forceFromCache);
        } catch (DataManager.NoDataException e) {
            Log.e(Constants.LOG_TAG, "Unable to fetch events for " + teamKey + " in " + districtKey);
            return APIResponse.CODE.NODATA;
        }

        ArrayList<APIResponse.CODE> pointsCodes = new ArrayList<>();
        pointsCodes.add(teamEvents.getCode());

        ArrayList<String> eventKeys = new ArrayList<>();
        try {
            eventKeys.add(teamEvents.getData().getEvent1Key());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to find event 1 key");
        }
        try {
            eventKeys.add(teamEvents.getData().getEvent2Key());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to find event 2 key");
        }
        try {
            eventKeys.add(teamEvents.getData().getCmpKey());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to find cmp key");
        }

        groups = new ArrayList<>();
        for (String eventKey : eventKeys) {
            try {
                APIResponse<JsonObject> teamPoints = DataManager.Events.getDistrictPointsForEvent(activity, eventKey, teamKey, forceFromCache);
                pointsCodes.add(teamPoints.getCode());
                ListGroup eventGroup = new ListGroup(DataManager.Events.getEventBasic(activity, eventKey, forceFromCache).getData().getEventName());

                DistrictPointBreakdown breakdown = JSONManager.getGson().fromJson(teamPoints.getData(), DistrictPointBreakdown.class);

                if (breakdown.getQualPoints() > -1) {
                    eventGroup.children.add(breakdown.renderQualPoints(activity));
                }
                if (breakdown.getElimPoints() > -1) {
                    eventGroup.children.add(breakdown.renderElimPoints(activity));
                }
                if (breakdown.getAlliancePoints() > -1) {
                    eventGroup.children.add(breakdown.renderAlliancePoints(activity));
                }
                if (breakdown.getAwardPoints() > -1) {
                    eventGroup.children.add(breakdown.renderAwardPoints(activity));
                }
                if (breakdown.getTotalPoints() > -1) {
                    eventGroup.children.add(breakdown.renderTotalPoints(activity));
                }

                groups.add(eventGroup);
            } catch (DataManager.NoDataException e) {
                Log.e(Constants.LOG_TAG, "Unable to get district points for " + eventKey + " " + teamKey);
                e.printStackTrace();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Unable to fetch event title");
                e.printStackTrace();
            }
        }


        return APIResponse.mergeCodes(pointsCodes);
    }

    protected void onPostExecute(APIResponse.CODE code) {
        View view = fragment.getView();

        ExpandableListAdapter adapter = new ExpandableListAdapter(activity, groups);

        if (view != null && activity != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no results in the adapter or if we can't download info
            // off the web, display a message.
            // only show the message when try try and actually load data from the web
            if (code == APIResponse.CODE.NODATA || (!forceFromCache && groups == null || adapter.groups == null || adapter.groups.isEmpty())) {
                noDataText.setVisibility(View.VISIBLE);
                noDataText.setText(R.string.no_team_district_breakdown);
            } else {
                noDataText.setVisibility(View.GONE);
                ExpandableListView results = (ExpandableListView) view.findViewById(R.id.expandable_list);
                Parcelable state = results.onSaveInstanceState();
                int firstVisiblePosition = results.getFirstVisiblePosition();
                results.setAdapter(adapter);
                results.onRestoreInstanceState(state);
                results.setSelection(firstVisiblePosition);
                if (groups.size() == 1) {
                    results.expandGroup(0);
                }
                adapter.notifyDataSetChanged();
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);

            // Display warning message if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }

        if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            PopulateTeamAtDistrictBreakdown secondLoad = new PopulateTeamAtDistrictBreakdown(fragment, false);
            fragment.updateTask(secondLoad);
            secondLoad.execute(teamKey, districtKey);
        } else {
            // Show notification if we've refreshed data.
            if (fragment instanceof RefreshListener) {
                Log.d(Constants.REFRESH_LOG, teamKey + " at " + districtKey + " refresh complete");
                activity.notifyRefreshComplete(fragment);
            }
        }
    }
}
