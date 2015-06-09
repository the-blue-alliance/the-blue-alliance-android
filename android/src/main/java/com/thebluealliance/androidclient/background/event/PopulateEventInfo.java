package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.comparators.TeamSortByStatComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.eventbus.EventInfoLoadedEvent;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Retrieves general information about an FRC event, like name, location, and social media links.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p>
 *         File created by phil on 4/22/14.
 */
public class PopulateEventInfo extends AsyncTask<String, String, APIResponse.CODE> {

    private EventInfoFragment mFragment;
    private RefreshableHostActivity activity;
    View topTeamsContainer, topOprsContainer;
    TextView eventName, eventDate, eventLoc, eventVenue, topTeams, topOprs;
    String eventKey, topTeamsString, topOprsString, nameString, titleString, venueString, locationString;
    Event event;
    private boolean showRanks, showStats;
    private RequestParams requestParams;
    private long startTime;

    public PopulateEventInfo(EventInfoFragment f, RequestParams requestParams) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); // reset event settings
        startTime = System.currentTimeMillis();
        showRanks = showStats = false;

        View view = mFragment.getView();
        if (view == null) {
            cancel(true);
            return;
        }
        eventName = (TextView) view.findViewById(R.id.event_name);
        eventDate = (TextView) view.findViewById(R.id.event_date);
        eventLoc = (TextView) view.findViewById(R.id.event_location);
        eventVenue = (TextView) view.findViewById(R.id.event_venue);
        topTeamsContainer = view.findViewById(R.id.top_teams_container);
        topOprsContainer = view.findViewById(R.id.top_oprs_container);
        topTeams = (TextView) view.findViewById(R.id.top_teams);
        topOprs = (TextView) view.findViewById(R.id.top_oprs);
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        APIResponse<Event> eventResponse = new APIResponse<>(null, APIResponse.CODE.NODATA);
        APIResponse<ArrayList<JsonArray>> rankResponse = new APIResponse<>(null, APIResponse.CODE.CACHED304);
        APIResponse<JsonObject> statsResponse = new APIResponse<>(null, APIResponse.CODE.CACHED304);
        APIResponse<ArrayList<Match>> matchResult = new APIResponse<>(null, APIResponse.CODE.CACHED304);

        if (isCancelled()) {
            return APIResponse.CODE.NODATA;
        }

        if (activity != null && eventKey != null) {
            try {
                eventResponse = DataManager.Events.getEvent(activity, eventKey, requestParams);
                event = eventResponse.getData();
                //return response.getCode();
                if (isCancelled()) {
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
                try {
                    rankResponse = DataManager.Events.getEventRankings(activity, eventKey, requestParams);
                    ArrayList<JsonArray> rankList = rankResponse.getData();
                    String rankString = "";
                    if (rankList.isEmpty() || rankList.size() == 1) {
                        showRanks = false;
                    }
                    for (int i = 1; i < Math.min(6, rankList.size()); i++) {
                        rankString += ((i) + ". <b>" + rankList.get(i).get(1).getAsString()) + "</b>";
                        if (i < Math.min(6, rankList.size()) - 1) {
                            rankString += "<br>";
                        }
                    }
                    rankString.trim();
                    topTeamsString = rankString;
                    if (isCancelled()) {
                        return APIResponse.CODE.NODATA;
                    }
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "Unable to load event rankings");
                    showRanks = false;
                    return APIResponse.CODE.NODATA;
                }

                try {
                    statsResponse = DataManager.Events.getEventStats(activity, eventKey, requestParams);
                    ArrayList<Map.Entry<String, JsonElement>> opr = new ArrayList<>();
                    if (statsResponse.getData().has("oprs") &&
                            !statsResponse.getData().get("oprs").getAsJsonObject().entrySet().isEmpty()) {
                        // ^ Make sure we actually have OPRs in our set!
                        opr.addAll(statsResponse.getData().get("oprs").getAsJsonObject().entrySet());

                        // Sort OPRs in decreasing order (highest to lowest)
                        Collections.sort(opr, new TeamSortByStatComparator());
                        Collections.reverse(opr);

                        String statsString = "";
                        for (int i = 0; i < Math.min(5, opr.size()); i++) {
                            statsString += (i + 1) + ". <b>" + opr.get(i).getKey() + "</b>";
                            if (i < Math.min(5, opr.size()) - 1) {
                                statsString += "<br>";
                            }
                        }
                        statsString = statsString.trim();
                        topOprsString = statsString;
                    } else {
                        showStats = false;
                    }
                    if (isCancelled()) {
                        return APIResponse.CODE.NODATA;
                    }
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to load event stats");
                    showStats = false;
                    return APIResponse.CODE.NODATA;
                }
            }

            try {
                nameString = event.getEventName();
                titleString = event.getEventYear() + " " + event.getEventShortName();
                venueString = event.getVenue();
                locationString = event.getLocation();
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.e(Constants.LOG_TAG, "Can't create social media intents. Missing event fields.\n" +
                        Arrays.toString(e.getStackTrace()));
                nameString = "Name not found";
                venueString = "";
                locationString = "";
            }
        }

        return APIResponse.mergeCodes(eventResponse.getCode(), rankResponse.getCode(), matchResult.getCode(), statsResponse.getCode());
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);

        if (activity != null && mFragment != null && mFragment.getView() != null) {
            View view = mFragment.getView();

            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            View content = view.findViewById(R.id.event_content);
            if (c == APIResponse.CODE.NODATA) {
                noDataText.setText(R.string.no_data);
                noDataText.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
            } else if (event != null) {
                activity.setActionBarTitle(titleString);

                noDataText.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);

                // Set the new info (if necessary)
                eventName.setText(nameString);
                if (event.getDateString().isEmpty()) {
                    activity.findViewById(R.id.event_date_container).setVisibility(View.GONE);
                } else {
                    eventDate.setText(event.getDateString());
                }

                // Show a venue if it is available, otherwise show just the location. If neither is available, hide
                if (!venueString.isEmpty()) {
                    eventVenue.setText(venueString);
                } else if (!locationString.isEmpty()) {
                    eventVenue.setText(locationString);
                } else {
                    eventVenue.setText(R.string.no_location_available);
                    activity.findViewById(R.id.event_venue_container).setVisibility(View.GONE);
                }

                if (showRanks) {
                    topTeamsContainer.setVisibility(View.VISIBLE);
                    topTeams.setText(Html.fromHtml(topTeamsString));
                }

                if (showStats) {
                    topOprsContainer.setVisibility(View.VISIBLE);
                    topOprs.setText(Html.fromHtml(topOprsString));
                }

                // setup social media intents
                // Default to showing the nav arrow in the venue view and the venue view being clickable
                // We need to set these again even though they're defined in XML in case we gain a location
                // or venue on a refresh and we're reusing the same view.
                View eventVenueContainer = view.findViewById(R.id.event_venue_container);
                eventVenueContainer.setFocusable(true);
                eventVenueContainer.setClickable(true);

                if (!venueString.isEmpty()) {
                    // Set the tag to the event venue if it is available
                    eventVenueContainer.setTag("geo:0,0?q=" + venueString.replace(" ", "+"));
                } else if (!locationString.isEmpty()) {
                    // Otherwise, use the location
                    eventVenueContainer.setTag("geo:0,0?q=" + locationString.replace(" ", "+"));
                } else {
                    // If neither location nor venue are available, hide the nav arrow, remove the tag,
                    // and set the view to not clickable so the user cannot interact with it.
                    // It will contain the text "No location available".
                    eventVenueContainer.setTag(null);
                    eventVenueContainer.setFocusable(false);
                    eventVenueContainer.setClickable(false);
                }

                // If the event doesn't have a defined website, default to a google search for the event name
                if(event.getWebsite().isEmpty()) {
                    view.findViewById(R.id.event_website_container).setTag("https://www.google.com/search?q=" + nameString);
                    ((TextView) view.findViewById(R.id.event_website_title)).setText(R.string.find_event_on_google);
                } else {
                    view.findViewById(R.id.event_website_container).setTag(event.getWebsite());
                    ((TextView) view.findViewById(R.id.event_website_title)).setText(R.string.view_event_website);
                }

                view.findViewById(R.id.event_twitter_container).setTag("https://twitter.com/search?q=%23" + eventKey);
                ((TextView) view.findViewById(R.id.event_twitter_title)).setText(activity.getResources().getString(R.string.view_event_twitter, eventKey));

                view.findViewById(R.id.event_youtube_container).setTag("https://www.youtube.com/results?search_query=" + eventKey);
                ((TextView) view.findViewById(R.id.event_youtube_title)).setText(activity.getResources().getString(R.string.view_event_youtube, eventKey));

                view.findViewById(R.id.event_cd_container).setTag("http://www.chiefdelphi.com/media/photos/tags/" + eventKey);

                EventBus.getDefault().post(new EventInfoLoadedEvent(event));
            }

            // Display warning if offline.
            if (c == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show info, since we're done loading the data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);

            if (c == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateEventInfo secondLoad = new PopulateEventInfo(mFragment, requestParams);
                mFragment.updateTask(secondLoad);
                secondLoad.execute(eventKey);
            } else {
                // Show notification if we've refreshed data.
                if (activity != null && mFragment instanceof RefreshListener) {
                    Log.i(Constants.REFRESH_LOG, "Event " + eventKey + " Info refresh complete");
                    activity.notifyRefreshComplete(mFragment);
                }
            }

            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "event info", eventKey);
        }
    }
}
