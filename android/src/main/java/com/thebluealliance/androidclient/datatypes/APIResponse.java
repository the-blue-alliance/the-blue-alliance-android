package com.thebluealliance.androidclient.datatypes;

/**
 * File created by phil on 5/11/14.
 */
public class APIResponse<A> {

    public static enum CODE{
        UPDATED, //data was updated from the API
        CACHED304, //data was found to have not changed (API returned 304-Not-Modified)
        OFFLINECACHE, //client is offline, loaded from local cache
        WEBLOAD, //data was first loaded from the web
        NODATA //nothing! uh ohs
    }

    A data;
    CODE code;

    public APIResponse(A data, CODE code) {
        this.code = code;
        this.data = data;
    }

    public A getData() {
        return data;
    }

    public CODE getCode() {
        return code;
    }
}