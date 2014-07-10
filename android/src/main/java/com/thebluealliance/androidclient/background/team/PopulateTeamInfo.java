package com.thebluealliance.androidclient.background.team;

import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private TeamInfoFragment mFragment;
    private RefreshableHostActivity activity;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;
    private String mTeamWebsite;
    private String mEventName;
    private Event mCurrentEvent;
    private ArrayList<Match> matches;
    private boolean mIsCurrentlyCompeting, forceFromCache;

    public PopulateTeamInfo(TeamInfoFragment fragment, boolean forceFromCache) {
        mFragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mTeamKey = params[0];
        try {
            Long start = System.nanoTime();
            APIResponse<Team> teamResponse = DataManager.Teams.getTeam(activity, mTeamKey, forceFromCache);
            APIResponse<Event> currentEventResponse = DataManager.Teams.getCurrentEventForTeam(activity, mTeamKey, forceFromCache);

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            Team team = teamResponse.getData();
            Long end = System.nanoTime();
            Log.d("doInBackground", "Total time to load team: " + (end - start));
            try {
                mTeamName = team.getNickname();
                mLocation = team.getLocation();
                mFullName = team.getFullName();
                mTeamWebsite = team.getWebsite();
                mTeamNumber = team.getTeamNumber();

                mCurrentEvent = currentEventResponse.getData();
                if (mCurrentEvent != null) {
                    mEventName = mCurrentEvent.getEventName();
                }
                mIsCurrentlyCompeting = mCurrentEvent != null;
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
                Log.e(Constants.LOG_TAG, "Can't load team parameters");
                return APIResponse.CODE.NODATA;
            }

            APIResponse<ArrayList<Match>> eventResponse = new APIResponse<>(null, APIResponse.CODE.CACHED304);
            if (mIsCurrentlyCompeting) {
                try {
                    eventResponse = DataManager.Events.getMatchList(activity, mCurrentEvent.getEventKey(), forceFromCache);
                    matches = eventResponse.getData();
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to fetch event data");
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Unable to get event key");
                }
            }

            return APIResponse.mergeCodes(teamResponse.getCode(), eventResponse.getCode());
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load team info");
            //some temp data
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        View view = mFragment.getView();
        LayoutInflater inflater = activity.getLayoutInflater();
        if (view != null && code != APIResponse.CODE.NODATA) {
            TextView teamName = ((TextView) view.findViewById(R.id.team_name));
            if (mTeamName.isEmpty()) {
                teamName.setText("Team " + mTeamNumber);
            } else {
                teamName.setText(mTeamName);
            }
            ((TextView) view.findViewById(R.id.team_location)).setText(mLocation);
            // Tag is used to create an ACTION_VIEW intent for a maps application
            view.findViewById(R.id.team_location_container).setTag("geo:0,0?q=" + mLocation.replace(" ", "+"));
            view.findViewById(R.id.team_twitter_button).setTag("https://twitter.com/search?q=%23" + mTeamKey);
            view.findViewById(R.id.team_youtube_button).setTag("https://www.youtube.com/results?search_query=" + mTeamKey);
            view.findViewById(R.id.team_cd_button).setTag("http://www.chiefdelphi.com/media/photos/tags/" + mTeamKey);
            view.findViewById(R.id.team_website_button).setTag(!mTeamWebsite.isEmpty() ? mTeamWebsite : "https://www.google.com/search?q=" + mTeamKey);
            if (mFullName.isEmpty()) {
                // No full name specified, hide the view
                view.findViewById(R.id.team_full_name_container).setVisibility(View.GONE);
            } else {
                // This string needs to be specially formatted
                SpannableString string = new SpannableString("aka " + mFullName);
                string.setSpan(new TextAppearanceSpan(mFragment.getActivity(), R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ((TextView) view.findViewById(R.id.team_full_name)).setText(string);
            }
            if (!mIsCurrentlyCompeting) {
                view.findViewById(R.id.team_current_event_container).setVisibility(View.GONE);
                view.findViewById(R.id.team_current_matches_container).setVisibility(View.GONE);
            } else {
                ((TextView) view.findViewById(R.id.team_current_event_name)).setText(mEventName);

                Collections.sort(matches, new MatchSortByPlayOrderComparator());
                Match lastMatch = null;
                Match nextMatch = null;
                try {
                    lastMatch = MatchHelper.getLastMatchPlayed(matches);
                    if (lastMatch != null) {
                        lastMatch.setSelectedTeam(mTeamKey);
                    }
                    nextMatch = MatchHelper.getNextMatchPlayed(matches);
                    if (nextMatch != null) {
                        nextMatch.setSelectedTeam(mTeamKey);
                    }
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Can't get next/last match. Missing fields" +
                            Arrays.toString(e.getStackTrace()));
                }
                if (lastMatch == null && nextMatch == null) {
                    // No matches found, aka not competing. Hide the matches container.
                    view.findViewById(R.id.team_current_matches_container).setVisibility(View.GONE);
                }
                if (lastMatch != null) {
                    // If this is a second refresh, the container could possibly have a match view in it already.
                    // We''l clear the container and add the match again.
                    LinearLayout mostRecentMatch = (LinearLayout) view.findViewById(R.id.team_most_recent_match_details);
                    mostRecentMatch.removeAllViews();
                    mostRecentMatch.addView(lastMatch.render().getView(activity, inflater, null));
                } else {
                    // Hide most recent match views, this team has not yet had a match at this competition
                    view.findViewById(R.id.team_most_recent_match_label).setVisibility(View.GONE);
                    view.findViewById(R.id.team_most_recent_match_details).setVisibility(View.GONE);
                }

                if (nextMatch != null) {
                    // If this is a second refresh, the container could possibly have a match view in it already.
                    // We''l clear the container and add the match again.
                    LinearLayout nextMatchContainer = (LinearLayout) view.findViewById(R.id.team_next_match_details);
                    nextMatchContainer.removeAllViews();
                    nextMatchContainer.addView(nextMatch.render().getView(activity, inflater, null));
                } else {
                    // Hide next match views, this team has no more matches at this competition
                    view.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
                    view.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);
                }
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                ((RefreshableHostActivity) mFragment.getActivity()).showWarningMessage(mFragment.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.team_info_container).setVisibility(View.VISIBLE);
        }

        if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            PopulateTeamInfo secondLoad = new PopulateTeamInfo(mFragment, false);
            mFragment.updateTask(secondLoad);
            secondLoad.execute(mTeamKey);
        } else {
            // Show notification if we've refreshed data.
            Log.i(Constants.REFRESH_LOG, "Team " + mTeamKey + " info refresh complete");
            if (mFragment instanceof RefreshListener) {
                activity.notifyRefreshComplete(mFragment);
            }
        }
    }


}
