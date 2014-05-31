package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.DataManager;

import java.util.HashSet;
import java.util.Set;

/**
 * File created by phil on 5/21/14.
 */
public class DownloadEventList extends AsyncTask<Integer, Void, Set<String>> {

    private Context c;

    public DownloadEventList(Context c){
        this.c = c;
    }

    @Override
    protected Set<String> doInBackground(Integer... params) {
        try {
            Log.d(Constants.LOG_TAG, "Loading event list...");
            return DataManager.getSimpleEventsForYear(c, params[0]).getData().keySet();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            Log.d(Constants.LOG_TAG, "Loading failed!");
            return new HashSet<>();
        }
    }
}
