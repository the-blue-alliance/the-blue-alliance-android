package com.thebluealliance.androidclient.background;

import android.content.res.Resources;
import android.os.AsyncTask;
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
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.MatchHelper;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;
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
    boolean activeEvent;

    public PopulateTeamAtEvent(RefreshableHostActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {

        if (params.length != 2)
            throw new IllegalArgumentException("PopulateTeamAtEvent must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];

        APIResponse<ArrayList<Match>> matchResponse;
        try {
            matchResponse = DataManager.getMatchList(activity, eventKey, teamKey);
            ArrayList<Match> matches = matchResponse.getData(); //sorted by play order
            matchResponse = DataManager.getMatchList(activity, eventKey);
            eventMatches = matchResponse.getData(); //sorted by play order
            matchGroups = MatchHelper.constructMatchList(activity, matches);
            int[] record = MatchHelper.getRecordForTeam(matches, teamKey);
            recordString = record[0] + "-" + record[1] + "-" + record[2];
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            matchResponse = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        APIResponse<Event> eventResponse;
        try {
            eventResponse = DataManager.getEvent(activity, eventKey);
            event = eventResponse.getData();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch event data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        if (event != null) {
            eventShort = event.getShortName();
            activeEvent = event.isHappeningNow();
            // Search for team in alliances
            JsonArray alliances = event.getAlliances();
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
            rankResponse = DataManager.getRankForTeamAtEvent(activity, teamKey, eventKey);
            rank = rankResponse.getData();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch ranking data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        APIResponse<ArrayList<Award>> awardResponse;
        try {
            awardResponse = DataManager.getEventAwards(activity, eventKey, teamKey);
            ArrayList<Award> awardList = awardResponse.getData();
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
            ExpandableListView listView = (ExpandableListView) activity.findViewById(R.id.results);
            listView.setAdapter(adapter);

            MatchHelper.EventPerformance performance =
                    MatchHelper.evaluatePerformanceForTeam(event, eventMatches, teamKey);
            String summary = generateTeamSummary(teamKey, rank,
                                                 recordString, allianceNumber, alliancePick, performance);
            ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(summary));

            if (stats.children.size() > 0) {
                adapter.addGroup(0, stats);
            }

            if (awards.children.size() > 0) {
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

            activity.findViewById(R.id.team_at_event_progress).setVisibility(View.GONE);
            activity.findViewById(R.id.content_view).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
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
        }  else if (performance == MatchHelper.EventPerformance.NOT_PICKED) {
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
