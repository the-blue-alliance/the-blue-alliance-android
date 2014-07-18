package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.comparators.EventWeekLabelSortComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * File created by phil on 5/21/14.
 */
public class DownloadEventList extends AsyncTask<Integer, Void, APIResponse.CODE> {

    private Context c;
    private EventsByWeekFragmentPagerAdapter adapter;
    private int year;
    private ArrayList<String> allLabels;
    private static EventWeekLabelSortComparator comparator;

    public DownloadEventList(Context c, EventsByWeekFragmentPagerAdapter adapter) {
        this.c = c;
        this.adapter = adapter;

        if(comparator == null){
            comparator = new EventWeekLabelSortComparator();
        }
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        year = params[0];
        try {
            Log.d(Constants.LOG_TAG, "Loading event list...");
            APIResponse<HashMap<String, ArrayList<Event>>> allEvents = DataManager.Events.getEventsByYear(c, year, false);
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

        if(code != APIResponse.CODE.NODATA && allLabels != null && allLabels.size() > 0) {
            adapter.setLabels(allLabels);
        }
    }
}
