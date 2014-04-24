package com.thebluealliance.androidclient.background;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<Void,String,Void> {

    private Fragment mFragment;
    private Context mContext;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;
    private boolean mIsCurrentlyCompeting = false;

    public PopulateTeamInfo(Context c, Fragment fragment, String teamKey){
        mFragment = fragment;
        mContext = c;
        mTeamKey = teamKey;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //some temp data
        mTeamName = "Teh Chezy Pofs";
        mLocation = "San Jose, CA";
        mFullName = "This name is too long to comfortably fit here";
        mTeamNumber = 254;
        mIsCurrentlyCompeting = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        View view = mFragment.getView();
        ((TextView) view.findViewById(R.id.name)).setText(mTeamName);
        ((TextView) view.findViewById(R.id.location)).setText(mLocation);
        // Tag is used to create an ACTION_VIEW intent for a maps application
        view.findViewById(R.id.location_wrapper).setTag("geo:0,0?q=" + mLocation.replace(" ", "+"));
        view.findViewById(R.id.twitter_button).setTag("twitter://search?q=%23" + mTeamKey);
        view.findViewById(R.id.youtube_button).setTag(String.format("#frc%d OR \"team %d\"", mTeamNumber, mTeamNumber));
        // This string needs to be specially formatted
        SpannableString string = new SpannableString("aka " + mFullName);
        string.setSpan(new TextAppearanceSpan(mContext, R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((TextView) view.findViewById(R.id.full_name)).setText(string);
        if(!mIsCurrentlyCompeting) {
            view.findViewById(R.id.current_event_wrapper).setVisibility(View.GONE);
            view.findViewById(R.id.current_matches_wrapper).setVisibility(View.GONE);
        } else {
            //TODO: populate current event/match fields with the appropriate data
            boolean hasPlayedAtCurrentEvent = true;
            boolean hasNextMatchAtCurrentEvent = true;
            if(hasPlayedAtCurrentEvent) {

            } else {
                // Hide most recent match views, this team has not yet had a match at this competition
                view.findViewById(R.id.most_recent_match).setVisibility(View.GONE);
                view.findViewById(R.id.most_recent_match_details).setVisibility(View.GONE);
            }

            if(hasNextMatchAtCurrentEvent) {
                // Hide the video button in the match details, future matches cannot have videos yet
                View nextMatchDetailsView = view.findViewById(R.id.next_match_details);
                nextMatchDetailsView.findViewById(R.id.match_video).setVisibility(View.INVISIBLE);
            } else {
                // Hide next match views, this team has no more matches at this competition
                view.findViewById(R.id.next_match).setVisibility(View.GONE);
                view.findViewById(R.id.next_match_details).setVisibility(View.GONE);
            }
        }
    }

}
