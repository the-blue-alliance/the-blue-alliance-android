package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.Constants;

import retrofit.RequestInterceptor;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv2RequestInterceptor implements RequestInterceptor {
    @Override
    public void intercept(RequestInterceptor.RequestFacade request) {
        request.addHeader("X-TBA-App-Id", Constants.getApiHeader());
    }
}