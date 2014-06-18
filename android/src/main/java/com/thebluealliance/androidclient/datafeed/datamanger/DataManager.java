package com.thebluealliance.androidclient.datafeed.datamanger;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    public static class NoDataException extends Exception {
        public NoDataException(String message) {
            super(message);
        }

        public NoDataException(String message, Throwable t) {
            super(message, t);
        }
    }
}