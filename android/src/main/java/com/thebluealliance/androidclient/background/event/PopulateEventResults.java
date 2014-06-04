package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamAtEvent;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventResults extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey, teamKey, recordString;
    ArrayList<ListGroup> groups;
    Match nextMatch, lastMatch;

    public PopulateEventResults(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
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
            response = DataManager.getMatchList(activity, eventKey, teamKey);
            ArrayList<Match> results = response.getData(); //sorted by play order

            ListGroup currentGroup = qualMatches;
            Match.TYPE lastType = null;
            Match previousIteration = null;
            boolean lastMatchPlayed = false;
            for (Match match : results) {

                if(lastType != match.getType()){
                    switch (match.getType()){
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

                if(lastMatchPlayed && !match.hasBeenPlayed()){
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
            if(lastMatch == null && results.size() > 0){
                lastMatch = results.get(results.size() -1);
            }

            if (!teamKey.isEmpty()) {
                recordString = record[0] + "-" + record[1] + "-" + record[2];
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        if (qualMatches.children.size() > 0) {
            groups.add(qualMatches);
        }
        if (quarterMatches.children.size() > 0) {
            groups.add(quarterMatches);
        }
        if (semiMatches.children.size() > 0) {
            groups.add(semiMatches);
        }
        if (finalMatches.children.size() > 0) {
            groups.add(finalMatches);
        }

        return response.getCode();
    }

    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            MatchListAdapter adapter = new MatchListAdapter(activity, groups, teamKey);
            ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.match_results);
            listView.setAdapter(adapter);

            //set action bar title
            if (teamKey.isEmpty()) {
                view.findViewById(R.id.progress).setVisibility(View.GONE);
            } else {
                PopulateTeamAtEvent task = new PopulateTeamAtEvent(activity, adapter);
                task.setLastMatch(lastMatch);
                task.setNextMatch(nextMatch);
                task.execute(teamKey, eventKey, recordString);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
        }
    }
}
