package com.thebluealliance.androidclient.helpers;

import android.support.annotation.Nullable;

/**
 * Helper class used to verify team keys.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 *         <p>
 *         File created by phil on 6/15/14.
 */
public class TeamHelper {
    /**
     * Checks if a team key is valid.
     *
     * @param key the team key to check
     * @return true if key is valid
     */
    public static boolean validateTeamKey(@Nullable String key) {
        return !(key == null || key.isEmpty()) && key.matches("^frc\\d{1,4}$");
    }

    /**
     * Checks if a multi-team key is valid.
     * <p>
     * In this context, a multi-team key is for a team that has two or more robots competing,
     * usually in an offseason event, and thus needs one (and ONLY one) extra letter attached to
     * the
     * end to differentiate one from the others (e.g. 904 & 904B, 1973 & 1973D)
     *
     * @param key the team key to check
     * @return true if key is valid
     */
    public static boolean validateMultiTeamKey(@Nullable String key) {
        return !(key == null || key.isEmpty()) && key.matches("^frc\\d{1,4}[a-zA-Z]$");
    }

    /**
     * Extract the team number from a given key
     *
     * @param key A team key. Assumed to be valid
     * @return the team number in the key, -1 if error
     */
    public static int getTeamNumber(@Nullable String key) {
        if (key == null) return -1;
        return Integer.parseInt(key.substring(3));
    }
}