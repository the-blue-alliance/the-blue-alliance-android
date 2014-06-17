package com.thebluealliance.androidclient;

/**
 * Created by Nathan on 6/17/2014.
 */
public class NfcUris {

    // Formatted event/EVENT_KEY
    public static final String URI_EVENT = "event/%1$s";
    public static final String URI_EVENT_MATCHER = "event/([a-zA-Z0-9]+)";

    // Formatted team/TEAM_KEY
    public static final String URI_TEAM = "team/%1$s";
    public static final String URI_TEAM_MATCHER = "team/([a-zA-Z0-9]+)";

    // Formatted team/TEAM_KEY/YEAR
    public static final String URI_TEAM_IN_YEAR = "team/%1$s/$2$d";
    public static final String URI_TEAM_IN_YEAR_MATCHER = "team/([a-zA-Z0-9]+)/([0-9]+)";
}
