package com.thebluealliance.androidclient.gcm.notifications;

/**
 * File created by phil on 8/17/14.
 */
public class NotificationTypes {
    public static final String
            UPCOMING_MATCH = "upcoming_match",
            MATCH_SCORE = "match_score",
            LEVEL_STARTING = "starting_comp_level",
            ALLIANCE_SELECTION = "alliance_selection",
            AWARDS = "awards_posted",
            MEDIA_POSTED = "media_posted",
            DISTRICT_POINTS_UPDATED = "district_points_updated",
            SCHEDULE_UPDATED = "schedule_updated",
            FINAL_RESULTS = "final_results",
            PING = "ping",
            BROADCAST = "broadcast",

            UPDATE_FAVORITES = "update_favorites",
            UPDATE_SUBSCRIPTIONS = "update_subscriptions",
            SUMMARY = "summary";

    public static String getDisplayName(String notificationType){
        switch (notificationType){
            case UPCOMING_MATCH:            return "Upcoming Matches";
            case MATCH_SCORE:               return "Match Scores";
            case LEVEL_STARTING:            return "Competition Level Starting";
            case ALLIANCE_SELECTION:        return "Alliance Selections";
            case AWARDS:                    return "Awards Posted";
            case MEDIA_POSTED:              return "Media Posted";
            case DISTRICT_POINTS_UPDATED:   return "Points Updated";
            case SCHEDULE_UPDATED:           return "Match Schedule Posted";
            case FINAL_RESULTS:             return "Final Results";
            default:                        return "";
        }
    }
}
