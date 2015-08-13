package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.Constants;

import java.io.IOException;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv2RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.urlString();
        Log.d(Constants.LOG_TAG, "FETCHING " + url);

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
          .addHeader("X-TBA-App-Id", Constants.getApiHeader());

        Request newRequest = newRequestBuilder.build();

        return chain.proceed(newRequest);
    }
}