package com.thebluealliance.androidclient.background.district;

import android.os.AsyncTask;

import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictBreakdownFragment;

/**
 * File created by phil on 7/26/14.
 */
public class PopulateTeamAtDistrictBreakdown extends AsyncTask<String, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private TeamAtDistrictBreakdownFragment fragment;

    public PopulateTeamAtDistrictBreakdown(TeamAtDistrictBreakdownFragment fragment, boolean forceFromCache){
        super();
        this.forceFromCache = forceFromCache;
        this.fragment = fragment;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        return null;
    }
}
