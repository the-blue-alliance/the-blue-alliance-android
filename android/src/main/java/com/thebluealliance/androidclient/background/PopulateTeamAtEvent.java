package com.thebluealliance.androidclient.background;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
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
    ArrayList<Match> eventMatches;
    ArrayList<ListGroup> matchGroups;
    int rank;
    int allianceNumber = -1, alliancePick = -1;
    ListGroup summary, awards, stats;
    Event event;
    Match lastMatch, nextMatch;
    boolean activeEvent, forceFromCache;
    MatchHelper.EventStatus status;

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

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            ArrayList<Match> matches = matchResponse.getData(); //sorted by play order
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
            eventResponse = DataManager.Events.getEvent(activity, eventKey, forceFromCache);
            event = eventResponse.getData();
            if (isCancelled()) {
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
            rankResponse = DataManager.Teams.getRankForTeamAtEvent(activity, teamKey, eventKey, forceFromCache);
            rank = rankResponse.getData();
            if (isCancelled()) {
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
            if (isCancelled()) {
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

            if (isCancelled()) {
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

        // Generate summary items

        status = MatchHelper.evaluateStatusOfTeam(event, eventMatches, teamKey);

        summary = new ListGroup(activity.getString(R.string.summary));
        if (status != MatchHelper.EventStatus.NOT_AVAILABLE) {
            // Rank
            if (rank != -1) {
                summary.children.add(new SummaryModel("Rank", rank + getOrdinalFor(rank)));
            }
            // Record
            if (!recordString.equals("0-0-0")) {
                summary.children.add(new SummaryModel("Record", recordString));
            }

            // Alliance
            if (status != MatchHelper.EventStatus.NO_ALLIANCE_DATA) {
                summary.children.add(new SummaryModel("Alliance", generateAllianceSummary(activity.getResources(), allianceNumber, alliancePick)));
            }

            // Status
            summary.children.add(new SummaryModel("Status", status.getDescriptionString(activity)));
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

            if (!summary.children.isEmpty()) {
                adapter.addGroup(0, summary);
            }

            // If the adapter has no children, display a generic "no data" message.
            // Otherwise, show the list as normal.
            if(adapter.isEmpty()) {
                activity.findViewById(R.id.status_message).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.results).setVisibility(View.GONE);
            } else {
                activity.findViewById(R.id.status_message).setVisibility(View.GONE);
                activity.findViewById(R.id.results).setVisibility(View.VISIBLE);

                ExpandableListView listView = (ExpandableListView) activity.findViewById(R.id.results);
                // If the list hasn't previously been initialized, expand the "summary" view
                boolean shouldExpandSummary = false;
                if(listView.getExpandableListAdapter() == null) {
                    shouldExpandSummary = true;
                }
                Parcelable state = listView.onSaveInstanceState();
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                listView.setAdapter(adapter);
                listView.onRestoreInstanceState(state);
                if(shouldExpandSummary) {
                    listView.expandGroup(0);
                }
                listView.setSelection(firstVisiblePosition);
            }

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
            if (activity instanceof RefreshableHostActivity) {
                activity.notifyRefreshComplete((RefreshListener) activity);
            }
        }

    }

    private String generateAllianceSummary(Resources r, int allianceNumber, int alliancePick) {
        ArrayList<Object> args = new ArrayList<>();
        String summary = "";
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
            summary = String.format(r.getString(R.string.alliance_summary), args.toArray());
        } else {
            summary = r.getString(R.string.not_picked);
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

    private class SummaryListItem extends ListElement {

        String label, value;

        public SummaryListItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public View getView(Context c, LayoutInflater inflater, View convertView) {
            ViewHolder holder;

            if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
                convertView = inflater.inflate(R.layout.list_item_summary, null);

                holder = new ViewHolder();
                holder.label = (TextView) convertView.findViewById(R.id.label);
                holder.value = (TextView) convertView.findViewById(R.id.value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.label.setText(label);
            holder.value.setText(value);

            return convertView;
        }

        private class ViewHolder {
            TextView label;
            TextView value;
        }
    }

    private class SummaryModel implements BasicModel {

        private String label, value;

        public SummaryModel(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public ListElement render() {
            return new SummaryListItem(label, value);
        }

        @Override
        public ContentValues getParams() {
            return null;
        }
    }
}
