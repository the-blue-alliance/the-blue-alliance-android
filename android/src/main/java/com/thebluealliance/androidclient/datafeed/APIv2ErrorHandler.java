package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * File to handler API Errors
 */
public class APIv2ErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(cause));
        Response response = cause.getResponse();
        if(response != null) {
            TypedByteArray data = (TypedByteArray) (response.getBody());
            byte[] bytes = data.getBytes();
            System.out.println(new String(bytes));
        }
        return cause;
    }
}
