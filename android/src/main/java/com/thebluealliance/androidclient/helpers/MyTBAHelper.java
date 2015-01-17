package com.thebluealliance.androidclient.helpers;

/**
 * File created by phil on 8/13/14.
 */
public class MyTBAHelper {

    public static String createKey(String userName, String modelKey){
        return userName+":"+modelKey;
    }

    public static String getFavoritePreferenceKey() {
        return "favorite";
    }

}
