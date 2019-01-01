package com.thebluealliance.androidclient.datafeed;

import android.content.SharedPreferences;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.api.ApiConstants;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to intercept Retrofit requests and add appropriate API headers
 */
public class APIv3RequestInterceptor implements Interceptor {

    private static final String APIV3_KEY = "apiv3_auth_key";
    private static final String APIV3_CACHE_BUST = "apiv3_edge_cache_bust";

    private final SharedPreferences mPrefs;

    public APIv3RequestInterceptor(SharedPreferences sharedPreferences) {
        mPrefs = sharedPreferences;
    }

    public static void updateApiKeys(FirebaseRemoteConfig config, SharedPreferences prefs) {
        String cacheBust = config.getString(APIV3_CACHE_BUST);
        prefs.edit()
                .putString(APIV3_KEY, config.getString(APIV3_KEY))
                .putString(APIV3_CACHE_BUST, cacheBust != null ? cacheBust : "")
                .apply();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();
        if (!url.contains("thebluealliance.com/api/v3")) {
            return chain.proceed(originalRequest);
        }

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
            .addHeader("User-Agent", Constants.getUserAgent() + " (gzip)");  // Include 'gzip' to force App Engine to serve gzipped content. https://cloud.google.com/appengine/kb/#compression


        String apiKey = mPrefs.getString(APIV3_KEY, "");
        newRequestBuilder.addHeader("X-TBA-Auth-Key", apiKey);

        // Configurable Edge-Cache busting
        String cacheBust = mPrefs.getString(APIV3_CACHE_BUST, "");
        if (!cacheBust.isEmpty()) {
            url += "?cacheBust=" + cacheBust;
            newRequestBuilder.url(url);
        }
        TbaLogger.d("FETCHING " + url);

        // If we've specified via a header that we want to force from cache/web, build the
        // proper CacheControl header to send with the requests
        String internalCacheHeader = originalRequest.header(ApiConstants.TBA_CACHE_HEADER);
        if (internalCacheHeader != null) {
            switch (internalCacheHeader) {
                case ApiConstants.TBA_CACHE_LOCAL:
                    newRequestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                    break;
                case ApiConstants.TBA_CACHE_WEB:
                    TbaLogger.d("Getting from WEB");
                    newRequestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                    break;
            }
            newRequestBuilder.removeHeader(ApiConstants.TBA_CACHE_HEADER);
        }

        Request newRequest = newRequestBuilder.build();

        Response response = chain.proceed(newRequest);

        if (!response.isSuccessful()) {
            TbaLogger.w("NET ERROR " + url + " " + response.code() + " " + response.message());
        }
        return response;
    }
}