package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.thebluealliance.androidclient.Log;

import java.util.ArrayList;
import java.util.Date;


public class Match extends BasicModel<Match> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE
    };

    private String selectedTeam;
    private int year;
    private MatchType type;
    private JsonObject alliances;
    private JsonArray videos;
    private JsonObject breakdown;

    public Match() {
        super(Database.TABLE_MATCHES, ModelType.MATCH);
        year = -1;
        type = MatchType.NONE;
        alliances = null;
        videos = null;
    }

    public String getKey() {
        if (fields.containsKey(MatchesTable.KEY) && fields.get(MatchesTable.KEY) instanceof String) {
            return (String) fields.get(MatchesTable.KEY);
        }
        return "";
    }

    public void setKey(String key) {
        if (!MatchHelper.validateMatchKey(key))
            throw new IllegalArgumentException("Invalid match key: " + key);
        fields.put(MatchesTable.KEY, key);
        fields.put(MatchesTable.EVENT, key.split("_")[0]);

        this.year = Integer.parseInt(key.substring(0, 4));
        this.type = MatchType.fromKey(key);
    }

    public String getEventKey() throws FieldNotDefinedException {
        if (fields.containsKey(MatchesTable.EVENT) && fields.get(MatchesTable.EVENT) instanceof String) {
            return (String) fields.get(MatchesTable.EVENT);
        }
        throw new FieldNotDefinedException("Field Database.Matches.EVENT is not defined");
    }

    public String getTimeString() throws FieldNotDefinedException {
        if (fields.containsKey(MatchesTable.TIMESTRING) && fields.get(MatchesTable.TIMESTRING) instanceof String) {
            return (String) fields.get(MatchesTable.TIMESTRING);
        }
        throw new FieldNotDefinedException("Field Database.Matches.TIMESTRING is not defined");
    }

    public void setTimeString(String timeString) {
        fields.put(MatchesTable.TIMESTRING, timeString);
    }

    public Date getTime() throws FieldNotDefinedException {
        if (fields.containsKey(MatchesTable.TIME) && fields.get(MatchesTable.TIME) instanceof Long) {
            return new Date((Long) fields.get(MatchesTable.TIME));
        }
        throw new FieldNotDefinedException("Field Database.Matches.TIME is not defined");
    }

    public long getTimeMillis() throws FieldNotDefinedException {
        return getTime().getTime();
    }

    public void setTime(Date time) {
        fields.put(MatchesTable.TIME, time.getTime());
    }

    public void setTime(long timestamp) {
        fields.put(MatchesTable.TIME, timestamp);
    }

    public MatchType getMatchType() throws FieldNotDefinedException {
        if (type == MatchType.NONE) {
            throw new FieldNotDefinedException("Field Database.Matches.KEY is not defined");
        }
        return type;
    }

    public void setType(MatchType type) {
        this.type = type;
    }

    public void setTypeFromShort(String type) {
        this.type = MatchType.fromShortType(type);
    }

    public JsonObject getBreakdown() throws FieldNotDefinedException {
        if (breakdown != null) {
            return breakdown;
        }
        if (fields.containsKey(MatchesTable.BREAKDOWN) && fields.get(MatchesTable.BREAKDOWN) instanceof String) {
            breakdown = JSONHelper.getasJsonObject((String) fields.get(MatchesTable.BREAKDOWN));
            return breakdown;
        }
        throw new FieldNotDefinedException("Field Database.Matches.BREAKDOWN is not defined");
    }

    public void setBreakdown(String breakdown) {
        fields.put(MatchesTable.BREAKDOWN, breakdown);
    }

    public void setBreakdown(JsonObject breakdown) {
        fields.put(MatchesTable.BREAKDOWN, breakdown.toString());
        this.breakdown = breakdown;
    }

    public JsonObject getAlliances() throws FieldNotDefinedException {
        if (alliances != null) {
            return alliances;
        }
        if (fields.containsKey(MatchesTable.ALLIANCES) && fields.get(MatchesTable.ALLIANCES) instanceof String) {
            alliances = JSONHelper.getasJsonObject((String) fields.get(MatchesTable.ALLIANCES));
            return alliances;
        }
        throw new FieldNotDefinedException("Field Database.Matches.ALLIANCES is not defined");
    }

    public void setAlliances(JsonObject alliances) {
        fields.put(MatchesTable.ALLIANCES, alliances.toString());
        this.alliances = alliances;
    }

    public void setAlliances(String allianceJson) {
        fields.put(MatchesTable.ALLIANCES, allianceJson);
    }

    public static JsonObject getRedAlliance(JsonObject alliances) {
        return alliances.getAsJsonObject("red");
    }

    public static JsonObject getBlueAlliance(JsonObject alliances) {
        return alliances.getAsJsonObject("blue");
    }

    public static int getRedScore(JsonObject alliances) {
        return getRedAlliance(alliances).get("score").getAsInt();
    }

    public static int getBlueScore(JsonObject alliances) {
        return getBlueAlliance(alliances).get("score").getAsInt();
    }

    public static JsonArray getRedTeams(JsonObject alliances) {
        return getRedAlliance(alliances).getAsJsonArray("teams");
    }

    public static JsonArray getBlueTeams(JsonObject alliances) {
        return getBlueAlliance(alliances).getAsJsonArray("teams");
    }

    /** @return team keys from {@link #getRedTeams} or {@link #getBlueTeams}. */
    @NonNull
    public static ArrayList<String> teamKeys(JsonArray teamsJson) {
        ArrayList<String> teamKeys = new ArrayList<>(teamsJson.size());

        for (JsonElement key : teamsJson) {
            teamKeys.add(key.getAsString());
        }
        return teamKeys;
    }

    /** @return team number strings from {@link #getRedTeams} or {@link #getBlueTeams}. */
    @NonNull
    public static ArrayList<String> teamNumbers(JsonArray teamsJson) {
        ArrayList<String> teamKeys = teamKeys(teamsJson);
        ArrayList<String> teamNumbers = new ArrayList<>(teamKeys.size());

        for (String key : teamKeys) {
            teamNumbers.add(key.replace("frc", ""));
        }
        return teamNumbers;
    }

    /**
     * @return true if the given team array contains the given team key, e.g. "frc111".
     */
    public static boolean hasTeam(JsonArray teams, String teamKey) {
        return teams.contains(new JsonPrimitive(teamKey));
    }

    public JsonArray getVideos() throws FieldNotDefinedException {
        if (videos != null) {
            return videos;
        }
        if (fields.containsKey(MatchesTable.VIDEOS) && fields.get(MatchesTable.VIDEOS) instanceof String) {
            videos = JSONHelper.getasJsonArray((String) fields.get(MatchesTable.VIDEOS));
            return videos;
        }
        throw new FieldNotDefinedException("Field Database.Matches.VIDEOS is not defined");
    }

    public void setVideos(JsonArray videos) {
        fields.put(MatchesTable.VIDEOS, videos.toString());
        this.videos = videos;
    }

    public void setVideos(String videosJson) {
        fields.put(MatchesTable.VIDEOS, videosJson);
    }

    public int getYear() throws FieldNotDefinedException {
        if (year == -1) {
            throw new FieldNotDefinedException("Fields Database.Matches.KEY is not defined");
        }
        return year;
    }

    public int getMatchNumber() throws FieldNotDefinedException {
        if (fields.containsKey(MatchesTable.MATCHNUM) && fields.get(MatchesTable.MATCHNUM) instanceof Integer) {
            return (Integer) fields.get(MatchesTable.MATCHNUM);
        }
        throw new FieldNotDefinedException("Field Database.Matches.MATCHNUM is not defined");
    }

    public void setMatchNumber(int matchNumber) {
        fields.put(MatchesTable.MATCHNUM, matchNumber);
    }

    public int getSetNumber() throws FieldNotDefinedException {
        if (fields.containsKey(MatchesTable.SETNUM) && fields.get(MatchesTable.SETNUM) instanceof Integer) {
            return (Integer) fields.get(MatchesTable.SETNUM);
        }
        throw new FieldNotDefinedException("Field Database.Matches.MATCHNUM is not defined");
    }

    public void setSetNumber(int setNumber) {
        fields.put(MatchesTable.SETNUM, setNumber);
    }

    public String getTitle(Resources resources, boolean lineBreak) {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            if (type == MatchType.QUAL) {
                return resources.getString(type.getTypeName()) + (lineBreak ? "\n" : " ") + matchNumber;
            } else {
                return resources.getString(type.getTypeName())+ (lineBreak ? "\n" : " ")
                        + setNumber + " - " + matchNumber;
            }
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields for title not present\n"
                    + "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return null;
        }
    }

    public String getTitle(Resources resources) {
        return getTitle(resources, false);
    }

    public Integer getDisplayOrder() {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            return type.getPlayOrder() * 1000000 + setNumber * 1000 + matchNumber;
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields for display order not present\n"
                    + "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return 1000000;
        }
    }

    public Integer getPlayOrder() {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            return type.getPlayOrder() * 1000000 + matchNumber * 1000 + setNumber;
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields for display order not present\n"
                  + "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return null;
        }
    }

    public String getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(String selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public boolean didSelectedTeamWin() {
        if (selectedTeam.isEmpty()) return false;
        try {
            JsonObject alliances = getAlliances();
            JsonArray redTeams = getRedTeams(alliances),
                    blueTeams = getBlueTeams(alliances);
            int redScore = getRedScore(alliances),
                    blueScore = getBlueScore(alliances);

            if (Match.hasTeam(redTeams, selectedTeam)) {
                return redScore > blueScore;
            } else if (Match.hasTeam(blueTeams, selectedTeam)) {
                return blueScore > redScore;
            } else {
                // team did not play in match
                return false;
            }
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields not present\n"
                    + "Required: Database.Matches.ALLIANCES");
            return false;
        }
    }

    public void addToRecord(String teamKey, int[] currentRecord /* {win, loss, tie} */) {
        try {
            JsonObject alliances = getAlliances();
            if (currentRecord == null || alliances == null || !(alliances.has("red") && alliances.has("blue"))) {
                return;
            }
            JsonArray redTeams = getRedTeams(alliances),
                    blueTeams = getBlueTeams(alliances);
            int redScore = getRedScore(alliances),
                    blueScore = getBlueScore(alliances);

            if (hasBeenPlayed(redScore, blueScore)) {
                if (Match.hasTeam(redTeams, teamKey)) {
                    if (redScore > blueScore) {
                        currentRecord[0]++;
                    } else if (redScore < blueScore) {
                        currentRecord[1]++;
                    } else {
                        currentRecord[2]++;
                    }
                } else if (Match.hasTeam(blueTeams, teamKey)) {
                    if (blueScore > redScore) {
                        currentRecord[0]++;
                    } else if (blueScore < redScore) {
                        currentRecord[1]++;
                    } else {
                        currentRecord[2]++;
                    }
                }
            }
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields not present\n"
                    + "Required: Database.Matches.ALLIANCES");
        }
    }

    private boolean hasBeenPlayed(int redScore, int blueScore) {
        return redScore >= 0 && blueScore >= 0;
    }

    public boolean hasBeenPlayed() {
        try {
            JsonObject alliances = getAlliances();
            int redScore = getRedScore(alliances),
                    blueScore = getBlueScore(alliances);

            return redScore >= 0 && blueScore >= 0;
        } catch (FieldNotDefinedException e) {
            Log.w("Required fields for title not present\n"
                  + "Required: Database.Matches.ALLIANCES");
            return false;
        }
    }
}
