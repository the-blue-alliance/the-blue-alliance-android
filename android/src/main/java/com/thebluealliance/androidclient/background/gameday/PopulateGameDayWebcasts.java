package com.thebluealliance.androidclient.background.gameday;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.gameday.GamedayWebcastsFragment;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;

/**
 * Created by phil on 3/27/15.
 */
public class PopulateGameDayWebcasts extends AsyncTask<String, Void, APIResponse.CODE> {

    private Activity activity;
    private GamedayWebcastsFragment fragment;
    private ArrayList<ListElement> events;
    private RequestParams requestParams;

    public PopulateGameDayWebcasts(GamedayWebcastsFragment fragment, RequestParams requestParams){
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        events = new ArrayList<>();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        int year = Utilities.getCurrentYear();
        int week = Utilities.getCurrentCompWeek();
        APIResponse<ArrayList<Event>> response;
        try {
            response = DataManager.Events.getSimpleEventsInWeek(activity, year, week, requestParams);
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch current events");
            return APIResponse.CODE.NODATA;
        }

        return response.getCode();
    }
}