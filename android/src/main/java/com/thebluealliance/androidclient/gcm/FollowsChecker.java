package com.thebluealliance.androidclient.gcm;

import android.content.Context;

/**
 * Methods for checking the user's subscriptions and favorites.
 */
public interface FollowsChecker {
    /**
     * Checks if the user subscribes to the given team.
     *
     * @param teamNumber the team number without the "frc" prefix.
     * @param notificationType one of NotificationTypes.UPCOMING_MATCH, MATCH_SCORE, ...
     */
    boolean followsTeam(Context context, String teamNumber, String notificationType);

    // TODO: followsEvent()
}
