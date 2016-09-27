package com.thebluealliance.androidclient.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.types.MatchType;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import java.util.ArrayList;
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
      throws BasicModel.FieldNotDefinedException {
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
      throws BasicModel.FieldNotDefinedException {
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
     * Possible statuses a team could be in.
     */
    public enum EventStatus {
        PLAYING_IN_QUALS(R.string.playing_in_quals),
        NOT_PICKED(R.string.not_picked),
        IN_PLAYOFFS(R.string.in_playoffs),
        PLAYING_IN_OCTOFINALS(R.string.playing_in_octofinals),
        ELIMINATED_IN_OCTOFINALS(R.string.eliminated_in_octofinals),
        PLAYING_IN_QUARTERS(R.string.playing_in_quarterfinals),
        ELIMINATED_IN_QUARTERS(R.string.eliminated_in_quarterfinals),
        PLAYING_IN_SEMIS(R.string.playing_in_semifinals),
        ELIMINATED_IN_SEMIS(R.string.eliminated_in_semifinals),
        PLAYING_IN_FINALS(R.string.playing_in_finals),
        ELIMINATED_IN_FINALS(R.string.eliminated_in_finals),
        WON_EVENT(R.string.won_event),
        NOT_AVAILABLE(R.string.not_available),
        NO_ALLIANCE_DATA(R.string.no_alli_data);
        public int descriptionId;

        EventStatus(int descriptionId) {
            this.descriptionId = descriptionId;
        }

        public String getDescriptionString(Resources resources) {
            return resources.getString(descriptionId);
        }
    }

    /**
     * Gets the alliance for a team competing at an event by looking at QF matches Used if no
     * alliance data available
     *
     * @param teamMatches team's match list for an event
     * @param teamKey     key associated with team
     * @return alliance number for team, or -1 if not on an alliance
     */
    public static int getAllianceForTeam(List<Match> teamMatches, String teamKey) {
        int alliance = -1;
        if (teamMatches == null) {
            return alliance;
        }
        for (Match match : teamMatches) {
            if (MatchType.fromShortType(match.getCompLevel()) == MatchType.QUARTER) {
                JsonObject matchAlliances = match.getAlliancesJson();
                JsonArray redTeams = Match.getRedTeams(matchAlliances);
                Boolean isRed = Match.hasTeam(redTeams, teamKey);

                if (match.getYear() != 2015) {
                    switch (match.getSetNumber()) {
                        case 1:
                            alliance = isRed ? 1 : 8;
                            break;
                        case 2:
                            alliance = isRed ? 4 : 5;
                            break;
                        case 3:
                            alliance = isRed ? 2 : 7;
                            break;
                        case 4:
                            alliance = isRed ? 3 : 6;
                            break;
                    }
                } else {
                    /* Special format for 2015 */
                    switch (match.getMatchNumber()) {
                        case 1:
                            alliance = isRed ? 4 : 5;
                            break;
                        case 2:
                            alliance = isRed ? 3 : 6;
                            break;
                        case 3:
                            alliance = isRed ? 2 : 7;
                            break;
                        case 4:
                            alliance = isRed ? 1 : 8;
                            break;
                    }
                }

                break;
            }
        }
        return alliance;
    }

    /**
     * Gets the record for a team competing at an event
     *
     * @param matches match list for an event
     * @param teamKey key associated with team
     * @return team record for that event
     */
    public static int[] getRecordForTeam(List<Match> matches, String teamKey) {
        int[] record = new int[3];
        for (Match match : matches) {
            match.addToRecord(teamKey, record);
        }
        return record;
    }

    /**
     * Determines the past/current status of a team at an event.
     *
     * @param e           the event the team is competing at
     * @param teamMatches team's match list
     * @param teamKey     key associated with team
     * @return team's past/current event status
     */
    public static EventStatus evaluateStatusOfTeam(Event e, List<Match> teamMatches, String teamKey)
    throws BasicModel.FieldNotDefinedException {

        // There might be match info available,
        // but no alliance selection data (for old events)
        JsonArray alliances = JSONHelper.getasJsonArray(e.getAlliances());
        int year = 2014;

        boolean inAlliance = false;
        if (alliances == null || alliances.size() == 0) {
            // We don't have alliance data. Try to determine from matches.
            inAlliance = MatchHelper.getAllianceForTeam(teamMatches, teamKey) != -1;
        } else {
            for (int i = 0; i < alliances.size(); i++) {
                JsonArray teams = alliances.get(i).getAsJsonObject().get("picks").getAsJsonArray();
                for (int j = 0; j < teams.size(); j++) {
                    if (teams.get(j).getAsString().equals(teamKey)) {
                        inAlliance = true;
                    }
                }
            }
        }
        ArrayList<Match> qualMatches = new ArrayList<>();
        ArrayList<Match> octoMatches = new ArrayList<>();
        ArrayList<Match> quarterMatches = new ArrayList<>();
        ArrayList<Match> semiMatches = new ArrayList<>();
        ArrayList<Match> finalMatches = new ArrayList<>();

        ArrayList<Match> currentGroup = qualMatches;
        MatchType lastType = null;

        // Team might be a no-show/drop out last minute at an event,
        // and might not play any matches as a result.
        boolean teamIsHere = false;

        boolean elimMatchPlayed = false;
        int qfPlayed = 0;
        int efPlayed = 0;
        int sfPlayed = 0;
        int fPlayed = 0;
        for (Match match : teamMatches) {
            match.setSelectedTeam(teamKey);
            year = match.getYear();

            MatchType matchType = MatchType.fromShortType(match.getCompLevel());
            JsonObject matchAlliances = match.getAlliancesJson();
            JsonArray redTeams = Match.getRedTeams(matchAlliances),
                    blueTeams = Match.getBlueTeams(matchAlliances);

            if (Match.hasTeam(redTeams, teamKey) || Match.hasTeam(blueTeams, teamKey)) {
                teamIsHere = true;
            }

            if (match.hasBeenPlayed()) {
                switch (matchType) {
                    case OCTO:
                        elimMatchPlayed = true;
                        efPlayed++;
                        break;
                    case QUARTER:
                        elimMatchPlayed = true;
                        qfPlayed++;
                        break;
                    case SEMI:
                        elimMatchPlayed = true;
                        sfPlayed++;
                        break;
                    case FINAL:
                        elimMatchPlayed = true;
                        fPlayed++;
                        break;
                }
            }

            if (lastType != matchType) {
                switch (matchType) {
                    case QUAL:
                        currentGroup = qualMatches;
                        break;
                    case OCTO:
                        currentGroup = octoMatches;
                        break;
                    case QUARTER:
                        currentGroup = quarterMatches;
                        break;
                    case SEMI:
                        currentGroup = semiMatches;
                        break;
                    case FINAL:
                        currentGroup = finalMatches;
                        break;
                }
            }
            currentGroup.add(match);
            lastType = matchType;
        }

        TbaLogger.d("qual size: " + qualMatches.size());
        TbaLogger.d("quarter size: " + quarterMatches.size());
        TbaLogger.d("semi size: " + semiMatches.size());
        TbaLogger.d("final size: " + finalMatches.size());

        if (e.isHappeningNow() && quarterMatches.size() == 0) {
            return EventStatus.PLAYING_IN_QUALS;
        }


        boolean allQualMatchesPlayed = true;
        for (Match match : qualMatches) {
            if (!match.hasBeenPlayed()) {
                TbaLogger.d("Match " + match.getKey() + " not played!");
                allQualMatchesPlayed = false;
                break;
            }
        }

        TbaLogger.d("In alliance: " + inAlliance);
        TbaLogger.d("All qual matches played: " + allQualMatchesPlayed);
        if (qualMatches.isEmpty()
                || (allQualMatchesPlayed && !teamIsHere)
                || (!(elimMatchPlayed || allQualMatchesPlayed) && !e.isHappeningNow())) {
            return EventStatus.NOT_AVAILABLE;
        } else if ((allQualMatchesPlayed && !inAlliance)
                || (!e.isHappeningNow()
                        && (octoMatches.isEmpty()
                                && quarterMatches.isEmpty()
                                && semiMatches.isEmpty()
                                && finalMatches.isEmpty()))) {
            return EventStatus.NOT_PICKED;
        }

        if (year == 2015) {
            /* Special elim logic for 2015 season */
            if (!finalMatches.isEmpty() && sfPlayed > 0) {
                int finalsWon = 0;
                for (Match match : finalMatches) {
                    if (match.didSelectedTeamWin()) {
                        finalsWon++;
                    }
                }
                if (finalsWon >= 2) {
                    return EventStatus.WON_EVENT;
                } else if ((fPlayed == 2 && finalsWon == 0) || (fPlayed == 3 && finalsWon == 1)) {
                    return EventStatus.ELIMINATED_IN_FINALS;
                } else {
                    return EventStatus.PLAYING_IN_FINALS;
                }
            } else if (!semiMatches.isEmpty() && qfPlayed > 0) {
                if (sfPlayed < 3) {
                    return EventStatus.PLAYING_IN_SEMIS;
                } else {
                    return EventStatus.ELIMINATED_IN_SEMIS;
                }
            } else if (!quarterMatches.isEmpty()){
                if (qfPlayed < 2) {
                    return EventStatus.PLAYING_IN_QUARTERS;
                } else {
                    return EventStatus.ELIMINATED_IN_QUARTERS;
                }
            } else if (!octoMatches.isEmpty()){
                if (efPlayed < 2) {
                    return EventStatus.PLAYING_IN_OCTOFINALS;
                } else {
                    return EventStatus.ELIMINATED_IN_OCTOFINALS;
                }
            } else {
                return EventStatus.IN_PLAYOFFS;
            }
        }

        if (!octoMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : octoMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliancesJson();
                    JsonArray redTeams = Match.getRedTeams(matchAlliances),
                            blueTeams = Match.getBlueTeams(matchAlliances);
                    if (!Match.hasTeam(redTeams, teamKey) && !Match.hasTeam(blueTeams, teamKey)) {
                        continue;
                    }
                    countPlayed++;
                    if (match.didSelectedTeamWin()) {
                        countWon++;
                    }
                }
            }
            if (countPlayed > 1 && countWon > 1) {
                // Won octofinals
            } else if ((countPlayed > 1 && countWon == 0) || (countPlayed > 2 && countWon == 1)) {
                return EventStatus.ELIMINATED_IN_OCTOFINALS;
            } else if (!e.isHappeningNow() && semiMatches.isEmpty()) {
                return EventStatus.ELIMINATED_IN_OCTOFINALS;
            } else {
                return EventStatus.PLAYING_IN_OCTOFINALS;
            }
        }

        if (!quarterMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : quarterMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliancesJson();
                    JsonArray redTeams = Match.getRedTeams(matchAlliances),
                            blueTeams = Match.getBlueTeams(matchAlliances);
                    if (!Match.hasTeam(redTeams, teamKey) && !Match.hasTeam(blueTeams, teamKey)) {
                        continue;
                    }
                    countPlayed++;
                    if (match.didSelectedTeamWin()) {
                        countWon++;
                    }
                }
            }
            if (countPlayed > 1 && countWon > 1) {
                // Won quarterfinals
            } else if ((countPlayed > 1 && countWon == 0) || (countPlayed > 2 && countWon == 1)) {
                return EventStatus.ELIMINATED_IN_QUARTERS;
            } else if (!e.isHappeningNow() && semiMatches.isEmpty()) {
                return EventStatus.ELIMINATED_IN_QUARTERS;
            } else {
                return EventStatus.PLAYING_IN_QUARTERS;
            }
        } else {
            // We've already checked for not picked/no alliance data/etc above, so if the current group is empty,
            // then the team is likely playing the first match of quarters/semis/finals.
            return EventStatus.IN_PLAYOFFS;
        }

        if (!semiMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : semiMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliancesJson();
                    JsonArray redTeams = Match.getRedTeams(matchAlliances),
                            blueTeams = Match.getBlueTeams(matchAlliances);
                    if (!Match.hasTeam(redTeams, teamKey) && !Match.hasTeam(blueTeams, teamKey)) {
                        continue;
                    }
                    countPlayed++;
                    if (match.didSelectedTeamWin()) {
                        countWon++;
                    }
                }
            }
            if (countPlayed > 1 && countWon > 1) {
                // Won semifinals
            } else if ((countPlayed > 1 && countWon == 0) || (countPlayed > 2 && countWon == 1)) {
                return EventStatus.ELIMINATED_IN_SEMIS;
            } else if (!e.isHappeningNow() && finalMatches.isEmpty()) {
                return EventStatus.ELIMINATED_IN_SEMIS;
            } else {
                return EventStatus.PLAYING_IN_SEMIS;
            }
        } else {
            return EventStatus.PLAYING_IN_SEMIS;
        }

        if (!finalMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : finalMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliancesJson();
                    JsonArray redTeams = Match.getRedTeams(matchAlliances),
                            blueTeams = Match.getBlueTeams(matchAlliances);
                    if (!Match.hasTeam(redTeams, teamKey) && !Match.hasTeam(blueTeams, teamKey)) {
                        continue;
                    }
                    countPlayed++;
                    if (match.didSelectedTeamWin()) {
                        countWon++;
                    }
                }
            }
            if (countPlayed > 1 && countWon > 1) {
                // Won event
                return EventStatus.WON_EVENT;
            } else if ((countPlayed > 1 && countWon == 0) || (countPlayed > 2 && countWon == 1)) {
                return EventStatus.ELIMINATED_IN_FINALS;
            } else if (!e.isHappeningNow()) {
                return EventStatus.ELIMINATED_IN_FINALS;
            } else {
                return EventStatus.PLAYING_IN_FINALS;
            }
        } else {
            return EventStatus.PLAYING_IN_FINALS;
        }
    }

    /**
     * Returns a title like "Quals 10" or "Finals 1 Match 2", or abbreviated "Q10" or "F1-2".
     * <p>
     * <p/>NOTE: For people following more than one event at a time, the abbreviated form could
     * include the event code, e.g. "ILCH Q10".
     */
    static String getMatchTitleFromMatchKey(Context context, String matchKey, boolean abbrev) {
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

    public static MatchType getMatchTypeFromKey(String matchKey) {
        String keyWithoutEvent = matchKey.replaceAll(".*_", "");
        Pattern regexPattern = Pattern.compile("([a-z]+)([0-9]+)m?([0-9]*)");
        Matcher m = regexPattern.matcher(keyWithoutEvent);
        if (m.matches()) {
            String typeCode = m.group(1);
            return MatchType.fromShortType(typeCode);
        } else {
            return MatchType.NONE;
        }
    }
}
