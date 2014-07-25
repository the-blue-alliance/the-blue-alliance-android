package com.thebluealliance.androidclient.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support methods for dealing with Match models
 *
 * @author Bryce Matsuda
 * @author Nathan Walters
 * @author Phil Lopreiato
 *         <p/>
 *         Created by Nathan on 6/6/2014.
 */
public class MatchHelper {

    public static enum TYPE {
        NONE,
        QUAL {
            @Override
            public TYPE previous() {
                return null; // see below for options for this line
            }
        },
        QUARTER,
        SEMI,
        FINAL {
            @Override
            public TYPE next() {
                return null; // see below for options for this line
            }
        };

        public TYPE next() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() + 1];
        }

        public TYPE previous() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() - 1];
        }

        public TYPE get(String str) {
            return valueOf(str);
        }

        public static TYPE fromShortType(String str) {
            switch (str) {
                case "qm":
                    return QUAL;
                case "ef":
                case "qf":
                    return QUARTER;
                case "sf":
                    return SEMI;
                case "f":
                    return FINAL;
                default:
                    throw new IllegalArgumentException("Invalid short type");
            }
        }

        public static TYPE fromKey(String key) {
            if (key.contains("_qm")) return QUAL;
            if (key.contains("_ef") || key.contains("_qf")) return QUARTER;
            if (key.contains("_sf")) return SEMI;
            if (key.contains("_f")) return FINAL;
            return NONE;
        }
    }

    public static final HashMap<TYPE, String> SHORT_TYPES;
    public static final HashMap<TYPE, String> LONG_TYPES;
    public static final HashMap<TYPE, Integer> PLAY_ORDER;

    static {
        SHORT_TYPES = new HashMap<>();
        SHORT_TYPES.put(MatchHelper.TYPE.QUAL, "qm");
        SHORT_TYPES.put(MatchHelper.TYPE.QUARTER, "qf");
        SHORT_TYPES.put(MatchHelper.TYPE.SEMI, "sf");
        SHORT_TYPES.put(MatchHelper.TYPE.FINAL, "f");

        LONG_TYPES = new HashMap<>();
        LONG_TYPES.put(MatchHelper.TYPE.QUAL, "Quals");
        LONG_TYPES.put(MatchHelper.TYPE.QUARTER, "Quarters");
        LONG_TYPES.put(MatchHelper.TYPE.SEMI, "Semis");
        LONG_TYPES.put(MatchHelper.TYPE.FINAL, "Finals");

        PLAY_ORDER = new HashMap<>();
        PLAY_ORDER.put(MatchHelper.TYPE.QUAL, 1);
        PLAY_ORDER.put(MatchHelper.TYPE.QUARTER, 2);
        PLAY_ORDER.put(MatchHelper.TYPE.SEMI, 3);
        PLAY_ORDER.put(MatchHelper.TYPE.FINAL, 4);
    }

    public static boolean validateMatchKey(String key) {
        if (key == null || key.isEmpty()) return false;

        return key.matches("^[1-9]\\d{3}[a-z,0-9]+\\_(?:qm|ef\\dm|qf\\dm|sf\\dm|f\\dm)\\d+$");
    }

    /**
     * Returns the match object of the match next to be played
     *
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Next match
     */
    public static Match getNextMatchPlayed(ArrayList<Match> matches) throws BasicModel.FieldNotDefinedException {
        for (Match m : matches) {
            if (m.getAlliances().get("red").getAsJsonObject().get("score").getAsInt() <= -1 &&
                    m.getAlliances().get("blue").getAsJsonObject().get("score").getAsInt() <= -1) {
                //match is unplayed
                return m;
            }
        }
        //all matches have been played
        return null;
    }

    /**
     * Returns the match object of the last match played
     *
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Last match played
     */
    public static Match getLastMatchPlayed(ArrayList<Match> matches) throws BasicModel.FieldNotDefinedException {
        Match last = null;
        for (Match m : matches) {
            if (m.getAlliances().get("red").getAsJsonObject().get("score").getAsInt() <= -1 &&
                    m.getAlliances().get("blue").getAsJsonObject().get("score").getAsInt() <= -1) {
                break;
            } else {
                last = m;
            }
        }
        return last;
    }

    /**
     * Possible statuses a team could be in.
     */
    public enum EventStatus {
        PLAYING_IN_QUALS(R.string.playing_in_quals),
        NOT_PICKED(R.string.not_picked),
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

        public String getDescriptionString(Context c) {
            return c.getResources().getString(descriptionId);
        }
    }

    /**
     * Constructs a match list for a team competing at an event
     *
     * @param c       activity
     * @param matches list of matches
     * @return match list
     */
    public static ArrayList<ListGroup> constructMatchList(Context c, ArrayList<Match> matches) throws BasicModel.FieldNotDefinedException {

        ArrayList<ListGroup> groups = new ArrayList<>();
        ListGroup qualMatches = new ListGroup(c.getString(R.string.quals_header));
        ListGroup quarterMatches = new ListGroup(c.getString(R.string.quarters_header));
        ListGroup semiMatches = new ListGroup(c.getString(R.string.semis_header));
        ListGroup finalMatches = new ListGroup(c.getString(R.string.finals_header));

        ListGroup currentGroup = qualMatches;
        TYPE lastType = null;
        for (Match match : matches) {

            if (lastType != match.getType()) {
                switch (match.getType()) {
                    case QUAL:
                        currentGroup = qualMatches;
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

            currentGroup.children.add(match);
        }

        if (!qualMatches.children.isEmpty()) {
            groups.add(qualMatches);
        }

        if (!quarterMatches.children.isEmpty()) {
            groups.add(quarterMatches);
        }
        if (!semiMatches.children.isEmpty()) {
            groups.add(semiMatches);
        }
        if (!finalMatches.children.isEmpty()) {
            groups.add(finalMatches);
        }
        return groups;
    }

    public static ArrayList<Match> getMatchesForTeam(ArrayList<Match> matches, String teamKey) {
        ArrayList<Match> teamMatches = new ArrayList<>();
        for (Match match : matches) {
            try {
                if (match.getAlliances().toString().contains(teamKey + "\"")) {
                    teamMatches.add(match);
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
                continue;
            }
        }
        return teamMatches;
    }

    /**
     * Gets the alliance for a team competing at an event by looking at QF matches
     * Used if no alliance data available
     *
     * @param teamMatches team's match list for an event
     * @param teamKey     key associated with team
     * @return alliance number for team, or -1 if not on an alliance
     */
    public static int getAllianceForTeam(ArrayList<Match> teamMatches, String teamKey) {
        int alliance = -1;
        if (teamMatches == null) {
            return alliance;
        }
        for (Match match : teamMatches) {
            try {
                if (match.getType() == TYPE.QUARTER) {
                    JsonObject matchAlliances = match.getAlliances();
                    JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray();
                    Boolean isRed = redTeams.toString().contains(teamKey);

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

                    break;
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Match doesn't have alliances defined. Can't determine alliance");
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
    public static int[] getRecordForTeam(ArrayList<Match> matches, String teamKey) {
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
    public static EventStatus evaluateStatusOfTeam(Event e, ArrayList<Match> teamMatches, String teamKey) throws BasicModel.FieldNotDefinedException {

        // There might be match info available,
        // but no alliance selection data (for old events)
        JsonArray alliances = e.getAlliances();

        boolean inAlliance = false;
        if (alliances.size() == 0) {
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
        ArrayList<Match> quarterMatches = new ArrayList<>();
        ArrayList<Match> semiMatches = new ArrayList<>();
        ArrayList<Match> finalMatches = new ArrayList<>();

        ArrayList<Match> currentGroup = qualMatches;
        TYPE lastType = null;

        // Team might be a no-show/drop out last minute at an event,
        // and might not play any matches as a result.
        boolean teamIsHere = false;
        for (Match match : teamMatches) {
            match.setSelectedTeam(teamKey);

            JsonObject matchAlliances = match.getAlliances();
            JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                    blueTeams = matchAlliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();

            if (redTeams.toString().contains(teamKey) ||
                    blueTeams.toString().contains(teamKey)) {
                teamIsHere = true;
            }

            if (lastType != match.getType()) {
                switch (match.getType()) {
                    case QUAL:
                        currentGroup = qualMatches;
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
        }

        Log.d(Constants.LOG_TAG, "qual size: " + qualMatches.size());
        Log.d(Constants.LOG_TAG, "quarter size: " + quarterMatches.size());
        Log.d(Constants.LOG_TAG, "semi size: " + semiMatches.size());
        Log.d(Constants.LOG_TAG, "final size: " + finalMatches.size());

        if (e.isHappeningNow() && quarterMatches.size() == 0) {
            return EventStatus.PLAYING_IN_QUALS;
        }


        boolean allQualMatchesPlayed = true;
        for (Match match : qualMatches) {
            if (!match.hasBeenPlayed()) {
                Log.d(Constants.LOG_TAG, "Match " + match.getKey() + " not played!");
                allQualMatchesPlayed = false;
                break;
            }
        }

        Log.d(Constants.LOG_TAG, "In alliance: " + inAlliance);
        Log.d(Constants.LOG_TAG, "All qual matches played: " + allQualMatchesPlayed);
        if (qualMatches.isEmpty() ||
                (allQualMatchesPlayed && !teamIsHere)) {
            return EventStatus.NOT_AVAILABLE;
        } else if ((allQualMatchesPlayed && !inAlliance) ||
                (!e.isHappeningNow() &&
                        (quarterMatches.isEmpty() &&
                                semiMatches.isEmpty() &&
                                finalMatches.isEmpty()))) {
            return EventStatus.NOT_PICKED;
        }

        if (!quarterMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : quarterMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliances();
                    JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                            blueTeams = matchAlliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
                    if (!redTeams.toString().contains(teamKey + "\"") && !blueTeams.toString().contains(teamKey + "\"")) {
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
            return EventStatus.PLAYING_IN_QUARTERS;
        }

        if (!semiMatches.isEmpty()) {
            int countPlayed = 0, countWon = 0;
            for (Match match : semiMatches) {
                if (match.hasBeenPlayed()) {
                    JsonObject matchAlliances = match.getAlliances();
                    JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                            blueTeams = matchAlliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
                    if (!redTeams.toString().contains(teamKey + "\"") && !blueTeams.toString().contains(teamKey + "\"")) {
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
                    JsonObject matchAlliances = match.getAlliances();
                    JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                            blueTeams = matchAlliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
                    if (!redTeams.toString().contains(teamKey + "\"") && !blueTeams.toString().contains(teamKey + "\"")) {
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

    public static String getMatchTitleFromMatchKey(String matchKey) {
        // match key comes in the form of (EVENTKEY)_(TYPE)(MATCHNUM)m(MATCHNUM)
        // e.g. "2014ilch_f1m1"

        // Strip out the event key
        String keyWithoutEvent = matchKey.replaceAll(".*_", "");

        Pattern regexPattern = Pattern.compile("([a-z]+)([0-9]+)m?([0-9]*)?");
        Matcher m = regexPattern.matcher(keyWithoutEvent);
        if (m.matches()) {

            String set = null, number = null;
            String type = m.group(1);
            // If the match key looks like AA##, then the numbers correspond to the match number.
            // Otherwise, if it looks like AA##m##, then the first group of numbers corresponds
            // to the set number and the second group of numbers corresponds to the match number.
            if (m.group(3) == null || m.group(3).isEmpty()) {
                number = m.group(2);
            } else {
                set = m.group(2);
                number = m.group(3);
            }

            return LONG_TYPES.get(TYPE.fromShortType(type)) + " " + number + (set == null ? "" : " Match " + set);
        } else {
            return "Could not find match title";
        }
    }
}
