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

import android.content.ContentValues;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.util.ArrayList;


public class Match extends com.thebluealliance.api.model.Match implements TbaDatabaseModel,
                                                                          RenderableModel<Match> {

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
        year = -1;
        type = MatchType.NONE;
        alliances = null;
        videos = null;
    }

    @Override
    public String getEventKey() {
        String apiValue = super.getEventKey();
        if (apiValue == null) {
            // Lazy load this
            String eventKey = MatchHelper.getEventKeyFromMatchKey(getKey());
            setEventKey(eventKey);
            return eventKey;
        }
        return apiValue;
    }

    public JsonObject getAlliancesJson() {
        if (alliances == null) {
            alliances = JSONHelper.getasJsonObject(getAlliances());
        }
        return alliances;
    }

    public JsonArray getVideosJson() {
        if (videos == null) {
            videos = JSONHelper.getasJsonArray(getVideos());
        }
        return videos;
    }

    public JsonObject getScoreBreakdownJson() {
        if (breakdown == null) {
            breakdown = JSONHelper.getasJsonObject(getScoreBreakdown());
        }
        return breakdown;
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
