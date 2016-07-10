package com.thebluealliance.androidclient.gcm;

public enum GcmMessageType {
    REGISTRATION,
    UPCOMING_MATCH,
    MATCH_SCORE,
    ALLIANCE_SELECTION,
    LEVEL_STARTING,
    SUGGEST_MEDIA;

    public String toString() {
        switch (this) {
            case REGISTRATION:
                return "registration";
            case UPCOMING_MATCH:
                return "upcoming_match";
            case MATCH_SCORE:
                return "match_score";
            case ALLIANCE_SELECTION:
                return "alliance_selection";
            case LEVEL_STARTING:
                return "starting_comp_level";
            case SUGGEST_MEDIA:
                return "suggest_media";
        }
        return "";
    }
}
