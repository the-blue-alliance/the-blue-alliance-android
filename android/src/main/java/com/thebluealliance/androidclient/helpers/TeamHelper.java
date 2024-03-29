package com.thebluealliance.androidclient.helpers;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class used to verify team keys.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 */
public final class TeamHelper {

    private TeamHelper() {
        // unused
    }

    /**
     * Checks if a team key is valid.
     *
     * @param key the team key to check
     * @return true if key is valid
     */
    public static boolean validateTeamKey(@Nullable String key) {
        return !(key == null || key.isEmpty()) && key.matches("^frc\\d{1,5}$");
    }

    /**
     * Checks if a multi-team key is valid.
     * <p>
     * In this context, a multi-team key is for a team that has two or more robots competing,
     * usually in an offseason event, and thus needs one (and ONLY one) extra letter attached to the
     * end to differentiate one from the others (e.g. 904 & 904B, 1973 & 1973D)
     *
     * @param key the team key to check
     * @return true if key is valid
     */
    public static boolean validateMultiTeamKey(@Nullable String key) {
        return !(key == null || key.isEmpty()) && key.matches("^frc\\d{1,5}[a-zA-Z]$");
    }

    /**
     * Strips off any multi-team key letter suffix to get a valid base team key.
     */
    public static String baseTeamKey(@Nullable String teamKey) {
        if (validateMultiTeamKey(teamKey)) {
            return teamKey.substring(0, teamKey.length() - 1);
        }
        return teamKey;
    }

    /**
     * Extract the team number from a given key
     * @param key A team key. Assumed to be valid
     * @return the team number in the key, -1 if error
     */
    public static int getTeamNumber(@Nullable String key) {
        if (key == null) return -1;

        Matcher teamNumberMatcher = Pattern.compile("^frc(\\d{1,5})[a-zA-Z]?$").matcher(key);

        // 0th group is the full matching string, 1st is our capture group for the number
        // The 0th group is not included in groupCount()
        if (!teamNumberMatcher.matches() || teamNumberMatcher.groupCount() < 1) return -1;
        String teamNumberString = teamNumberMatcher.group(1);

        if (teamNumberString == null) return -1;
        return Integer.parseInt(teamNumberString);
    }
}