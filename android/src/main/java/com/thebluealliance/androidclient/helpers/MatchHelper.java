package com.thebluealliance.androidclient.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.types.MatchType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support methods for dealing with Match models
 *
 * @author Bryce Matsuda
 * @author Nathan Walters
 * @author Phil Lopreiato
 */
public final class MatchHelper {

    private MatchHelper() {
        // not used
    }

    public static boolean validateMatchKey(String key) {
        if (key == null || key.isEmpty()) return false;

        return key.matches("^[1-9]\\d{3}[a-z,0-9]+_(?:qm|ef\\dm|qf\\dm|sf\\dm|f\\dm)\\d+$");
    }

    public static String getEventKeyFromMatchKey(String matchKey) {
        if (validateMatchKey(matchKey)) {
            return matchKey.replaceAll("_.+", "");
        } else {
            return matchKey;
        }
    }

    /**
     * Returns the match object of the match next to be played
     * Iterate backwards to account for data gaps
     *
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Next match
     */
    public static @Nullable Match getNextMatchPlayed(List<Match> matches)
       {
        if (matches == null || matches.isEmpty()) return null;

        Match last = null;
        for (int i = matches.size() - 1; i >= 0; i--) {
            Match m = matches.get(i);
            if (m.hasBeenPlayed()) {
                return last;
            }
            last = m;
        }

        // no matches played
        return matches.get(0);
    }

    /**
     * Returns the match object of the last match played
     * Iterate backwards to account for data gaps
     *
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Last match played
     */
    public static @Nullable Match getLastMatchPlayed(List<Match> matches)
       {
        if (matches == null || matches.isEmpty()) return null;
        for (int i = matches.size() - 1; i >= 0; i--) {
            Match m = matches.get(i);
            if (m.hasBeenPlayed()) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns a title like "Quals 10" or "Finals 1 Match 2", or abbreviated "Q10" or "F1-2".
     * <p>
     * <p/>NOTE: For people following more than one event at a time, the abbreviated form could
     * include the event code, e.g. "ILCH Q10".
     */
    private static String getMatchTitleFromMatchKey(Context context, String matchKey, boolean abbrev) {
        // match key comes in the form of (EVENTKEY)_(TYPE)(MATCHNUM)m(MATCHNUM)
        // e.g. "2014ilch_f1m1"

        // Strip out the event key
        String keyWithoutEvent = matchKey.replaceAll(".*_", "");

        Pattern regexPattern = Pattern.compile("([a-z]+)([0-9]+)m?([0-9]*)");
        Matcher m = regexPattern.matcher(keyWithoutEvent);
        if (m.matches()) {

            String set = null, number;
            String typeCode = m.group(1);
            MatchType type = MatchType.fromShortType(typeCode);
            String typeName = context.getString(abbrev ? type.getTypeAbbreviation() : type.getTypeName());

            // If the match key looks like AA##, then the numbers correspond to the match number.
            // Otherwise, if it looks like AA##m##, then the first group of numbers corresponds
            // to the set number and the second group of numbers corresponds to the match number.
            if (m.group(3) == null || m.group(3).isEmpty()) {
                number = m.group(2);
            } else {
                set = m.group(2);
                number = m.group(3);
            }

            if (set == null) {
                // No set specified; this is a match like "Quals 10" (abbrev "Q10")
                String format = context.getString(abbrev ? R.string.match_title_abbrev_format
                        : R.string.match_title_format);
                return String.format(format, typeName, number);
            } else {
                // This is a match like "Semis 1 Match 2" (abbrev "SF1-2")
                String format = context.getString(abbrev ? R.string.submatch_title_abbrev_format
                        : R.string.submatch_title_format);
                return String.format(format, typeName, set, number);
            }
        } else {
            return context.getString(R.string.cannot_find_match_title);
        }
    }

    public static String getMatchTitleFromMatchKey(Context context, String matchKey) {
        return getMatchTitleFromMatchKey(context, matchKey, false);
    }

    public static String getAbbrevMatchTitleFromMatchKey(Context context, String matchKey) {
        return getMatchTitleFromMatchKey(context, matchKey, true);
    }
}
