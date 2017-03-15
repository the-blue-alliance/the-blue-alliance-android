package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IMatch;
import com.thebluealliance.api.model.IMatchAlliancesContainer;
import com.thebluealliance.api.model.IMatchVideo;

import android.content.ContentValues;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class Match implements IMatch, TbaDatabaseModel, RenderableModel<Match> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.MATCH_VIDEO,
    };

    private String key;
    private String eventKey;
    private String compLevel;
    private Integer matchNumber;
    private Integer setNumber;

    private @Nullable IMatchAlliancesContainer alliances;
    private @Nullable String scoreBreakdown;
    private @Nullable List<IMatchVideo> videos;
    private @Nullable Long time;
    private @Nullable Long actualTime;
    private @Nullable String winningAlliance;
    private @Nullable Long lastModified;

    // Other variables
    private String selectedTeam;

    public static String[] getNotificationTypes() {
        return NOTIFICATION_TYPES;
    }

    @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Override public String getEventKey() {
        return eventKey;
    }

    @Override public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Override public String getCompLevel() {
        return compLevel;
    }

    @Override public void setCompLevel(String compLevel) {
        this.compLevel = compLevel;
    }

    @Override public Integer getMatchNumber() {
        return matchNumber;
    }

    @Override public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    @Override public Integer getSetNumber() {
        return setNumber;
    }

    @Override public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    @Override @Nullable public IMatchAlliancesContainer getAlliances() {
        return alliances;
    }

    @Override public void setAlliances(@Nullable IMatchAlliancesContainer alliances) {
        this.alliances = alliances;
    }

    @Override @Nullable public String getScoreBreakdown() {
        return scoreBreakdown;
    }

    @Override public void setScoreBreakdown(@Nullable String scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    @Override @Nullable public List<IMatchVideo> getVideos() {
        return videos;
    }

    @Override public void setVideos(@Nullable List<IMatchVideo> videos) {
        this.videos = videos;
    }

    @Override @Nullable public Long getTime() {
        return time;
    }

    @Override public void setTime(@Nullable Long time) {
        this.time = time;
    }

    @Override @Nullable public Long getActualTime() {
        return actualTime;
    }

    @Override public void setActualTime(@Nullable Long actualTime) {
        this.actualTime = actualTime;
    }

    @Override @Nullable public String getWinningAlliance() {
        return winningAlliance;
    }

    @Override public void setWinningAlliance(@Nullable String winningAlliance) {
        this.winningAlliance = winningAlliance;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }

    public MatchType getType() {
        return MatchType.fromKey(getKey());
    }

    public String getTitle(Resources resources, boolean lineBreak) {
        int matchNumber = getMatchNumber();
        int setNumber = getSetNumber();
        MatchType matchType = getType();
        if (matchType == MatchType.QUAL) {
            return resources.getString(matchType.getTypeName()) + (lineBreak ? "\n" : " ") + matchNumber;
        } else {
            return resources.getString(matchType.getTypeName()) + (lineBreak ? "\n" : " ")
                   + setNumber + " - " + matchNumber;
        }
    }

    public String getTitle(Resources resources) {
        return getTitle(resources, false);
    }

    public Integer getDisplayOrder() {
        int matchNumber = getMatchNumber(),
                setNumber = getSetNumber();
        return getType().getPlayOrder() * 1000000 + setNumber * 1000 + matchNumber;
    }

    public Integer getPlayOrder() {
        int matchNumber = getMatchNumber(),
                setNumber = getSetNumber();
        return getType().getPlayOrder() * 1000000 + matchNumber * 1000 + setNumber;
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
        if (selectedTeam.isEmpty() || alliances == null || winningAlliance == null || winningAlliance.isEmpty()) {
            return false;
        }

        if ("red".equals(winningAlliance)) {
            return getRedTeams(alliances).contains(selectedTeam);
        } else if ("blue".equals(winningAlliance)) {
            return getBlueTeams(alliances).contains(selectedTeam);
        } else {
            return false;
        }
    }

    public static Integer getRedScore(IMatchAlliancesContainer alliances) {
        return alliances.getRed().getScore();
    }

    public static Integer getBlueScore(IMatchAlliancesContainer alliances) {
        return alliances.getBlue().getScore();
    }

    public static List<String> getRedTeams(IMatchAlliancesContainer alliances) {
        return alliances.getRed().getTeamKeys();
    }

    public static List<String> getBlueTeams(IMatchAlliancesContainer alliances) {
        return alliances.getBlue().getTeamKeys();
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
    public static ArrayList<String> teamNumbers(List<String> teamKeys) {
        ArrayList<String> teamNumbers = new ArrayList<>(teamKeys.size());

        for (String key : teamKeys) {
            teamNumbers.add(key.replace("frc", ""));
        }
        return teamNumbers;
    }

    public void addToRecord(String teamKey, int[] currentRecord /* {win, loss, tie} */) {
        if (alliances == null || alliances.getBlue() == null || alliances.getRed() == null) {
            return;
        }
        List<String> redTeams = getRedTeams(alliances),
                     blueTeams = getBlueTeams(alliances);
        int redScore = getRedScore(alliances),
            blueScore = getBlueScore(alliances);

        if (hasBeenPlayed(redScore, blueScore)) {
            if (redTeams.contains(teamKey)) {
                if ("red".equals(winningAlliance)) {
                    currentRecord[0]++;
                } else if ("blue".equals(winningAlliance)) {
                    currentRecord[1]++;
                } else {
                    currentRecord[2]++;
                }
            } else if (blueTeams.contains(teamKey)) {
                if ("blue".equals(winningAlliance)) {
                    currentRecord[0]++;
                } else if ("red".equals(winningAlliance)) {
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
        int redScore = getRedScore(alliances);
        int blueScore = getBlueScore(alliances);

        return redScore >= 0 && blueScore >= 0;
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues data = new ContentValues();
        data.put(MatchesTable.KEY, getKey());
        data.put(MatchesTable.MATCHNUM, getMatchNumber());
        data.put(MatchesTable.SETNUM, getSetNumber());
        data.put(MatchesTable.EVENT, getEventKey());
        data.put(MatchesTable.TIME, getTime());
        data.put(MatchesTable.ALLIANCES, gson.toJson(alliances, IMatchAlliancesContainer.class));
        data.put(MatchesTable.WINNER, getWinningAlliance());
        data.put(MatchesTable.VIDEOS, gson.toJson(videos, new TypeToken<List<IMatchVideo>>(){}.getType()));
        data.put(MatchesTable.BREAKDOWN, getScoreBreakdown());
        data.put(MatchesTable.LAST_MODIFIED, getLastModified());
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

    public static class MatchVideo implements IMatchVideo {
        private String key;
        private String type;

        @Override public String getKey() {
            return key;
        }

        @Override public void setKey(String key) {
            this.key = key;
        }

        @Override public String getType() {
            return type;
        }

        @Override public void setType(String type) {
            this.type = type;
        }

        public Media asMedia() {
            Media media = new Media();
            media.setForeignKey(getKey());
            media.setType(getType());
            return media;
        }
    }
}
