package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;

/**
 * File created by phil on 6/3/14.
 */
public class PopulateTeamAtEvent extends AsyncTask<String, Void, APIResponse.CODE> {

    String teamKey, eventKey, recordString, eventShort;
    RefreshableHostActivity activity;
    ExpandableListAdapter adapter;
    int rank;
    ListGroup awards, stats, recentMatches;
    Event event;
    Match lastMatch, nextMatch;
    boolean activeEvent;

    public PopulateTeamAtEvent(RefreshableHostActivity activity, ExpandableListAdapter adapter, Event event){
        super();
        this.activity = activity;
        this.adapter = adapter;
        this.event = event;
    }

    public void setNextMatch(Match nextMatch) {
        this.nextMatch = nextMatch;
    }

    public void setLastMatch(Match lastMatch) {
        this.lastMatch = lastMatch;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {

        if(params.length != 3) throw new IllegalArgumentException("PopulateTeamAtEvent must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];
        recordString = params[2];

        if(event != null){
            eventShort = event.getShortName();
            activeEvent = event.isHappeningNow();
        }else{
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
            awards = new ListGroup(activity.getString(R.string.tab_event_awards));
            awards.children.addAll(awardList);
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch award data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<JsonObject> statsResponse;
        try {
            statsResponse = DataManager.getEventStats(activity, eventKey, teamKey);
            JsonObject statData = statsResponse.getData();
            String statString = "";
            if(statData.has("opr")){
                statString += activity.getString(R.string.opr)+" "+Stat.displayFormat.format(statData.get("opr").getAsDouble());
            }
            if(statData.has("dpr")){
                statString += "\n"+activity.getString(R.string.dpr)+" "+Stat.displayFormat.format(statData.get("dpr").getAsDouble());
            }
            if(statData.has("ccwm")){
                statString += "\n"+activity.getString(R.string.ccwm)+" "+Stat.displayFormat.format(statData.get("ccwm").getAsDouble());
            }
            stats = new ListGroup(activity.getString(R.string.tab_event_stats));
            if(!statString.isEmpty()){
                stats.children.add(new Stat(teamKey, "", "", statString));
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch stats data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        return APIResponse.mergeCodes(rankResponse.getCode(), awardResponse.getCode(), statsResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if(activity != null && code != APIResponse.CODE.NODATA) {
            boolean listViewUpdated=false;
            if (activity.getActionBar() != null && eventShort != null && !eventShort.isEmpty()) {
                activity.getActionBar().setTitle(teamKey.substring(3) + " @ " + eventShort);
            }
            //set the other UI elements specific to team@event
            ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(
                    String.format(activity.getString(R.string.team_record),
                            teamKey.substring(3), rank, recordString)
            ));

           if(stats.children.size() > 0){
                adapter.addGroup(0, stats);
                listViewUpdated = true;
            }

            if(awards.children.size() > 0){
                adapter.addGroup(0, awards);
                listViewUpdated = true;
            }

            if(activeEvent && nextMatch != null){
                ListGroup nextMatches = new ListGroup(activity.getString(R.string.title_next_match));
                nextMatches.children.add(nextMatch);
                adapter.addGroup(0, nextMatches);
                listViewUpdated = true;
            }
            if(activeEvent && lastMatch != null){
                ListGroup lastMatches = new ListGroup(activity.getString(R.string.title_last_match));
                lastMatches.children.add(lastMatch);
                adapter.addGroup(0, lastMatches);
                listViewUpdated = true;
            }

            activity.findViewById(R.id.team_at_event_progress).setVisibility(View.GONE);
            activity.findViewById(R.id.content_view).setVisibility(View.VISIBLE);

            if(listViewUpdated){
                adapter.notifyDataSetChanged();
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }
}
