package com.thebluealliance.androidclient.datafeed;

/**
 * Created by phil on 12/1/14.
 */
public class RequestParams {
    public boolean cacheLocally;
    public boolean forceFromCache;
    public boolean forceFromWeb;

    public RequestParams(){
        cacheLocally = true;
        forceFromCache = false;
        forceFromWeb = false;
    }

    public RequestParams(boolean forceFromCache){
        this();
        this.forceFromCache = forceFromCache;
    }

    public RequestParams(boolean forceFromCache, boolean forceFromWeb) {
        this();
        this.forceFromCache = forceFromCache;
        this.forceFromWeb = forceFromWeb;
    }

    public RequestParams(boolean cacheLocally, boolean forceFromCache, boolean forceFromWeb) {
        this();
        this.cacheLocally = cacheLocally;
        this.forceFromCache = forceFromCache;
        this.forceFromWeb = forceFromWeb;
    }
}
