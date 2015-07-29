package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.EventWeekComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * File created by phil on 5/21/14.
 */
public class BuildEventWeekTabs extends AsyncTask<Integer, Void, APIResponse.CODE> {

    private EventsByWeekFragment fragment;
    private int year;
    private ArrayList<String> allLabels;
    private static EventWeekComparator comparator;
    private long startTime;

    public BuildEventWeekTabs(EventsByWeekFragment fragment) {
        this.fragment = fragment;

        if (comparator == null) {
            comparator = new EventWeekComparator();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        year = params[0];
        try {
            Log.d(Constants.LOG_TAG, "Loading event list...");
            APIResponse<HashMap<String, List<Event>>> allEvents = DataManager.Events.getEventsByYear(fragment.getActivity(), year, new RequestParams());
            allLabels = new ArrayList<>();
            allLabels.addAll(allEvents.getData().keySet());
            //Collections.sort(allLabels, comparator);
            return allEvents.getCode();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            Log.d(Constants.LOG_TAG, "Loading failed!");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        if (fragment != null && fragment.getActivity() != null) {
            if (code != APIResponse.CODE.NODATA && allLabels != null && allLabels.size() > 0) {
                Log.d(Constants.REFRESH_LOG, "Event week tabs data downloaded");
                //fragment.updateLabels(allLabels);
            }
            AnalyticsHelper.sendTimingUpdate(fragment.getActivity(), System.currentTimeMillis() - startTime, "build week tabs", "");
        }
    }


}
