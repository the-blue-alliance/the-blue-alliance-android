package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datatypes.ListGroup;

import java.util.ArrayList;

/**
 * Helps determine a team's record & past/current performance at an FRC event.
 *
 * @author Bryce Matsuda
 * @author Nathan Walters
 *
 * Created by Nathan on 6/6/2014.
 */
public class MatchHelper {

    /**
     * Possible outcomes of a team's performance,
     *
     */
    public enum EventPerformance {
        PLAYING_IN_QUALS("playing in the qualification matches"),
        NOT_PICKED("not picked"),
        PLAYING_IN_QUARTERS("playing in the quarterfinals"),
        ELIMINATED_IN_QUARTERS("eliminated in the quarterfinals"),
        PLAYING_IN_SEMIS("playing in the semifinals"),
        ELIMINATED_IN_SEMIS("eliminated in the semifinals"),
        PLAYING_IN_FINALS("playing in the finals"),
        ELIMINATED_IN_FINALS("eliminated in the finals"),
        WON_EVENT("won the event"),
        NOT_AVAILABLE("not available"),
        NO_ALLIANCE_DATA("no alliance data");
        public String description;

        EventPerformance(String description) {
            this.description = description;
        }
    }

    /**
     * Constructs a match list for a team competing at an event
     * @param c activity
     * @param matches list of matches
     * @return match list
     */
    public static ArrayList<ListGroup> constructMatchList(Context c, ArrayList<Match> matches) {

        ArrayList<ListGroup> groups = new ArrayList<>();
        ListGroup qualMatches = new ListGroup(c.getString(R.string.quals_header));
        ListGroup quarterMatches = new ListGroup(c.getString(R.string.quarters_header));
        ListGroup semiMatches = new ListGroup(c.getString(R.string.semis_header));
        ListGroup finalMatches = new ListGroup(c.getString(R.string.finals_header));
        MatchSortByPlayOrderComparator comparator = new MatchSortByPlayOrderComparator();

        ListGroup currentGroup = qualMatches;
        Match.TYPE lastType = null;
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
     * Determines the past/current performance of a team at an event.
     *
     * @param e the event the team is competing at
     * @param matches team's match list
     * @param teamKey key associated with team
     * @return team's past/current event performance
     */
    public static EventPerformance evaluatePerformanceForTeam(Event e, ArrayList<Match> matches, String teamKey) {

        // There might be match info available,
        // but no alliance selection data (for old events)
        boolean allianceData = true;
        JsonArray alliances = e.getAlliances();

        if (alliances.size() == 0) allianceData = false;

        boolean inAlliance = false;
        for (int i = 0; i < alliances.size(); i++) {
            JsonArray teams = alliances.get(i).getAsJsonObject().get("picks").getAsJsonArray();
            for (int j = 0; j < teams.size(); j++) {
                if (teams.get(j).getAsString().equals(teamKey)) {
                    inAlliance = true;
                }
            }
        }
        ArrayList<Match> qualMatches = new ArrayList<>();
        ArrayList<Match> quarterMatches = new ArrayList<>();
        ArrayList<Match> semiMatches = new ArrayList<>();
        ArrayList<Match> finalMatches = new ArrayList<>();

        ArrayList<Match> currentGroup = qualMatches;
        Match.TYPE lastType = null;

        // Team might be a no-show/drop out last minute at an event,
        // and might not play any matches as a result.
        boolean teamIsHere = false;
        for (Match match : matches) {
            match.setSelectedTeam(teamKey);

            JsonObject matchAlliances = match.getAlliances();
            JsonArray redTeams = matchAlliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                    blueTeams = matchAlliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();

            if (redTeams.toString().contains(teamKey) ||
                    blueTeams.toString().contains(teamKey)){
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
            return EventPerformance.PLAYING_IN_QUALS;
        }


        boolean allQualMatchesPlayed = true;
        for (Match match : qualMatches) {
            if (!match.hasBeenPlayed()) {
                allQualMatchesPlayed = false;
                break;
            }
        }

        if (qualMatches.isEmpty() ||
           (allQualMatchesPlayed && !teamIsHere)) {
            return EventPerformance.NOT_AVAILABLE;
        }
        else if (allQualMatchesPlayed && !allianceData)
        {
            return EventPerformance.NO_ALLIANCE_DATA;
        }
        else if (allQualMatchesPlayed && !inAlliance) {
            return EventPerformance.NOT_PICKED;
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
                return EventPerformance.ELIMINATED_IN_QUARTERS;
            } else {
                return EventPerformance.PLAYING_IN_QUARTERS;
            }
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
                return EventPerformance.ELIMINATED_IN_SEMIS;
            } else {
                return EventPerformance.PLAYING_IN_SEMIS;
            }
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
                return EventPerformance.WON_EVENT;
            } else if ((countPlayed > 1 && countWon == 0) || (countPlayed > 2 && countWon == 1)) {
                return EventPerformance.ELIMINATED_IN_FINALS;
            } else {
                return EventPerformance.PLAYING_IN_FINALS;
            }
        } else {
            return EventPerformance.PLAYING_IN_FINALS;
        }
    }
}
