package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;

import java.io.IOException;

import javax.inject.Inject;

import retrofit.RequestInterceptor;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv2RequestInterceptor implements RequestInterceptor, Interceptor {

    @Inject Database mDb;

    /**
     * If you just need to add the header via Retrofit, use this method.
     * NOT CALLED OUTSIDE OF TESTS (will not do cacheing, etc)
     */
    @Override
    public void intercept(RequestInterceptor.RequestFacade request) {
        request.addHeader("X-TBA-App-Id", Constants.getApiHeader());
    }

    /**
     * For more advanced interception, use this
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.urlString();
        Log.d(Constants.LOG_TAG, "FETCHING " + url);

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
          .addHeader("X-TBA-App-Id", Constants.getApiHeader());

        /* TODO Implement setting If-Modified-Since header here
        APIResponse<String> cachedResponse = mDb.getResponseTable().getResponseIfExists(url);
        if (cachedResponse != null) {
            newRequestBuilder.addHeader("If-Modified-Since", cachedResponse.getLastUpdate());
        }
        */

        Request newRequest = newRequestBuilder.build();

        return chain.proceed(newRequest);

        // TODO Update Response in database
    }
}