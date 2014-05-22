package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;

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
            return DataManager.getEventsByYear(c, params[0]).getData().keySet();
        } catch (DataManager.NoDataException e) {
            return new HashSet<>();
        }
    }
}
