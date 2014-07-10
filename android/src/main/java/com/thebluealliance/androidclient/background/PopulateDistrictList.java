package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;

import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.fragments.DistrictListFragment;

/**
 * Created by phil on 7/10/14.
 */
public class PopulateDistrictList extends AsyncTask<Integer, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private DistrictListFragment fragment;

    public PopulateDistrictList(DistrictListFragment fragment, boolean forceFromCache){
        super();
        this.fragment = fragment;
        this.forceFromCache  = forceFromCache;
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        return null;
    }
}
