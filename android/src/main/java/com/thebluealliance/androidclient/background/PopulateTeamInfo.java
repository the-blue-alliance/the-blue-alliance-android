package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamInfo extends AsyncTask<String,String,Void> {

    private Fragment mFragment;
    private String mTeamName;
    private int mTeamNumber;
    private String mLocation;
    private String mFullName;
    private String mTeamKey;

    public PopulateTeamInfo(Fragment fragment, String teamKey){
        mFragment = fragment;
        mTeamKey = teamKey;
    }

    @Override
    protected Void doInBackground(String... params) {
        //some more temp data
        mTeamName = "Teh Chezy Pofs";
        mLocation = "San Jose, CA";
        mFullName = "This name is too long to comfortably fit here";
        mTeamNumber = 254;
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        View view = mFragment.getView();
        ((TextView) view.findViewById(R.id.name)).setText(mTeamName);
        ((TextView) view.findViewById(R.id.location)).setText("from " + mLocation);
        // Tag is used to create an ACTION_VIEW intent for a maps application
        view.findViewById(R.id.location_wrapper).setTag("geo:0,0?q=" + mLocation.replace(" ", "+"));
        view.findViewById(R.id.twitter_button).setTag("twitter://search?q=%23" + mTeamKey);
        view.findViewById(R.id.youtube_button).setTag(String.format("#frc%d OR \"team %d\"", mTeamNumber, mTeamNumber));
        ((TextView) view.findViewById(R.id.full_name)).setText("aka " + mFullName);
    }

}
