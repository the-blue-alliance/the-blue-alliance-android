package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;

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

        // If we've specified via a header that we want to force from cache/web, build the
        // proper CacheControl header to send with the requests
        String internalCacheHeader = originalRequest.header(APIv2.TBA_CACHE_HEADER);
        if (internalCacheHeader != null) {
            switch (internalCacheHeader) {
                case APIv2.TBA_CACHE_LOCAL:
                    newRequestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                    break;
                case APIv2.TBA_CACHE_WEB:
                    newRequestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                    break;
            }
            newRequestBuilder.removeHeader(APIv2.TBA_CACHE_HEADER);
        }

        Request newRequest = newRequestBuilder.build();

        return chain.proceed(newRequest);
    }
}