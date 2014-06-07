package com.thebluealliance.androidclient.background;

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

        return APIResponse.mergeCodes(matchResponse.getCode(), eventResponse.getCode(), rankResponse.getCode(), awardResponse.getCode(), statsResponse.getCode());
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

            //set the other UI elements specific to team@event
            ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(
                    String.format(activity.getString(R.string.team_record),
                            teamKey.substring(3), rank, recordString)
            ));

            long startTime = System.nanoTime();
            MatchHelper.EventPerformance performance = MatchHelper.evaluatePerformanceForTeam(event, eventMatches, teamKey);
            long endTime = System.nanoTime();
            Log.d(Constants.LOG_TAG, "Elapsed time calculating event performance: " + (endTime - startTime) + " nanos");
            String summary = generateTeamSummary(teamKey, event,eventMatches,  rank,  recordString, allianceNumber, alliancePick, performance);
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

    private String generateTeamSummary(String teamKey, Event event, ArrayList<Match> eventMatches, int rank, String record, int allianceNumber, int alliancePick, MatchHelper.EventPerformance performance) {
        String summary = "";
        if (performance == MatchHelper.EventPerformance.NOT_AVAILABLE) {
            return "No data available";
        } else if (performance == MatchHelper.EventPerformance.PLAYING_IN_QUALS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_QUARTERS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_SEMIS
                || performance == MatchHelper.EventPerformance.PLAYING_IN_FINALS) {
            summary = "Team " + teamKey.substring(3) + " is ranked <b>" + rank + getOrdinalFor(rank) + " and has a record of <b>" + recordString + "</b>. ";
            if (allianceNumber > 0) {
                summary += "They are the <b>";
                switch (alliancePick) {
                    case 0:
                        summary += "captain</b> of the " + allianceNumber + getOrdinalFor(allianceNumber) + " alliance.";
                        break;
                    default:
                        summary += alliancePick + getOrdinalFor(alliancePick) + " pick</b> of the <b>" + allianceNumber + getOrdinalFor(allianceNumber) + " alliance</b>.";
                        break;
                }
            }
            summary += " They are currently <b>" + performance.description + "</b>.";
        } else if (performance == MatchHelper.EventPerformance.ELIMINATED_IN_QUARTERS
                || performance == MatchHelper.EventPerformance.ELIMINATED_IN_SEMIS
                || performance == MatchHelper.EventPerformance.ELIMINATED_IN_FINALS
                || performance == MatchHelper.EventPerformance.WON_EVENT) {
            summary = "Team " + teamKey.substring(3) + " was ranked <b>" + rank + getOrdinalFor(rank) + "</b> and had a record of <b>" + recordString + "</b>. ";
            if (allianceNumber > 0) {
                summary += "They were the <b>";
                switch (alliancePick) {
                    case 0:
                        summary += "captain</b> of the <b>" + allianceNumber + getOrdinalFor(allianceNumber) + "</b> alliance.";
                        break;
                    default:
                        summary += alliancePick + getOrdinalFor(alliancePick) + " pick</b> of the <b>" + allianceNumber + getOrdinalFor(allianceNumber) + "</b> alliance.";
                        break;
                }
            }
            if (performance != MatchHelper.EventPerformance.WON_EVENT) {
                summary += " They were <b>" + performance.description + "</b>.";
            } else {
                summary += " They <b>won the event</b>!";
            }
        } else if (performance == MatchHelper.EventPerformance.NOT_PICKED) {
            summary = "Team " + teamKey.substring(3) + " was ranked <b>" + rank + getOrdinalFor(rank) + "</b> and had a record of <b>" + recordString + "</b>. They were <b>not picked</b> for an alliance.";
        }
        return summary;
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
