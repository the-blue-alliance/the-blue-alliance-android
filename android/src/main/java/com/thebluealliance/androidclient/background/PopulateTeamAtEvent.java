package com.thebluealliance.androidclient.background;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * File created by phil on 6/3/14.
 */
public class PopulateTeamAtEvent extends AsyncTask<String, Void, APIResponse.CODE> {

    String teamKey, eventKey, recordString, eventShort;
    RefreshableHostActivity activity;
    ArrayList<Match> eventMatches;
    ArrayList<ListGroup> matchGroups;
    int rank;
    int allianceNumber = -1, alliancePick = -1;
    ListGroup awards, stats;
    Event event;
    Match lastMatch, nextMatch;
    boolean activeEvent, forceFromCache;

    public PopulateTeamAtEvent(RefreshableHostActivity activity, boolean forceFromCache) {
        super();
        this.activity = activity;
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {

        if (params.length != 2)
            throw new IllegalArgumentException("PopulateTeamAtEvent must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];

        APIResponse<ArrayList<Match>> matchResponse;
        try {
            matchResponse = DataManager.Teams.getMatchesForTeamAtEvent(activity, teamKey, eventKey, forceFromCache);

            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }

            ArrayList<Match> matches = matchResponse.getData(); //sorted by play order
            eventMatches = matchResponse.getData(); //sorted by play order
            try {
                matchGroups = MatchHelper.constructMatchList(activity, matches);
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't construct match list. Missing fields: "+e.getMessage());
                return APIResponse.CODE.NODATA;
            }
            int[] record = MatchHelper.getRecordForTeam(matches, teamKey);
            recordString = record[0] + "-" + record[1] + "-" + record[2];
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            matchResponse = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        APIResponse<Event> eventResponse;
        try {
            eventResponse = DataManager.Events.getEvent(activity, eventKey, forceFromCache);
            event = eventResponse.getData();
            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch event data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        if (event != null) {
            eventShort = event.getShortName();
            activeEvent = event.isHappeningNow();
            // Search for team in alliances
            JsonArray alliances = null;
            try {
                alliances = event.getAlliances();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't get event alliances");
                return APIResponse.CODE.NODATA;
            }
            for (int i = 0; i < alliances.size(); i++) {
                JsonArray teams = alliances.get(i).getAsJsonObject().get("picks").getAsJsonArray();
                for (int j = 0; j < teams.size(); j++) {
                    if (teams.get(j).getAsString().equals(teamKey)) {
                        allianceNumber = i + 1;
                        alliancePick = j;
                    }
                }
            }
        } else {
            return APIResponse.CODE.NODATA;
        }

        APIResponse<Integer> rankResponse;
        try {
            rankResponse = DataManager.Teams.getRankForTeamAtEvent(activity, teamKey, eventKey, forceFromCache);
            rank = rankResponse.getData();
            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch ranking data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<ArrayList<Award>> awardResponse;
        try {
            awardResponse = DataManager.Teams.getAwardsForTeamAtEvent(activity, teamKey, eventKey, forceFromCache);
            ArrayList<Award> awardList = awardResponse.getData();
            awards = new ListGroup(activity.getString(R.string.tab_event_awards));
            awards.children.addAll(awardList);
            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch award data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<JsonObject> statsResponse;
        try {
            statsResponse = DataManager.Events.getEventStats(activity, eventKey, teamKey, forceFromCache);
            JsonObject statData = statsResponse.getData();

            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }

            String statString = "";
            if (statData.has("opr")) {
                statString += activity.getString(R.string.opr) + " " + Stat.displayFormat.format(statData.get("opr").getAsDouble());
            }
            if (statData.has("dpr")) {
                statString += "\n" + activity.getString(R.string.dpr) + " " + Stat.displayFormat.format(statData.get("dpr").getAsDouble());
            }
            if (statData.has("ccwm")) {
                statString += "\n" + activity.getString(R.string.ccwm) + " " + Stat.displayFormat.format(statData.get("ccwm").getAsDouble());
            }
            stats = new ListGroup(activity.getString(R.string.tab_event_stats));
            if (!statString.isEmpty()) {
                stats.children.add(new Stat(teamKey, "", "", statString));
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch stats data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        return APIResponse.mergeCodes(matchResponse.getCode(), eventResponse.getCode(),
                rankResponse.getCode(), awardResponse.getCode(), statsResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if (activity != null && code != APIResponse.CODE.NODATA) {
            if (activity.getActionBar() != null && eventShort != null && !eventShort.isEmpty()) {
                activity.getActionBar().setTitle(teamKey.substring(3) + " @ " + eventShort);
            }

            MatchListAdapter adapter = new MatchListAdapter(activity, matchGroups, teamKey);

            MatchHelper.EventPerformance performance = null;
            try {
                performance = MatchHelper.evaluatePerformanceForTeam(event, eventMatches, teamKey);
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Can't create match performance. Missing fields: "+ Arrays.toString(e.getStackTrace()));
                performance = MatchHelper.EventPerformance.NOT_AVAILABLE;
            }
            String summary = generateTeamSummary(teamKey, rank,
                    recordString, allianceNumber, alliancePick, performance);
            ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(summary));

            if (!stats.children.isEmpty()) {
                adapter.addGroup(0, stats);
            }

            if (!awards.children.isEmpty()) {
                adapter.addGroup(0, awards);
            }

            if (activeEvent && nextMatch != null) {
                ListGroup nextMatches = new ListGroup(activity.getString(R.string.title_next_match));
                nextMatches.children.add(nextMatch);
                adapter.addGroup(0, nextMatches);
            }

            if (activeEvent && lastMatch != null) {
                ListGroup lastMatches = new ListGroup(activity.getString(R.string.title_last_match));
                lastMatches.children.add(lastMatch);
                adapter.addGroup(0, lastMatches);
            }

            ExpandableListView listView = (ExpandableListView) activity.findViewById(R.id.results);
            Parcelable state = listView.onSaveInstanceState();
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(state);
            listView.setSelection(firstVisiblePosition);

            activity.findViewById(R.id.team_at_event_progress).setVisibility(View.GONE);
            activity.findViewById(R.id.content_view).setVisibility(View.VISIBLE);

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
            new PopulateTeamAtEvent(activity, false).execute(teamKey, eventKey);
        } else {
            // Show notification if we've refreshed data.
            Log.i(Constants.REFRESH_LOG, teamKey+"@"+eventKey+" refresh complete");
            if (activity instanceof RefreshableHostActivity) {
                activity.notifyRefreshComplete((RefreshListener) activity);
            }
        }

    }

    private String generateTeamSummary(String teamKey, int rank,
                                       String record, int allianceNumber, int alliancePick,
                                       MatchHelper.EventPerformance performance) {
        String summary = "";
        List<Object> summaryArgs = new ArrayList<>();
        Resources r = activity.getResources();
        if (performance == MatchHelper.EventPerformance.NOT_AVAILABLE) {
            return r.getString(R.string.team_at_event_no_data);
        } else if (rank == -1 && !record.equals("0-0-0")) {
            summary = r.getString(R.string.team_at_event_no_ranking_data);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(record);
        } else if (performance == MatchHelper.EventPerformance.NOT_PICKED) {
            summary = r.getString(R.string.team_at_event_past_tense_not_picked);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(rank + getOrdinalFor(rank));
            summaryArgs.add(record);
        } else if (performance == MatchHelper.EventPerformance.PLAYING_IN_QUALS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_QUARTERS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_SEMIS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_FINALS) {
            summary = r.getString(R.string.team_at_event_present_tense);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(rank + getOrdinalFor(rank));
            summaryArgs.add(record);
            summaryArgs.addAll(getAllianceArgs(allianceNumber, alliancePick, r));
            summaryArgs.add(performance.description);
        } else if (performance == MatchHelper.EventPerformance.ELIMINATED_IN_QUARTERS
                || performance == MatchHelper.EventPerformance.ELIMINATED_IN_SEMIS
                || performance == MatchHelper.EventPerformance.ELIMINATED_IN_FINALS) {
            summary = r.getString(R.string.team_at_event_past_tense);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(rank + getOrdinalFor(rank));
            summaryArgs.add(record);
            summaryArgs.addAll(getAllianceArgs(allianceNumber, alliancePick, r));
            summaryArgs.add(performance.description);
        } else if (performance == MatchHelper.EventPerformance.WON_EVENT) {
            summary = r.getString(R.string.team_at_event_past_tense_won_event);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(rank + getOrdinalFor(rank));
            summaryArgs.add(record);
            summaryArgs.addAll(getAllianceArgs(allianceNumber, alliancePick, r));
        } else if (performance == MatchHelper.EventPerformance.NO_ALLIANCE_DATA) {
            summary = r.getString(R.string.team_at_event_no_alliance_data);
            summaryArgs.add(teamKey.substring(3));
            summaryArgs.add(rank + getOrdinalFor(rank));
            summaryArgs.add(record);
        }
        return String.format(summary, summaryArgs.toArray());
    }


    private static Collection<Object> getAllianceArgs(int allianceNumber, int alliancePick, Resources r) {
        ArrayList<Object> args = new ArrayList<>();
        if (allianceNumber > 0) {
            switch (alliancePick) {
                case 0:
                    args.add(r.getString(R.string.team_at_event_captain));
                    args.add(allianceNumber + getOrdinalFor(allianceNumber));
                    break;
                default:
                    args.add(alliancePick + getOrdinalFor(alliancePick) + " " + r.getString(R.string.team_at_event_pick));
                    args.add(allianceNumber + getOrdinalFor(allianceNumber));
                    break;
            }
        }
        return args;
    }

    private static String getOrdinalFor(int value) {
        int hundredRemainder = value % 100;
        int tenRemainder = value % 10;
        if (hundredRemainder - tenRemainder == 10) {
            return "th";
        }

        switch (tenRemainder) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
