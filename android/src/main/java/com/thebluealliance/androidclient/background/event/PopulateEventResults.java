package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventResults extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey, teamKey, recordString;
    SparseArray<ListGroup> groups;
    private Event event;
    private int rank;

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


        groups = new SparseArray<>();
        ListGroup awards = new ListGroup("Awards");
        ListGroup qualMatches = new ListGroup("Qualification Matches");
        ListGroup quarterMatches = new ListGroup("Quarterfinal Matches");
        ListGroup semiMatches = new ListGroup("Semifinal Matches");
        ListGroup finalMatches = new ListGroup("Finals Matches");
        MatchSortByPlayOrderComparator comparator = new MatchSortByPlayOrderComparator();
        APIResponse<HashMap<Match.TYPE, ArrayList<Match>>> response;
        int[] record = {0, 0, 0}; //wins, losses, ties
        try {
            event = DataManager.getEvent(activity, eventKey).getData();

            response = DataManager.getEventResults(activity, eventKey, teamKey);
            HashMap<Match.TYPE, ArrayList<Match>> results = response.getData();

            Collections.sort(results.get(Match.TYPE.QUAL), comparator);
            Collections.sort(results.get(Match.TYPE.QUARTER), comparator);
            Collections.sort(results.get(Match.TYPE.SEMI), comparator);
            Collections.sort(results.get(Match.TYPE.FINAL), comparator);

            ListGroup currentGroup = qualMatches;
            for (Map.Entry<Match.TYPE, ArrayList<Match>> entry : results.entrySet()) {
                switch (entry.getKey()) {
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
                for (Match m : entry.getValue()) {
                    currentGroup.children.add(m);
                    currentGroup.childrenKeys.add(m.getKey());

                    m.addToRecord(teamKey, record);
                }
            }

            if (!teamKey.isEmpty()) {
                recordString = record[0] + "-" + record[1] + "-" + record[2];
                rank = DataManager.getRankForTeamAtEvent(activity, teamKey, eventKey).getData();

                ArrayList<Award> awardList = DataManager.getEventAwards(activity, eventKey, teamKey).getData();
                awards.children.addAll(awardList);
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event results");
            response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        int numGroups = 0;
        if(awards.children.size() > 0){
            groups.append(numGroups, awards);
            numGroups++;
        }
        if (qualMatches.children.size() > 0) {
            groups.append(numGroups, qualMatches);
            numGroups++;
        }
        if (quarterMatches.children.size() > 0) {
            groups.append(numGroups, quarterMatches);
            numGroups++;
        }
        if (semiMatches.children.size() > 0) {
            groups.append(numGroups, semiMatches);
            numGroups++;
        }
        if (finalMatches.children.size() > 0) {
            groups.append(numGroups, finalMatches);
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
                activity.getActionBar().setTitle(event.getEventName());
            } else {
                activity.getActionBar().setTitle(teamKey.substring(3) + " @ " + event.getShortName());

                //set the other UI elements specific to team@event
                ((TextView) activity.findViewById(R.id.team_record)).setText(Html.fromHtml(
                        String.format(activity.getString(R.string.team_record),
                                teamKey.substring(3), rank, recordString)
                ));

                activity.findViewById(R.id.content_view).setVisibility(View.VISIBLE);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
