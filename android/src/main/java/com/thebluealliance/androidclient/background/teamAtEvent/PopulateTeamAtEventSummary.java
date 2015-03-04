package com.thebluealliance.androidclient.background.teamAtEvent;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * File created by phil on 6/3/14.
 */
public class PopulateTeamAtEventSummary extends AsyncTask<String, Void, APIResponse.CODE> {

    String teamKey, eventKey, eventYear, recordString, eventShort;
    RefreshableHostActivity activity;
    TeamAtEventSummaryFragment fragment;
    ArrayList<Match> eventMatches;
    ArrayList<Match> teamMatches;
    ArrayList<ListItem> summary;
    int rank;
    int allianceNumber = -1, alliancePick = -1;
    Event event;
    boolean activeEvent;
    MatchHelper.EventStatus status;
    RequestParams requestParams;

    public PopulateTeamAtEventSummary(TeamAtEventSummaryFragment fragment, RequestParams requestParams) {
        super();
        this.fragment = fragment;
        this.activity = (RefreshableHostActivity) fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {

        if (params.length != 2)
            throw new IllegalArgumentException("PopulateTeamAtEvent must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];

        APIResponse<ArrayList<Match>> matchResponse;
        Match nextMatch = null, lastMatch = null;
        try {
            matchResponse = DataManager.Events.getMatchList(activity, eventKey, requestParams);

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            eventMatches = matchResponse.getData();
            Collections.sort(eventMatches, new MatchSortByPlayOrderComparator());

            teamMatches = MatchHelper.getMatchesForTeam(eventMatches, teamKey);

            int[] record = MatchHelper.getRecordForTeam(eventMatches, teamKey);
            recordString = record[0] + "-" + record[1] + "-" + record[2];
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            matchResponse = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        APIResponse<Event> eventResponse;
        try {
            eventResponse = DataManager.Events.getEvent(activity, eventKey, requestParams);
            event = eventResponse.getData();
            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            if (event.isHappeningNow() && teamMatches != null) {
                nextMatch = MatchHelper.getNextMatchPlayed(teamMatches);
                lastMatch = MatchHelper.getLastMatchPlayed(teamMatches);
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch event data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to determine next/last match being played");
            return APIResponse.CODE.NODATA;
        }

        if (event != null) {
            try {
                eventShort = event.getEventShortName();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't get event short name");
                return APIResponse.CODE.NODATA;
            }
            eventYear = eventKey.substring(0, 4);
            activeEvent = event.isHappeningNow();
            // Search for team in alliances
            JsonArray alliances;
            try {
                alliances = event.getAlliances();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't get event alliances");
                return APIResponse.CODE.NODATA;
            }
            if (alliances.size() == 0) {
                // We don't have alliance data. Try to determine from matches.
                allianceNumber = MatchHelper.getAllianceForTeam(teamMatches, teamKey);
            } else {
                for (int i = 0; i < alliances.size(); i++) {
                    JsonArray teams = alliances.get(i).getAsJsonObject().get("picks").getAsJsonArray();
                    for (int j = 0; j < teams.size(); j++) {
                        if (teams.get(j).getAsString().equals(teamKey)) {
                            allianceNumber = i + 1;
                            alliancePick = j;
                        }
                    }
                }
            }
        } else {
            return APIResponse.CODE.NODATA;
        }

        APIResponse<Integer> rankResponse;
        try {
            rankResponse = DataManager.Teams.getRankForTeamAtEvent(activity, teamKey, eventKey, requestParams);
            rank = rankResponse.getData();
            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch ranking data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        // Generate summary items

        try {
            status = MatchHelper.evaluateStatusOfTeam(event, teamMatches, teamKey);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.d(Constants.LOG_TAG, "Status could not be evaluated for team; missing fields: " + Arrays.toString(e.getStackTrace()));
            status = MatchHelper.EventStatus.NOT_AVAILABLE;
        }

        summary = new ArrayList<>();
        if (status != MatchHelper.EventStatus.NOT_AVAILABLE) {
            // Rank
            if (rank != -1) {
                summary.add(new LabelValueListItem(activity.getString(R.string.team_at_event_rank), rank + Utilities.getOrdinalFor(rank)));
            }
            // Record
            if (!recordString.equals("0-0-0")) {
                summary.add(new LabelValueListItem(activity.getString(R.string.team_at_event_record), recordString));
            }

            // Alliance
            if (status != MatchHelper.EventStatus.PLAYING_IN_QUALS &&
                    status != MatchHelper.EventStatus.NO_ALLIANCE_DATA) {
                summary.add(new LabelValueListItem(activity.getString(R.string.team_at_event_alliance), generateAllianceSummary(activity.getResources(), allianceNumber, alliancePick)));
            }

            // Status
            summary.add(new LabelValueListItem(activity.getString(R.string.team_at_event_status), status.getDescriptionString(activity)));

            if (lastMatch != null) {
                summary.add(new LabelValueListItem(activity.getString(R.string.title_last_match), lastMatch.render()));
            }
            if (nextMatch != null) {
                summary.add(new LabelValueListItem(activity.getString(R.string.title_next_match), nextMatch.render()));
            }
        }

        return APIResponse.mergeCodes(matchResponse.getCode(), eventResponse.getCode(),
                rankResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        View view = fragment.getView();
        if (activity != null && view != null) {
            if (activity.getSupportActionBar() != null && eventShort != null && !eventShort.isEmpty()) {
                activity.setActionBarTitle(String.format(activity.getString(R.string.team_actionbar_title), teamKey.substring(3)));
                activity.setActionBarSubtitle("@ " + eventYear + " " + eventShort);
            }

            ListViewAdapter adapter = new ListViewAdapter(activity, summary);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            // If the adapter has no children, display a generic "no data" message.
            // Otherwise, show the list as normal.
            if (code == APIResponse.CODE.NODATA || (!requestParams.forceFromCache && summary.isEmpty())) {
                noDataText.setText(R.string.not_available);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
                ListView listView = (ListView) view.findViewById(R.id.list);
                listView.setVisibility(View.VISIBLE);
                // If the list hasn't previously been initialized, expand the "summary" view
                Parcelable state = listView.onSaveInstanceState();
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                listView.setAdapter(adapter);
                listView.onRestoreInstanceState(state);

                listView.setSelection(firstVisiblePosition);
                adapter.notifyDataSetChanged();
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);

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
            requestParams.forceFromCache = false;
            PopulateTeamAtEventSummary secondTask = new PopulateTeamAtEventSummary(fragment, requestParams);
            fragment.updateTask(secondTask);
            secondTask.execute(teamKey, eventKey);
        } else {
            // Show notification if we've refreshed data.
            Log.i(Constants.REFRESH_LOG, teamKey + "@" + eventKey + " refresh complete");
            if (activity != null && activity instanceof RefreshableHostActivity) {
                activity.notifyRefreshComplete(fragment);
            }
        }
    }

    private String generateAllianceSummary(Resources r, int allianceNumber, int alliancePick) {
        String[] args = new String[2];
        String summary;
        if (allianceNumber > 0) {
            switch (alliancePick) {
                case 0:
                    args[0] = r.getString(R.string.team_at_event_captain);
                    args[1] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
                case -1:
                    args[0] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
                default:
                    args[0] = alliancePick + Utilities.getOrdinalFor(alliancePick) + " " + r.getString(R.string.team_at_event_pick);
                    args[1] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
            }
            if (alliancePick == -1) {
                summary = String.format(r.getString(R.string.alliance_summary_no_pick_num), args[0]);
            } else {
                summary = String.format(r.getString(R.string.alliance_summary), args[0], args[1]);
            }
        } else {
            summary = r.getString(R.string.not_picked);
        }
        return summary;
    }

}
