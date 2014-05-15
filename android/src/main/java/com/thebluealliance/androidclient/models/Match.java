package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.datatypes.MatchListElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Match implements BasicModel {
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
    }

    public static final HashMap<TYPE, String> SHORT_TYPES, LONG_TYPES;
    public static final HashMap<TYPE, Integer> PLAY_ORDER;

    static {
        SHORT_TYPES = new HashMap<TYPE, String>();
        SHORT_TYPES.put(TYPE.QUAL, "qm");
        SHORT_TYPES.put(TYPE.QUARTER, "qf");
        SHORT_TYPES.put(TYPE.SEMI, "sf");
        SHORT_TYPES.put(TYPE.FINAL, "f");

        LONG_TYPES = new HashMap<TYPE, String>();
        LONG_TYPES.put(TYPE.QUAL, "Quals");
        LONG_TYPES.put(TYPE.QUARTER, "Quarters");
        LONG_TYPES.put(TYPE.SEMI, "Semis");
        LONG_TYPES.put(TYPE.FINAL, "Finals");

        PLAY_ORDER = new HashMap<>();
        PLAY_ORDER.put(TYPE.QUAL,1);
        PLAY_ORDER.put(TYPE.QUARTER,2);
        PLAY_ORDER.put(TYPE.SEMI,3);
        PLAY_ORDER.put(TYPE.FINAL,4);
    }


    String key,
            eventKey,
            timeString;
    Match.TYPE type;
    JsonObject alliances;
    JsonArray videos;
    int year,
            matchNumber,
            setNumber;
    Date time;
    long last_updated;

    public Match() {
        this.key = "";
        this.eventKey = "";
        this.timeString = "";
        this.time = new Date(0);
        this.type = TYPE.NONE;
        this.alliances = new JsonObject();
        this.videos = new JsonArray();
        this.year = -1;
        this.matchNumber = -1;
        this.setNumber = -1;
        this.last_updated = -1;
    }

    public Match(String key, TYPE type, int matchNumber, int setNumber, JsonObject alliances, String timeString, long timestamp, JsonArray videos, long last_updated) {
        if (!validateMatchKey(key)) throw new IllegalArgumentException("Invalid match key.");
        this.key = key;
        this.eventKey = key.split("_")[0];
        this.timeString = timeString;
        this.time = new Date(timestamp);
        this.type = type;
        this.alliances = alliances;
        this.videos = videos;
        this.year = Integer.parseInt(key.substring(0, 3));
        this.matchNumber = matchNumber;
        this.setNumber = setNumber;
        this.last_updated = last_updated;
    }

    /* Temporary constructor for fake data. Probably to be removed... */

    public Match(String key, TYPE type, int matchNumber, int setNumber, int red1, int red2, int red3, int blue1, int blue2, int blue3, int redScore, int blueScore){
        if(!validateMatchKey(key)) throw new IllegalArgumentException("Invalid match key: "+key);
        this.key = key;
        this.eventKey = key.split("_")[0];
        this.timeString = "";
        this.time = new Date(0);
        this.type = type;
        this.alliances = new JsonObject();
        JsonObject blueAlliance = new JsonObject();
        blueAlliance.addProperty("score", blueScore);
        JsonArray blueTeams = new JsonArray();
        blueTeams.add(new JsonPrimitive("frc" + blue1));
        blueTeams.add(new JsonPrimitive("frc" + blue2));
        blueTeams.add(new JsonPrimitive("frc" + blue3));
        blueAlliance.add("teams", blueTeams);
        JsonObject redAllaince = new JsonObject();
        redAllaince.addProperty("score", redScore);
        JsonArray redTeams = new JsonArray();
        redTeams.add(new JsonPrimitive("frc" + red1));
        redTeams.add(new JsonPrimitive("frc" + red2));
        redTeams.add(new JsonPrimitive("frc" + red3));
        redAllaince.add("teams", redTeams);
        alliances.add("blue", blueAlliance);
        alliances.add("red", redAllaince);
        this.videos = new JsonArray();
        this.year = Integer.parseInt(key.substring(0, 3));
        this.matchNumber = matchNumber;
        this.setNumber = setNumber;
        this.last_updated = -1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if(!validateMatchKey(key)) throw new IllegalArgumentException("Invalid match key: "+key);
        this.key = key;
        this.eventKey = key.split("_")[0];
        this.year = Integer.parseInt(key.substring(0, 3));
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTime(long timestamp) {
        this.time = new Date(timestamp);
    }

    public Match.TYPE getType() {
        return type;
    }

    public void setType(Match.TYPE type) {
        this.type = type;
    }

    public void setTypeFromShort(String type) {
        this.type = TYPE.fromShortType(type);
    }

    public JsonObject getAlliances() {
        return alliances;
    }

    public void setAlliances(JsonObject alliances) {
        this.alliances = alliances;
    }

    public JsonArray getVideos() {
        return videos;
    }

    public void setVideos(JsonArray videos) {
        this.videos = videos;
    }

    public int getYear() {
        return year;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public long getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(long last_updated) {
        this.last_updated = last_updated;
    }

    public String getTitle(boolean lineBreak){
        if (type == TYPE.QUAL) {
            return LONG_TYPES.get(TYPE.QUAL) + (lineBreak?"\n":" ") + matchNumber;
        } else {
            return LONG_TYPES.get(type) + (lineBreak?"\n":" ") + setNumber + " - " + matchNumber;
        }
    }

    public String getTitle() {
        return getTitle(false);
    }

    public Integer getPlayOrder(){
        return PLAY_ORDER.get(type) * 1000000 + matchNumber * 1000 + setNumber;
    }

    /**
     * Renders a MatchListElement for displaying this match.
     * ASSUMES 3v3 match structure with red/blue alliances
     * Use different render methods for other structured
     *
     * @return A MatchListElement to be used to display this match
     */
    public MatchListElement render() {
        JsonArray redTeams = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                blueTeams = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
        int redScore = alliances.get("red").getAsJsonObject().get("score").getAsInt(),
                blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsInt();
        return new MatchListElement(videos.size()>0, getTitle(true),
                new String[]{redTeams.get(0).getAsString().substring(3), redTeams.get(1).getAsString().substring(3), redTeams.get(2).getAsString().substring(3)},
                new String[]{blueTeams.get(0).getAsString().substring(3), blueTeams.get(1).getAsString().substring(3), blueTeams.get(2).getAsString().substring(3)},
                redScore, blueScore, key);
    }

    @Override
    public ContentValues getParams(){
        ContentValues values = new ContentValues();
        return values;
    }

    public static boolean validateMatchKey(String key) {
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+\\_(?:qm|ef|qf\\dm|sf\\dm|f\\dm)\\d+$");
    }

    /**
     * Returns the match object of the match next to be played
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Next match
     */
    public static Match getNextMatchPlayed(ArrayList<Match> matches){
        for(Match m:matches){
            if(m.getAlliances().get("red").getAsJsonObject().get("score").getAsInt() == -1 &&
               m.getAlliances().get("blue").getAsJsonObject().get("score").getAsInt() == -1){
                //match is unplayed
                return m;
            }
        }
        //all matches have been played
        return null;
    }

    /**
     * Returns the match object of the last match played
     * @param matches ArrayList of matches. Assumes the list is sorted by play order
     * @return Last match played
     */
    public static Match getLastMatchPlayed(ArrayList<Match> matches){
        Match last = null;
        for(Match m:matches){
            if(m.getAlliances().get("red").getAsJsonObject().get("score").getAsInt() == -1 &&
               m.getAlliances().get("blue").getAsJsonObject().get("score").getAsInt() == -1){
                break;
            }else{
                last = m;
            }
        }
        return last;
    }

}
