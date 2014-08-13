package com.thebluealliance.androidclient.background.district;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictSummaryFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.LabelValueDetailListItem;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;

/**
 * File created by phil on 7/26/14.
 */
public class PopulateTeamAtDistrictSummary extends AsyncTask<String, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private TeamAtDistrictSummaryFragment fragment;
    private RefreshableHostActivity activity;
    private String teamKey, districtKey;
    private ArrayList<ListItem> summaryItems;

    public PopulateTeamAtDistrictSummary(TeamAtDistrictSummaryFragment fragment, boolean forceFromCache) {
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

        try {
            APIResponse<DistrictTeam> response = DataManager.Districts.getDistrictTeam(activity, districtTeamKey, forceFromCache);
            summaryItems = new ArrayList<>();
            DistrictTeam team = response.getData();

            try {
                summaryItems.add(new LabelValueListItem(activity.getString(R.string.district_point_rank),
                        team.getRank() + Utilities.getOrdinalFor(team.getRank())));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to get DistrictTeam rank");
            }

            try {
                APIResponse<Event> event1Name = DataManager.Events.getEventBasic(activity, team.getEvent1Key(), forceFromCache);
                summaryItems.add(new LabelValueDetailListItem(event1Name.getData().getEventName(),
                        String.format(activity.getString(R.string.district_points_format), team.getEvent1Points()),
                        EventTeamHelper.generateKey(team.getEvent1Key(), team.getTeamKey())));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to get Event 1 details");
            }

            try {
                APIResponse<Event> event2Name = DataManager.Events.getEventBasic(activity, team.getEvent2Key(), forceFromCache);
                summaryItems.add(new LabelValueDetailListItem(event2Name.getData().getEventName(),
                        String.format(activity.getString(R.string.district_points_format), team.getEvent2Points()),
                        EventTeamHelper.generateKey(team.getEvent2Key(), team.getTeamKey())));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to get Event 2 details");
            }

            try {
                APIResponse<Event> cmpName = DataManager.Events.getEventBasic(activity, team.getCmpKey(), forceFromCache);
                summaryItems.add(new LabelValueDetailListItem(cmpName.getData().getEventName(),
                        String.format(activity.getString(R.string.district_points_format), team.getCmpPoints()),
                        EventTeamHelper.generateKey(team.getCmpKey(), team.getTeamKey())));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to get CMP details");
            }

            try {
                summaryItems.add(new LabelValueListItem(activity.getString(R.string.total_district_points),
                        String.format(activity.getString(R.string.district_points_format), team.getTotalPoints())));
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Unable to get DistrictTeam rank");
            }

            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.e(Constants.LOG_TAG, "Unable to fetch DistrictTeam " + districtTeamKey);
            e.printStackTrace();
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        View view = fragment.getView();
        if (view != null && activity != null) {
            if (activity.getActionBar() != null) {
                activity.setActionBarTitle(String.format(activity.getString(R.string.team_actionbar_title), teamKey.substring(3)));
                activity.setActionBarSubtitle("@ " + districtKey.substring(0, 4) + " " + districtKey.substring(4).toUpperCase());
            }
            ListViewAdapter adapter = new ListViewAdapter(activity, summaryItems);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no data in the adapter or if we can't download info
            // off the web, display a message.
            if ((code == APIResponse.CODE.NODATA && !ConnectionDetector.isConnectedToInternet(activity)) || (!forceFromCache && adapter.values.isEmpty())) {
                noDataText.setText(R.string.no_district_summary);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView eventList = (ListView) view.findViewById(R.id.list);
                Parcelable state = eventList.onSaveInstanceState();
                eventList.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                eventList.onRestoreInstanceState(state);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(fragment.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                PopulateTeamAtDistrictSummary second = new PopulateTeamAtDistrictSummary(fragment, false);
                fragment.updateTask(second);
                second.execute(teamKey, districtKey);
            } else {
                // Show notification if we've refreshed data.
                Log.d(Constants.REFRESH_LOG, "Team@District summary refresh complete");
                activity.notifyRefreshComplete((RefreshListener) fragment);
            }

        }
    }
}
