package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.comparators.TeamSortByOPRComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.MatchListElement;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<String, String, APIResponse.CODE> {

    private Fragment mFragment;
    private BaseActivity activity;
    View last, next;
    LinearLayout nextLayout, lastLayout, topTeams, topOpr;
    TextView eventName, eventDate, eventLoc, ranks, stats;
    String eventKey;
    Event event;
    private boolean showLastMatch, showNextMatch, showRanks, showStats;

    public PopulateEventInfo(Fragment f) {
        mFragment = f;
        activity = (BaseActivity) mFragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showLastMatch = showNextMatch = showRanks = showStats = false;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        View view = mFragment.getView();
        if (view != null && mFragment.getActivity() != null) {
            eventName = (TextView) view.findViewById(R.id.event_name);
            eventDate = (TextView) view.findViewById(R.id.event_date);
            eventLoc = (TextView) view.findViewById(R.id.event_location);
            nextLayout = (LinearLayout) view.findViewById(R.id.event_next_match_container);
            lastLayout = (LinearLayout) view.findViewById(R.id.event_last_match_container);
            topTeams = (LinearLayout) view.findViewById(R.id.event_top_teams_container);
            topOpr = (LinearLayout) view.findViewById(R.id.top_opr_container);

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                APIResponse<Event> response = DataManager.getEvent(activity, eventKey);
                event = response.getData();
                last = new MatchListElement(true, "Quals 1", new String[]{"3182", "3634", "2168"}, new String[]{"181", "4055", "237"}, 23, 120, "2014ctgro_qm1").getView(activity, inflater, null);
                next = new MatchListElement(true, "Quals 2", new String[]{"3718", "230", "5112"}, new String[]{"175", "4557", "125"}, 60, 121, "2014ctgro_qm2").getView(activity, inflater, null);
                //return response.getCode();
            } catch (DataManager.NoDataException e) {
                Log.w(Constants.LOG_TAG, "unable to load event info");
                return APIResponse.CODE.NODATA;
            }

            if(event.hasStarted()){
                //event has started (may or may not have finished).
                //show the ranks and stats
                showRanks = showStats = true;
                ranks = new TextView(activity);
                try {
                    APIResponse<ArrayList<JsonArray>> rankResponse = DataManager.getEventRankings(activity, eventKey);
                    ArrayList<JsonArray> rankList = rankResponse.getData();
                    String rankString = "";
                    for(int i=1;i<Math.min(6, rankList.size()); i++){
                        rankString += ((i)+". "+rankList.get(i).get(1).getAsString())+"\n";
                    }
                    ranks.setText(rankString);
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "Unable to load event rankings");
                    return APIResponse.CODE.NODATA;
                }

                stats = new TextView(activity);
                try {
                    APIResponse<JsonObject> statsResponse = DataManager.getEventStats(activity, eventKey);
                    ArrayList<Map.Entry<String,JsonElement>> opr = new ArrayList<>();
                    opr.addAll(statsResponse.getData().get("oprs").getAsJsonObject().entrySet());
                    Collections.sort(opr, new TeamSortByOPRComparator());
                    String statsString = "";
                    for(int i=0;i<Math.min(5, opr.size()); i++){
                        statsString += ((i+1)+". "+opr.get(i).getKey()+"\n");
                    }
                    stats.setText(statsString);
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to load event stats");
                    return APIResponse.CODE.NODATA;
                }
            }

        /* TODO finish basic event bits as the rest of the API queries get implemented
         * this includes next/last match, if event is currently active
         * Top teams in rankings
         * Top teams in stats
         *
         */
        }
        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);
        if (event != null && mFragment.getActivity() != null) {
            eventName.setText(event.getEventName());
            eventDate.setText(event.getDateString());
            eventLoc.setText(event.getLocation());
            nextLayout.addView(next);
            lastLayout.addView(last);
            if(showRanks) {
                topTeams.setVisibility(View.VISIBLE);
                topTeams.addView(ranks);
            }

            if(showStats) {
                topOpr.setVisibility(View.VISIBLE);
                topOpr.addView(stats);
            }

            if (c == APIResponse.CODE.OFFLINECACHE /* && event is current */) {
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            if (mFragment.getView() != null) {
                mFragment.getView().findViewById(R.id.progress).setVisibility(View.GONE);
                mFragment.getView().findViewById(R.id.event_info_container).setVisibility(View.VISIBLE);
            }
        }
    }
}
