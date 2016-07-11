package com.thebluealliance.androidclient;

public final class NfcUris {

    private NfcUris() {
        // unused
    }

    // Formatted as event/EVENT_KEY
    public static final String URI_EVENT = "event/%1$s";
    public static final String URI_EVENT_MATCHER = "event/([a-zA-Z0-9]+)";

    public static final String URI_DISTRICT = "events/%1$s";
    public static final String URI_TEAM_DISTRICT = "events/%1$s/team/%2$s";

    // Formatted as team/TEAM_KEY
    public static final String URI_TEAM = "team/%1$s";
    public static final String URI_TEAM_MATCHER = "team/([a-zA-Z0-9]+)";

    // Formatted as team/TEAM_KEY/YEAR
    public static final String URI_TEAM_IN_YEAR = "team/%1$s/%2$d";
    public static final String URI_TEAM_IN_YEAR_MATCHER = "team/([a-zA-Z0-9]+)/([0-9]+)";

    // Formatted as event/EVENT_KEY/team/TEAM_KEY
    public static final String URI_TEAM_AT_EVENT = "event/%1$s/team/%2$s";
    public static final String URI_TEAM_AT_EVENT_MATCHER = "event/([a-zA-Z0-9]+)/team/([a-zA-Z0-9]+)";

    // Formatted as match/MATCH_KEY
    public static final String URI_MATCH = "match/%1$s";
    public static final String URI_MATCH_MATCHER = "match/([a-zA-Z0-9_]+)";

    public static final String URI_GAMEDAY = "gameday";
}
