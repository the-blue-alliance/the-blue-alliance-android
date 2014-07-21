package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.EventWeekLabelSortComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * File created by phil on 5/21/14.
 */
public class BuildEventWeekTabs extends AsyncTask<Integer, Void, APIResponse.CODE> {

    private EventsByWeekFragment fragment;
    private int year;
    private ArrayList<String> allLabels;
    private static EventWeekLabelSortComparator comparator;

    public BuildEventWeekTabs(EventsByWeekFragment fragment) {
        this.fragment = fragment;

        if(comparator == null){
            comparator = new EventWeekLabelSortComparator();
        }
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        year = params[0];
        try {
            Log.d(Constants.LOG_TAG, "Loading event list...");
            APIResponse<HashMap<String, ArrayList<Event>>> allEvents = DataManager.Events.getEventsByYear(fragment.getActivity(), year, false);
            allLabels = new ArrayList<>();
            allLabels.addAll(allEvents.getData().keySet());
            Collections.sort(allLabels, comparator);
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

        if(fragment != null && fragment.getActivity() != null && !fragment.getActivity().isDestroyed()) {
            if (code != APIResponse.CODE.NODATA && allLabels != null && allLabels.size() > 0) {
                Log.d(Constants.REFRESH_LOG, "Event week tabs data downloaded");
                fragment.updateLabels(allLabels);
                fragment.notifyRefreshComplete(fragment);
            }
        }
    }


}
