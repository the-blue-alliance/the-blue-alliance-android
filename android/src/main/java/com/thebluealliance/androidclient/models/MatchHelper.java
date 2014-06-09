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
 * Created by Nathan on 6/6/2014.
 */
public class MatchHelper {

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

        if (qualMatches.children.size() > 0) {
            groups.add(qualMatches);
        }

        if (quarterMatches.children.size() > 0) {
            groups.add(quarterMatches);
        }
        if (semiMatches.children.size() > 0) {
            groups.add(semiMatches);
        }
        if (finalMatches.children.size() > 0) {
            groups.add(finalMatches);
        }
        return groups;
    }

    public static int[] getRecordForTeam(ArrayList<Match> matches, String teamKey) {
        int[] record = new int[3];
        for (Match match : matches) {
            match.addToRecord(teamKey, record);
        }
        return record;
    }

    public static EventPerformance evaluatePerformanceForTeam(Event e, ArrayList<Match> matches, String teamKey) {
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
        for (Match match : matches) {
            match.setSelectedTeam(teamKey);

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

        if (qualMatches.size() == 0) {
            return EventPerformance.NOT_AVAILABLE;
        }

        boolean allQualMatchesPlayed = true;
        for (Match match : qualMatches) {
            if (!match.hasBeenPlayed()) {
                allQualMatchesPlayed = false;
                break;
            }
        }
        if (allQualMatchesPlayed && !allianceData)
        {
            return EventPerformance.NO_ALLIANCE_DATA;
        }
        else if (allQualMatchesPlayed && !inAlliance) {
            return EventPerformance.NOT_PICKED;
        }


        if (quarterMatches.size() > 0) {
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

        if (semiMatches.size() > 0) {
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

        if (finalMatches.size() > 0) {
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
