package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IMatch;

import android.content.ContentValues;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class Match implements IMatch, TbaDatabaseModel, RenderableModel<Match> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE
    };

    private String alliances = null;
    private String compLevel = null;
    private String eventKey = null;
    private String key = null;
    private Long lastModified = null;
    private Integer matchNumber = null;
    private String scoreBreakdown = null;
    private Integer setNumber = null;
    private Long time = null;
    private String timeString = null;
    private String videos = null;

    private String selectedTeam;
    private int year;
    private MatchType type;
    private JsonObject alliancesObject;
    private JsonArray videosArray;
    private JsonObject breakdownObject;

    public Match() {
        year = -1;
        type = MatchType.NONE;
        alliancesObject = null;
        videosArray = null;
    }

    @Nullable @Override public String getAlliances() {
        return alliances;
    }

    @Override public void setAlliances(String alliances) {
        this.alliances = alliances;
    }

    @Override public String getCompLevel() {
        if (compLevel == null) {
            compLevel = MatchHelper.getMatchTypeFromKey(getKey());
        }
        return compLevel;
    }

    @Override public void setCompLevel(String compLevel) {
        this.compLevel = compLevel;
    }

    @Override public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override public Integer getMatchNumber() {
        return matchNumber;
    }

    @Override public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    @Nullable @Override public String getScoreBreakdown() {
        return scoreBreakdown;
    }

    @Override public void setScoreBreakdown(String scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    @Override public Integer getSetNumber() {
        return setNumber;
    }

    @Override public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    @Nullable @Override public Long getTime() {
        return time;
    }

    @Override public void setTime(Long time) {
        this.time = time;
    }

    @Nullable @Override public String getTimeString() {
        return timeString;
    }

    @Override public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Nullable @Override public String getVideos() {
        return videos;
    }

    @Override public void setVideos(String videos) {
        this.videos = videos;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public MatchType getType() {
        return type;
    }

    public void setType(MatchType type) {
        this.type = type;
    }

    @Override
    public String getEventKey() {
        String apiValue = eventKey;
        if (apiValue == null) {
            // Lazy load this
            String eventKey = MatchHelper.getEventKeyFromMatchKey(getKey());
            setEventKey(eventKey);
            return eventKey;
        }
        return apiValue;
    }

    public JsonObject getAlliancesJson() {
        if (alliancesObject == null) {
            alliancesObject = JSONHelper.getasJsonObject(getAlliances());
        }
        return alliancesObject;
    }

    public JsonArray getVideosJson() {
        if (videosArray == null) {
            videosArray = JSONHelper.getasJsonArray(getVideos());
        }
        return videosArray;
    }

    public JsonObject getScoreBreakdownJson() {
        if (breakdownObject == null) {
            breakdownObject = JSONHelper.getasJsonObject(getScoreBreakdown());
        }
        return breakdownObject;
    }

    public String getTitle(Resources resources, boolean lineBreak) {
        int matchNumber = getMatchNumber(),
                setNumber = getSetNumber();
        if (type == MatchType.QUAL) {
            return resources.getString(type.getTypeName()) + (lineBreak ? "\n" : " ") + matchNumber;
        } else {
            return resources.getString(type.getTypeName()) + (lineBreak ? "\n" : " ")
                   + setNumber + " - " + matchNumber;
        }
    }

    public String getTitle(Resources resources) {
        return getTitle(resources, false);
    }

    public Integer getDisplayOrder() {
        int matchNumber = getMatchNumber(),
                setNumber = getSetNumber();
        return type.getPlayOrder() * 1000000 + setNumber * 1000 + matchNumber;
    }

    public Integer getPlayOrder() {
        int matchNumber = getMatchNumber(),
                setNumber = getSetNumber();
        return type.getPlayOrder() * 1000000 + matchNumber * 1000 + setNumber;
    }

    public int getYear() {
        return Integer.parseInt(getKey().substring(0, 4));
    }

    public String getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(String selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public boolean didSelectedTeamWin() {
        if (selectedTeam.isEmpty()) {
            return false;
        }
        JsonObject alliances = getAlliancesJson();
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

    public void addToRecord(String teamKey, int[] currentRecord /* {win, loss, tie} */) {
        JsonObject alliances = getAlliancesJson();
        if (currentRecord == null || alliances == null ||
            !(alliances.has("red") && alliances.has("blue"))) {
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
    }

    private boolean hasBeenPlayed(int redScore, int blueScore) {
        return redScore >= 0 && blueScore >= 0;
    }

    public boolean hasBeenPlayed() {
        JsonObject alliances = getAlliancesJson();
        int redScore = getRedScore(alliances),
                blueScore = getBlueScore(alliances);

        return redScore >= 0 && blueScore >= 0;
    }

    @Override
    public ContentValues getParams() {
        ContentValues data = new ContentValues();
        data.put(MatchesTable.KEY, getKey());
        data.put(MatchesTable.MATCHNUM, getMatchNumber());
        data.put(MatchesTable.SETNUM, getSetNumber());
        data.put(MatchesTable.EVENT, getEventKey());
        data.put(MatchesTable.TIMESTRING, getTimeString());
        data.put(MatchesTable.TIME, getTime());
        data.put(MatchesTable.ALLIANCES, getAlliances());
        data.put(MatchesTable.VIDEOS, getVideos());
        data.put(MatchesTable.BREAKDOWN, getScoreBreakdown());
        return data;
    }

    @Override
    public ListElement render(ModelRendererSupplier rendererSupplier) {
        MatchRenderer renderer = (MatchRenderer)rendererSupplier.getRendererForType(ModelType.MATCH);
        if (renderer == null) {
            return null;
        }
        return renderer.renderFromModel(this, MatchRenderer.RENDER_DEFAULT);

    }
}
