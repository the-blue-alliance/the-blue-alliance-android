package com.thebluealliance.androidclient;

public final class ShareUris {

    private ShareUris() {
        // unused
    }

    public static final String BASE_URI = "https://thebluealliance.com";

    // Format with event key
    public static final String URI_EVENT = BASE_URI + "/event/%1$s";

    public static final String URI_TEAM_LIST = BASE_URI + "/teams";
    public static final String URI_MYTBA = BASE_URI + "/account/mytba";

    // Format with year
    public static final String URI_EVENT_LIST = BASE_URI + "/events/%1$d";

    // Format with district short, year
    public static final String URI_DISTRICT_EVENTS = BASE_URI + "/events/%1$s/%2$d";

    // Nothing on site for this yet...
    public static final String URI_TEAM_DISTRICT = URI_DISTRICT_EVENTS;

    // Format with team number, year
    public static final String URI_TEAM = BASE_URI + "/team/%1$d/%2$d";

    // Format with team number, year, event key
    public static final String URI_TEAM_AT_EVENT = URI_TEAM + "#%3$s";

    // Format with match key
    public static final String URI_MATCH = BASE_URI + "/match/%1$s";

    public static final String URI_GAMEDAY = BASE_URI + "/gameday";
}
