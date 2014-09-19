package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByDisplayOrderComparator;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.greenrobot.event.EventBus;

/**
 * Retrieves event results for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/22/14.
 */
public class PopulateEventMatches extends AsyncTask<String, Void, APIResponse.CODE> {

    private EventMatchesFragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private String teamKey;
    private boolean forceFromCache;
    ArrayList<ListGroup> groups;
    Match nextMatch, lastMatch;
    Event event;
    MatchListAdapter adapter;

    public PopulateEventMatches(EventMatchesFragment f, boolean forceFromCache) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];
        if (params.length == 2) {
            teamKey = params[1];
        } else {
            teamKey = "";
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

        groups = new ArrayList<>();
        ListGroup qualMatches = new ListGroup(activity.getString(R.string.quals_header));
        ListGroup quarterMatches = new ListGroup(activity.getString(R.string.quarters_header));
        ListGroup semiMatches = new ListGroup(activity.getString(R.string.semis_header));
        ListGroup finalMatches = new ListGroup(activity.getString(R.string.finals_header));
        APIResponse<ArrayList<Match>> response;
        int[] record = {0, 0, 0}; //wins, losses, ties
        try {
            response = DataManager.Events.getMatchList(activity, eventKey, teamKey, forceFromCache);
            ArrayList<Match> results = response.getData();

            if(event != null && event.isHappeningNow()){
                Collections.sort(results, new MatchSortByPlayOrderComparator());
            } else {
                Collections.sort(results, new MatchSortByDisplayOrderComparator());
            }

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            ListGroup currentGroup = qualMatches;
            MatchHelper.TYPE lastType = null;
            Match previousIteration = null;
            boolean lastMatchPlayed = false;
            if(results.size() > 0){
                nextMatch = results.get(0);
            }
            for (Match match : results) {
                try {
                    MatchHelper.TYPE currentType = match.getType();
                    if (lastType != currentType) {
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
                    lastType = currentType;
                    previousIteration = match;
                    lastMatchPlayed = match.hasBeenPlayed();
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Can't get match type. Missing fields..." +
                            Arrays.toString(e.getStackTrace()));
                }
            }
            if (lastMatch == null && !results.isEmpty()) {
                Match last = results.get(results.size() - 1);
                if(last.hasBeenPlayed()) {
                    lastMatch = last;
                }
            }

        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        if (!qualMatches.children.isEmpty()) {
            groups.add(qualMatches);
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

        adapter = new MatchListAdapter(activity, groups, teamKey);

        return APIResponse.mergeCodes(eventResponse.getCode(), response.getCode());
    }

    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_match_data);

            // If there's no results in the adapter or if we can't download info
            // off the web, display a message.
            // only show the message when try try and actually load data from the web
            if (code == APIResponse.CODE.NODATA || (!forceFromCache && (groups == null || adapter.groups.isEmpty()))) {
                noDataText.setVisibility(View.VISIBLE);
                noDataText.setText(teamKey.isEmpty() ? R.string.no_match_data : R.string.no_team_match_data);
            } else {
                noDataText.setVisibility(View.GONE);
                ExpandableListView results = (ExpandableListView) view.findViewById(R.id.match_results);
                Parcelable state = results.onSaveInstanceState();
                int firstVisiblePosition = results.getFirstVisiblePosition();
                results.setAdapter(adapter);
                results.onRestoreInstanceState(state);
                results.setSelection(firstVisiblePosition);
                if (groups.size() == 1) {
                    results.expandGroup(0);
                }
                adapter.notifyDataSetChanged();
            }

            // Alert any interested parties of matches that are being played if this is a live event
            if(code != APIResponse.CODE.NODATA) {
                if (event.isHappeningNow()) {
                    // Send out that there are live matches happening for other things to pick up
                    Log.d(Constants.LOG_TAG, "Sending live event broadcast: " + eventKey);
                    EventBus.getDefault().post(new LiveEventMatchUpdateEvent(lastMatch, nextMatch));
                } else{
                    Log.d(Constants.LOG_TAG, "Not sending live event broadcast");
                }
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.match_results).setVisibility(View.VISIBLE);

            // Display warning message if offline.
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
            PopulateEventMatches secondLoad = new PopulateEventMatches(mFragment, false);
            mFragment.updateTask(secondLoad);
            secondLoad.execute(eventKey, teamKey);
        } else {
            // Show notification if we've refreshed data.
            if (mFragment instanceof RefreshListener) {
                Log.i(Constants.REFRESH_LOG, "Event " + eventKey + " Results refresh complete");
                activity.notifyRefreshComplete(mFragment);
            }
        }
    }
}
