package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;

import java.util.Arrays;
import java.util.List;

/**
 * File created by phil on 8/13/14.
 */
public class UpdateMyTBA extends AsyncTask<Short, Void, Void> {

    public static final short UPDATE_FAVORITES = 0, UPDATE_SUBSCRIPTION = 1;

    private Context context;
    private RequestParams requestParams;

    public UpdateMyTBA(Context context, RequestParams requestParams) {
        this.context = context;
        this.requestParams = requestParams;
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
            DataManager.MyTBA.updateUserFavorites(context, requestParams);
        }

        if (toUpdate.contains(UPDATE_SUBSCRIPTION)) {
            DataManager.MyTBA.updateUserSubscriptions(context, requestParams);
        }

        return null;
    }
}
