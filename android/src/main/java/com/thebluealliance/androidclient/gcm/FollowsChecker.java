package com.thebluealliance.androidclient.gcm;

import android.content.Context;

/**
 * Methods for checking the user's subscriptions and favorites.
 */
public interface FollowsChecker {
    /**
     * Checks if the user follows (subscribes to or favorites) the given Team or Team@Event for this
     * notification type.
     *
     * @param teamNumber the team number without the "frc" prefix, e.g. "111" or "111B".
     * @param matchKey the event_match key, e.g. "2014calb_qm17", for extracting the event key.
     * @param notificationType one of NotificationTypes.UPCOMING_MATCH, MATCH_SCORE, ...
     */
    boolean followsTeam(Context context, String teamNumber, String matchKey,
                        String notificationType);

    // TODO: followsEvent()
}
