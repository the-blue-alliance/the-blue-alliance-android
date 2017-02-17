package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.api.ApiConstants;
import com.thebluealliance.androidclient.config.AppConfig;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv3RequestInterceptor implements Interceptor {

    public static final String APIV3_KEY = "apiv3_auth_key";
    private static String sApiKey;

    public APIv3RequestInterceptor(AppConfig config) {
        if (config != null) {
            updateApiKey(config.getString(APIV3_KEY));
        } else {
            TbaLogger.w("Can't get RemoteConfig for TBA Auth Key");
            sApiKey = "";
        }
    }

    public static void updateApiKey(String key) {
        TbaLogger.d("Using TBA Auth Key: " + sApiKey);
        sApiKey = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();
        TbaLogger.d("FETCHING " + url);

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
            .addHeader("X-TBA-Auth-Key", sApiKey)
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