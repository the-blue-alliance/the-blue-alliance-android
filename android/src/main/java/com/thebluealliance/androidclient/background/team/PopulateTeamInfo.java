package com.thebluealliance.androidclient.background.team;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
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
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;
    private String mTeamWebsite;
    private SimpleEvent mCurrentEvent;
    private boolean mIsCurrentlyCompeting;

    public PopulateTeamInfo(Fragment fragment) {
        mFragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        mTeamKey = params[0];
        try {
            Long start = System.nanoTime();
            APIResponse<Team> response = DataManager.getTeam(activity, mTeamKey);
            Team team = response.getData();
            Long end = System.nanoTime();
            Log.d("doInBackground", "Total time to load team: " + (end - start));
            mTeamName = team.getNickname();
            mLocation = team.getLocation();
            mFullName = team.getFullName();
            mTeamWebsite = team.getWebsite();
            mTeamNumber = team.getTeamNumber();
            mCurrentEvent = team.getCurrentEvent();
            mIsCurrentlyCompeting = mCurrentEvent != null;
            return response.getCode();
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
            view.findViewById(R.id.team_youtube_button).setTag("https://www.youtube.com/results?search_query="+mTeamKey);
            view.findViewById(R.id.team_cd_button).setTag("http://www.chiefdelphi.com/media/photos/tags/"+mTeamKey);
            view.findViewById(R.id.team_website_button).setTag(!mTeamWebsite.isEmpty()?mTeamWebsite:"https://www.google.com/search?q="+mTeamKey);
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
                ((TextView)view.findViewById(R.id.team_current_event_name)).setText(mCurrentEvent.getEventName());
                ArrayList<Match> matches;
                try {
                    matches = DataManager.getMatchList(activity, mCurrentEvent.getEventKey()).getData();
                } catch (DataManager.NoDataException e) {
                    Log.w(Constants.LOG_TAG, "unable to fetch event data");
                    return;
                }
                Collections.sort(matches, new MatchSortByPlayOrderComparator());
                Match lastMatch = Match.getLastMatchPlayed(matches);
                Match nextMatch = Match.getNextMatchPlayed(matches);
                if (lastMatch != null) {
                    ((LinearLayout)view.findViewById(R.id.team_most_recent_match_details)).addView(lastMatch.render().getView(activity, inflater, null));
                } else {
                    // Hide most recent match views, this team has not yet had a match at this competition
                    view.findViewById(R.id.team_most_recent_match_label).setVisibility(View.GONE);
                    view.findViewById(R.id.team_most_recent_match_details).setVisibility(View.GONE);
                }

                if (nextMatch != null) {
                    ((LinearLayout)view.findViewById(R.id.team_next_match_details)).addView(nextMatch.render().getView(activity, inflater, null));
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
    }


}
