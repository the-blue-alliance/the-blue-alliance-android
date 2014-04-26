package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datatypes.MatchListElement;

import java.util.HashMap;

/**
 * File created by phil on 4/22/14.
 */
public class Match {
    private String matchKey,
            redAlliance,
            blueAlliance;
    private MATCH_TYPES matchType;
    private int matchNumber;
    private int setNumber;
    private int blueScore;
    private int redScore;
    private JsonArray redTeams, blueTeams;

    public enum MATCH_TYPES {
        QUAL {
            @Override
            public MATCH_TYPES previous() {
                return null; // see below for options for this line
            }
        },
        QUARTER,
        SEMI,
        FINAL {
            @Override
            public MATCH_TYPES next() {
                return null; // see below for options for this line
            }
        };

        public MATCH_TYPES next() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() + 1];
        }

        public MATCH_TYPES previous() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() - 1];
        }
    }

    public static final HashMap<MATCH_TYPES, String> SHORT_TYPES, LONG_TYPES;

    static {
        SHORT_TYPES = new HashMap<MATCH_TYPES, String>();
        SHORT_TYPES.put(MATCH_TYPES.QUAL, "q");
        SHORT_TYPES.put(MATCH_TYPES.QUARTER, "qf");
        SHORT_TYPES.put(MATCH_TYPES.SEMI, "sf");
        SHORT_TYPES.put(MATCH_TYPES.FINAL, "f");

        LONG_TYPES = new HashMap<MATCH_TYPES, String>();
        LONG_TYPES.put(MATCH_TYPES.QUAL, "Quals");
        LONG_TYPES.put(MATCH_TYPES.QUARTER, "Quarters");
        LONG_TYPES.put(MATCH_TYPES.SEMI, "Semis");
        LONG_TYPES.put(MATCH_TYPES.FINAL, "Finals");
    }


    public Match() {

    }

    public Match(String matchKey, MATCH_TYPES matchType, int matchNumber, int setNumber, String blueAlliance, String redAlliance, int blueScore, int redScore) {
        this.matchKey = matchKey;
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.setNumber = setNumber;
        this.blueAlliance = blueAlliance;
        this.redAlliance = redAlliance;
        this.blueScore = blueScore;
        this.redScore = redScore;

        redTeams = JSONManager.getasJsonArray(redAlliance);
        blueTeams = JSONManager.getasJsonArray(blueAlliance);
    }

    public Match(String matchKey, MATCH_TYPES matchType, int matchNumber, int setNumber, int blue1, int blue2, int blue3, int red1, int red2, int red3, int blueScore, int redScore) {
        this.matchKey = matchKey;
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.setNumber = setNumber;
        this.blueAlliance = "[" + blue1 + "," + blue2 + "," + blue3 + "]";
        this.redAlliance = "[" + red1 + "," + red2 + "," + red3 + "]";
        this.blueScore = blueScore;
        this.redScore = redScore;

        redTeams = JSONManager.getasJsonArray(redAlliance);
        blueTeams = JSONManager.getasJsonArray(blueAlliance);
    }

    public int getRedScore() {
        return redScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public String getBlueAlliance() {
        return blueAlliance;
    }

    public void setBlueAlliance(String blueAlliance) {
        this.blueAlliance = blueAlliance;

        blueTeams = JSONManager.getasJsonArray(blueAlliance);
    }

    public String getRedAlliance() {
        return redAlliance;
    }

    public void setRedAlliance(String redAlliance) {
        this.redAlliance = redAlliance;

        redTeams = JSONManager.getasJsonArray(redAlliance);
    }

    public MATCH_TYPES getMatchType() {
        return matchType;
    }

    public void setMatchType(MATCH_TYPES matchType) {
        this.matchType = matchType;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public String getTitle() {
        if (matchType == MATCH_TYPES.QUAL) {
            return LONG_TYPES.get(MATCH_TYPES.QUAL) + " " + matchNumber;
        } else {
            return LONG_TYPES.get(matchType) + " " + setNumber + " - " + matchNumber;
        }
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public JsonArray getRedAllianceTeams() {
        if (redTeams == null)
            redTeams = JSONManager.getasJsonArray(redAlliance);
        return redTeams;
    }

    public JsonArray getBlueAllianceTeams() {
        if (blueTeams == null)
            JSONManager.getasJsonArray(blueAlliance);
        return blueTeams;
    }

    public MatchListElement render() {
        return new MatchListElement(true, getTitle(),
                new String[]{redTeams.get(0).getAsString(), redTeams.get(1).getAsString(), redTeams.get(2).getAsString()},
                new String[]{blueTeams.get(0).getAsString(), blueTeams.get(1).getAsString(), blueTeams.get(2).getAsString()},
                redScore, blueScore, matchKey);
    }

}