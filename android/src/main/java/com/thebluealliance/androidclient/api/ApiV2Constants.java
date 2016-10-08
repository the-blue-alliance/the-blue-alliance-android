package com.thebluealliance.androidclient.api;

public class ApiV2Constants {
    public static final String DEV_TBA_PREF_KEY = "tba_host";
    public static final String TBA_URL = "https://www.thebluealliance.com/";
    /**
     * Here's how we can force data to be loaded from either the cache or the web
     * We pass a custom header (that'll be removed in
     * {@link com.thebluealliance.androidclient.datafeed.APIv2RequestInterceptor}) which
     * we use to construct the proper {@link okhttp3.CacheControl} to be used with the request
     */
    public static final String TBA_CACHE_HEADER = "X-TBA-Cache";
    public static final String TBA_CACHE_WEB = "web";
    public static final String TBA_CACHE_LOCAL = "local";
}
