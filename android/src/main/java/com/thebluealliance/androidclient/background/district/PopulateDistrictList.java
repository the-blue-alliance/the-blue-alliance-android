package com.thebluealliance.androidclient.background.district;

import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.district.DistrictListFragment;
import com.thebluealliance.androidclient.models.District;

import java.util.ArrayList;

/**
 * Created by phil on 7/24/14.
 */
public class PopulateDistrictList extends AsyncTask<Integer, Void, APIResponse.CODE>{

    private boolean forceFromCache;
    private DistrictListFragment fragment;
    private RefreshableHostActivity activity;
    private int year;

    public PopulateDistrictList(DistrictListFragment fragment, boolean forceFromCache){
        this.forceFromCache = forceFromCache;
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        year = params[0];

        try {
            APIResponse<ArrayList<District>> response = DataManager.Districts.getDistrictsInYear(activity, year, forceFromCache);

            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to get district list for "+year);
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
    }
}
