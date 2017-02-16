package com.thebluealliance.androidclient.datafeed;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.api.ApiConstants;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv3RequestInterceptor implements Interceptor {

    private final String mApiKey;

    public APIv3RequestInterceptor(@Nullable FirebaseRemoteConfig config) {
        if (config != null) {
            mApiKey = config.getString("apiv3_auth_key");
        } else {
            mApiKey = "";
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();
        TbaLogger.d("FETCHING " + url);

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
            .addHeader("X-TBA-Auth-Key", mApiKey)
            .addHeader("User-Agent", Constants.getUserAgent() + " (gzip)");  // Include 'gzip' to force App Engine to serve gzipped content. https://cloud.google.com/appengine/kb/#compression

        // If we've specified via a header that we want to force from cache/web, build the
        // proper CacheControl header to send with the requests
        String internalCacheHeader = originalRequest.header(ApiConstants.TBA_CACHE_HEADER);
        if (internalCacheHeader != null) {
            switch (internalCacheHeader) {
                case ApiConstants.TBA_CACHE_LOCAL:
                    newRequestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                    break;
                case ApiConstants.TBA_CACHE_WEB:
                    newRequestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                    break;
            }
            newRequestBuilder.removeHeader(ApiConstants.TBA_CACHE_HEADER);
        }

        Request newRequest = newRequestBuilder.build();

        return chain.proceed(newRequest);
    }
}