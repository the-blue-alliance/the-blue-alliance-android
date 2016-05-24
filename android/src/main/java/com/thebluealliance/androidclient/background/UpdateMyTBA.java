package com.thebluealliance.androidclient.background;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class UpdateMyTBA extends AsyncTask<Short, Void, Void> {

    public static final short UPDATE_FAVORITES = 0, UPDATE_SUBSCRIPTION = 1;

    private final MyTbaDatafeed mDatafeed;

    public UpdateMyTBA(MyTbaDatafeed datafeed) {
        mDatafeed = datafeed;
    }

    @Override
    protected Void doInBackground(Short... params) {

        List<Short> toUpdate;
        if (params.length > 0) {
            toUpdate = Arrays.asList(params);
        } else {
            toUpdate = Arrays.asList(UPDATE_FAVORITES, UPDATE_SUBSCRIPTION);
        }

        Log.d(Constants.LOG_TAG, "Updating myTBA data");

        /**
         * These update routines don't need to do anything with the data,
         * so we can ignore the return values of the DataManager calls
         */

        if (toUpdate.contains(UPDATE_FAVORITES)) {
            mDatafeed.updateUserFavorites();
        }

        if (toUpdate.contains(UPDATE_SUBSCRIPTION)) {
            mDatafeed.updateUserSubscriptions();
        }

        return null;
    }
}
