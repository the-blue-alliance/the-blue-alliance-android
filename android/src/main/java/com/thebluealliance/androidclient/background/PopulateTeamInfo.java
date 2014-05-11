package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<String, Void, Void> {

    private Fragment mFragment;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;
    private boolean mIsCurrentlyCompeting = false;

    public PopulateTeamInfo(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    protected Void doInBackground(String... params) {
        mTeamKey = params[0];
        try {
            Long start = System.nanoTime();
            Team team = DataManager.getTeam(mFragment.getActivity(), mTeamKey);
            Long end = System.nanoTime();
            Log.d("doInBackground", "Total time to load team: " + (end - start));
            mTeamName = team.getNickname();
            mLocation = team.getLocation();
            mFullName = team.getFullName();
            mTeamNumber = team.getTeamNumber();
            // TODO: determine if the team actually is competing
            mIsCurrentlyCompeting = false;
            return null;
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            //some temp data
            mTeamName = "Teh Chezy Pofs";
            mLocation = "San Jose, CA";
            mFullName = "This name is too long to comfortably fit here";
            mTeamNumber = 254;
            mIsCurrentlyCompeting = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        View view = mFragment.getView();
        if (view != null) {
            TextView teamName = ((TextView) view.findViewById(R.id.team_name));
            if (mTeamName.isEmpty()) {
                teamName.setText("Team " + mTeamNumber);
            } else {
                teamName.setText(mTeamName);
            }
            ((TextView) view.findViewById(R.id.team_location)).setText(mLocation);
            // Tag is used to create an ACTION_VIEW intent for a maps application
            view.findViewById(R.id.team_location_container).setTag("geo:0,0?q=" + mLocation.replace(" ", "+"));
            view.findViewById(R.id.team_twitter_button).setTag("twitter://search?q=%23" + mTeamKey);
            view.findViewById(R.id.team_youtube_button).setTag(String.format("#frc%d OR \"team %d\"", mTeamNumber, mTeamNumber));
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
                //TODO: populate current event/match fields with the appropriate data
                boolean hasPlayedAtCurrentEvent = true;
                boolean hasNextMatchAtCurrentEvent = true;
                if (hasPlayedAtCurrentEvent) {

                } else {
                    // Hide most recent match views, this team has not yet had a match at this competition
                    view.findViewById(R.id.team_most_recent_match_label).setVisibility(View.GONE);
                    view.findViewById(R.id.team_most_recent_match_details).setVisibility(View.GONE);
                }

                if (hasNextMatchAtCurrentEvent) {
                    // Hide the video button in the match details, future matches cannot have videos yet
                    View nextMatchDetailsView = view.findViewById(R.id.team_next_match_details);
                    nextMatchDetailsView.findViewById(R.id.match_video).setVisibility(View.INVISIBLE);
                } else {
                    // Hide next match views, this team has no more matches at this competition
                    view.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
                    view.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);
                }
            }
        }
    }


}
