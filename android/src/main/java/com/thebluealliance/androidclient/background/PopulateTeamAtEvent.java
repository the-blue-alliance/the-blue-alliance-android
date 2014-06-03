package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;

/**
 * File created by phil on 6/3/14.
 */
public class PopulateTeamAtEvent extends AsyncTask<String, Void, APIResponse.CODE> {

    String teamKey, eventKey, recordString, eventShort;
    RefreshableHostActivity activity;
    ExpandableListAdapter adapter;
    int rank;
    boolean listViewUpdated;

    public PopulateTeamAtEvent(RefreshableHostActivity activity, ExpandableListAdapter adapter){
        super();
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {

        if(params.length != 3) throw new IllegalArgumentException("PopulateTeamAtEvent must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];
        recordString = params[2];

        listViewUpdated = false;

        APIResponse<Event> eventResponse;
        Event event;
        try {
            eventResponse = DataManager.getEvent(activity, eventKey);
            event = eventResponse.getData();
            eventShort = event.getShortName();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch event data for "+teamKey+"@"+eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<Integer> rankResponse;
        try {
            rankResponse = DataManager.getRankForTeamAtEvent(activity, teamKey, eventKey);
            rank = rankResponse.getData();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch ranking data for "+teamKey+"@"+eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<ArrayList<Award>> awardResponse;
        try {
            awardResponse = DataManager.getEventAwards(activity, eventKey, teamKey);
            ArrayList< Award > awardList = awardResponse.getData();
            ListGroup awards = new ListGroup(activity.getString(R.string.awards_header));
            awards.children.addAll(awardList);
            if(awardList.size() > 0){
                adapter.addGroup(0, awards);
                listViewUpdated = true;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch award data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }


        return APIResponse.mergeCodes(eventResponse.getCode(), rankResponse.getCode(), awardResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if(activity != null) {
            if (eventShort != null && !eventShort.isEmpty()) {
                activity.getActionBar().setTitle(teamKey.substring(3) + " @ " + eventShort);
            }
            //set the other UI elements specific to team@event
            ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(
                    String.format(activity.getString(R.string.team_record),
                            teamKey.substring(3), rank, recordString)
            ));

            activity.findViewById(R.id.team_at_event_info).setVisibility(View.VISIBLE);

            activity.findViewById(R.id.progress).setVisibility(View.GONE);

            if(listViewUpdated){
                adapter.notifyDataSetChanged();
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }
}
