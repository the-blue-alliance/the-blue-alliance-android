package com.thebluealliance.androidclient.background.event;

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
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.comparators.TeamSortByOPRComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Retrieves general information about an FRC event, like name, location, and social media links.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<String, String, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    View last, next;
    LinearLayout nextLayout, lastLayout, topTeams, topOpr;
    TextView eventName, eventDate, eventLoc, eventVenue, ranks, stats;
    String eventKey;
    Event event;
    private boolean showLastMatch, showNextMatch, showRanks, showStats, forceFromCache;

    public PopulateEventInfo(Fragment f, boolean forceFromCache) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); // reset event settings
        showLastMatch = showNextMatch = showRanks = showStats = false;
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        View view = mFragment.getView();
        // Initialize the views.

        APIResponse<Event> eventResponse = new APIResponse<>(null, APIResponse.CODE.NODATA);
        APIResponse<ArrayList<JsonArray>> rankResponse = new APIResponse<>(null, APIResponse.CODE.CACHED304);
        APIResponse<JsonObject> statsResponse = new APIResponse<>(null, APIResponse.CODE.CACHED304);
        APIResponse<ArrayList<Match>> matchResult = new APIResponse<>(null, APIResponse.CODE.CACHED304);

        if (view != null && activity != null && eventKey != null) {
            eventName = (TextView) view.findViewById(R.id.event_name);
            eventDate = (TextView) view.findViewById(R.id.event_date);
            eventLoc = (TextView) view.findViewById(R.id.event_location);
            eventVenue = (TextView) view.findViewById(R.id.event_venue);
            nextLayout = (LinearLayout) view.findViewById(R.id.event_next_match_container);
            lastLayout = (LinearLayout) view.findViewById(R.id.event_last_match_container);
            topTeams = (LinearLayout) view.findViewById(R.id.event_top_teams_container);
            topOpr = (LinearLayout) view.findViewById(R.id.event_top_opr_container);

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                eventResponse = DataManager.Events.getEvent(activity, eventKey, forceFromCache);
                event = eventResponse.getData();
                //return response.getCode();
                if(isCancelled()){
                    return APIResponse.CODE.NODATA;
                }
            } catch (DataManager.NoDataException e) {
                Log.w(Constants.LOG_TAG, "unable to load event info");
                return APIResponse.CODE.NODATA;
            }

            if (event.hasStarted()) {
                //event has started (may or may not have finished).
                //show the ranks and stats
                showRanks = showStats = true;
                ranks = new TextView(activity);
                try {
                    rankResponse = DataManager.Events.getEventRankings(activity, eventKey, forceFromCache);
                    ArrayList<JsonArray> rankList = rankResponse.getData();
                    String rankString = "";
                    if (rankList.isEmpty() || rankList.size() == 1) {
                        showRanks = false;
                    }
                    for (int i = 1; i < Math.min(6, rankList.size()); i++) {
                        rankString += ((i) + ". " + rankList.get(i).get(1).getAsString());
                        if(i < Math.min(6, rankList.size()) - 1) {
                            rankString += "\n";
                        }
                    }
                    ranks.setText(rankString);
                    if(isCancelled()){
                        return APIResponse.CODE.NODATA;
                    }
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "Unable to load event rankings");
                    showRanks = false;
                    return APIResponse.CODE.NODATA;
                }

                stats = new TextView(activity);
                try {
                    statsResponse = DataManager.Events.getEventStats(activity, eventKey, forceFromCache);
                    ArrayList<Map.Entry<String, JsonElement>> opr = new ArrayList<>();
                    if (statsResponse.getData().has("oprs") &&
                            !statsResponse.getData().get("oprs").getAsJsonObject().entrySet().isEmpty()) {
                        // ^ Make sure we actually have OPRs in our set!
                        opr.addAll(statsResponse.getData().get("oprs").getAsJsonObject().entrySet());

                        // Sort OPRs in decreasing order (highest to lowest)
                        Collections.sort(opr, new TeamSortByOPRComparator());
                        Collections.reverse(opr);

                        String statsString = "";
                        for (int i = 0; i < Math.min(5, opr.size()); i++) {
                            statsString += (i + 1) + ". " + opr.get(i).getKey();
                            if(i < Math.min(5, opr.size()) - 1) {
                                statsString += "\n";
                            }
                        }
                        stats.setText(statsString);
                    } else {
                        showStats = false;
                    }
                    if(isCancelled()){
                        return APIResponse.CODE.NODATA;
                    }
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to load event stats");
                    showStats = false;
                    return APIResponse.CODE.NODATA;
                }
            }

            if (event.isHappeningNow()) {
                //show the next/last matches, if applicable
                try {
                    matchResult = DataManager.Events.getMatchList(activity, eventKey, forceFromCache);
                    ArrayList<Match> matches = matchResult.getData();
                    Collections.sort(matches, new MatchSortByPlayOrderComparator());
                    Match nextMatch = MatchHelper.getNextMatchPlayed(matches);
                    Match lastMatch = MatchHelper.getLastMatchPlayed(matches);

                    if (nextMatch != null) {
                        showNextMatch = true;
                        next = nextMatch.render().getView(activity, inflater, null);
                    }
                    if (lastMatch != null) {
                        showLastMatch = true;
                        last = lastMatch.render().getView(activity, inflater, null);
                    }
                    if(isCancelled()){
                        return APIResponse.CODE.NODATA;
                    }
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to load match list");
                    return APIResponse.CODE.NODATA;
                }
            }

            // setup social media intents
            view.findViewById(R.id.event_venue_container).setTag("geo:0,0?q=" + event.getVenue().replace(" ", "+"));
            view.findViewById(R.id.event_location_container).setTag("geo:0,0?q=" + event.getLocation().replace(" ", "+"));
            view.findViewById(R.id.event_website_button).setTag(!event.getWebsite().isEmpty() ? event.getWebsite() : "https://www.google.com/search?q=" + event.getEventName());
            view.findViewById(R.id.event_twitter_button).setTag("https://twitter.com/search?q=%23" + event.getEventKey());
            view.findViewById(R.id.event_youtube_button).setTag("https://www.youtube.com/results?search_query=" + event.getEventKey());
            view.findViewById(R.id.event_cd_button).setTag("http://www.chiefdelphi.com/media/photos/tags/" + event.getEventKey());
        }

        return APIResponse.mergeCodes(eventResponse.getCode(), rankResponse.getCode(), matchResult.getCode(), statsResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);

        if (event != null && activity != null) {
            activity.setActionBarTitle(event.getEventName());

            // Set the new info (if necessary)
            eventName.setText(event.getEventName());
            if (event.getDateString().isEmpty()) {
                activity.findViewById(R.id.event_date_container).setVisibility(View.GONE);
            } else {
                eventDate.setText(event.getDateString());
            }
            if (event.getVenue().isEmpty() &&
                activity.findViewById(R.id.event_venue_container) != null){
                activity.findViewById(R.id.event_venue_container).setVisibility(View.GONE);
            }
            else{
                eventVenue.setText(event.getVenue());
                if (activity.findViewById(R.id.event_location_container) != null) {
                    activity.findViewById(R.id.event_location_container).setVisibility(View.GONE);
                }
            }
            if (event.getLocation().isEmpty() &&
                    activity.findViewById(R.id.event_location_container) != null) {
                activity.findViewById(R.id.event_location_container).setVisibility(View.GONE);
            } else {
                eventLoc.setText(event.getLocation());
            }
            if (showNextMatch) {
                nextLayout.setVisibility(View.VISIBLE);
                if (nextLayout.getChildCount() > 1) {
                    nextLayout.removeViewAt(1);
                }
                nextLayout.addView(next);
            }
            if (showLastMatch) {
                lastLayout.setVisibility(View.VISIBLE);
                if (lastLayout.getChildCount() > 1) {
                    lastLayout.removeViewAt(1);
                }
                lastLayout.addView(last);
            }
            if (showRanks) {
                topTeams.setVisibility(View.VISIBLE);
                if (topTeams.getChildCount() > 1) {
                    topTeams.removeViewAt(1);
                }
                topTeams.addView(ranks);
            }

            if (showStats) {
                topOpr.setVisibility(View.VISIBLE);
                if (topOpr.getChildCount() > 1) {
                    topOpr.removeViewAt(1);
                }
                topOpr.addView(stats);
            }

            // Display warning if offline.
            if (c == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show info, since we're done loading the data.
            View view = mFragment.getView();
            if (view != null) {
                view.findViewById(R.id.progress).setVisibility(View.GONE);
                view.findViewById(R.id.event_info_container).setVisibility(View.VISIBLE);
            }

            if (c == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                new PopulateEventInfo(mFragment, false).execute(eventKey);
            } else {
                // Show notification if we've refreshed data.
                if (mFragment instanceof RefreshListener) {
                    Log.d(Constants.LOG_TAG, "Event Info refresh complete");
                    activity.notifyRefreshComplete((RefreshListener) mFragment);
                }
            }
        }
    }
}
