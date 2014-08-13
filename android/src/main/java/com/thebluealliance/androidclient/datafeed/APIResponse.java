package com.thebluealliance.androidclient.datafeed;

import java.util.ArrayList;
import java.util.Date;

/**
 * File created by phil on 5/11/14.
 */
public class APIResponse<A> {

    public static enum CODE { /* DO NOT CHANGE ORDER. USED FOR COMPARING (ordered least to most precedence) */
        CACHED304, //data was found to have not changed (API returned 304-Not-Modified)
        WEBLOAD, //data was first loaded from the web
        UPDATED, //data was updated from the API
        OFFLINECACHE, //client is offline, loaded from local cache
        LOCAL, //loaded locally. Could be either CACHED304 or OFFLINECACHE
        ERROR, //HTTP error happened somewhere
        NODATA; //nothing! uh ohs

        public static int compareCodes(CODE one, CODE another) {
            int left, right;
            left = one.ordinal();
            right = another.ordinal();
            return Math.max(left, right);
        }
    }

    A data;
    CODE code;
    String lastUpdate, errorMessage;
    Date lastHit;

    public APIResponse(A data, CODE code, String lastUpdate, Date lastHit) {
        this.lastUpdate = lastUpdate;
        this.data = data;
        this.code = code;
        this.lastHit = lastHit;
        this.errorMessage = "";
    }

    public APIResponse(A data, CODE code, String lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.data = data;
        this.code = code;
        this.lastHit = new Date(); //default to now
        this.errorMessage = "";
    }

    public APIResponse(A data, CODE code) {
        this.code = code;
        this.data = data;
        this.lastUpdate = "";
        this.errorMessage = "";
    }

    public APIResponse(A data, String errorMessage) {
        this.code = CODE.ERROR;
        this.data = data;
        this.lastUpdate = "";
        this.errorMessage = errorMessage;
    }

    public A getData() {
        return data;
    }

    public CODE getCode() {
        return code;
    }

    public APIResponse<A> updateCode(CODE code) {
        this.code = code;
        return this;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public Date getLastHit() {
        return lastHit;
    }

    public void setLastHit(Date lastHit) {
        this.lastHit = lastHit;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String error) {
        errorMessage = error;
    }

    public static CODE mergeCodes(CODE... codes) {
        if (codes.length == 0) return CODE.NODATA;
        CODE merged = CODE.CACHED304; //start with least precedence
        for (CODE code : codes) {
            int newIndex = CODE.compareCodes(merged, code);
            CODE[] values = CODE.values();
            if (newIndex < 0 || newIndex > values.length) continue;
            merged = values[newIndex];
        }
        return merged;
    }

    public static CODE mergeCodes(ArrayList<CODE> codes) {
        if (codes.size() == 0) return CODE.NODATA;
        CODE merged = CODE.CACHED304; //start with least precedence
        for (CODE code : codes) {
            int newIndex = CODE.compareCodes(merged, code);
            CODE[] values = CODE.values();
            if (newIndex < 0 || newIndex > values.length) continue;
            merged = values[newIndex];
        }
        return merged;
    }
}