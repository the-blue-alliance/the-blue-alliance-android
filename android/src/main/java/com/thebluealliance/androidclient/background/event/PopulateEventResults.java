package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.datamanger.DataManager;
import com.thebluealliance.androidclient.datafeed.datamanger.Events;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;

/**
 * Retrieves event results for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/22/14.
 */
public class PopulateEventResults extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private String teamKey;
    private boolean forceFromCache;
    ArrayList<ListGroup> groups;
    Match nextMatch, lastMatch;
    Event event;

    public PopulateEventResults(Fragment f, boolean forceFromCache) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];
        if (params.length == 2) {
            teamKey = params[1];
        } else {
            teamKey = "";
        }


        groups = new ArrayList<>();
        ListGroup qualMatches = new ListGroup(activity.getString(R.string.quals_header));
        ListGroup quarterMatches = new ListGroup(activity.getString(R.string.quarters_header));
        ListGroup semiMatches = new ListGroup(activity.getString(R.string.semis_header));
        ListGroup finalMatches = new ListGroup(activity.getString(R.string.finals_header));
        MatchSortByPlayOrderComparator comparator = new MatchSortByPlayOrderComparator();
        APIResponse<ArrayList<Match>> response;
        int[] record = {0, 0, 0}; //wins, losses, ties
        try {
            response = Events.getMatchList(activity, eventKey, teamKey, forceFromCache);
            ArrayList<Match> results = response.getData(); //sorted by play order

            ListGroup currentGroup = qualMatches;
            MatchHelper.TYPE lastType = null;
            Match previousIteration = null;
            boolean lastMatchPlayed = false;
            for (Match match : results) {

                if (lastType != match.getType()) {
                    switch (match.getType()) {
                        case QUAL:
                            currentGroup = qualMatches;
                            break;
                        case QUARTER:
                            currentGroup = quarterMatches;
                            break;
                        case SEMI:
                            currentGroup = semiMatches;
                            break;
                        case FINAL:
                            currentGroup = finalMatches;
                            break;
                    }
                }

                currentGroup.children.add(match);

                if (lastMatchPlayed && !match.hasBeenPlayed()) {
                    lastMatch = previousIteration;
                    nextMatch = match;
                }

                /**
                 * the only reason this isn't moved to PopulateTeamAtEvent is that if so,
                 * we'd have to iterate through every match again to calculate the
                 * record, and that's just wasteful
                 */
                match.addToRecord(teamKey, record);
                lastType = match.getType();
                previousIteration = match;
                lastMatchPlayed = match.hasBeenPlayed();
            }
            if (lastMatch == null && !results.isEmpty()) {
                lastMatch = results.get(results.size() - 1);
            }

            if (!teamKey.isEmpty()) {
                String recordString = record[0] + "-" + record[1] + "-" + record[2];
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        APIResponse<Event> eventResponse;
        try {
            eventResponse = Events.getEvent(activity, eventKey, forceFromCache);
            event = eventResponse.getData();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch event data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        if (!qualMatches.children.isEmpty()) {
            groups.add(qualMatches);
        }

        ArrayList<AllianceListElement> alliances = event.renderAlliances();
        if (!alliances.isEmpty()) {
            ListGroup allianceGroup = new ListGroup(activity.getString(R.string.alliances_header));
            allianceGroup.children.addAll(alliances);
            groups.add(allianceGroup);
        }

        if (!quarterMatches.children.isEmpty()) {
            groups.add(quarterMatches);
        }
        if (!semiMatches.children.isEmpty()) {
            groups.add(semiMatches);
        }
        if (!finalMatches.children.isEmpty()) {
            groups.add(finalMatches);
        }

        return APIResponse.mergeCodes(eventResponse.getCode(), response.getCode());
    }

    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            MatchListAdapter adapter = new MatchListAdapter(activity, groups, teamKey);
            TextView noDataText = (TextView) view.findViewById(R.id.no_match_data);

            // If there's no results in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || groups == null || adapter.groups.isEmpty()) {
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.match_results);
                listView.setAdapter(adapter);
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.match_results).setVisibility(View.VISIBLE);

            // Display warning message if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }

        if (code == APIResponse.CODE.LOCAL) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            new PopulateEventResults(mFragment, false).execute(eventKey, teamKey);
        } else {
            // Show notification if we've refreshed data.
            if (mFragment instanceof RefreshListener) {
                Log.d(Constants.LOG_TAG, "Event Results refresh complete");
                activity.notifyRefreshComplete((RefreshListener) mFragment);
            }
        }
    }
}
