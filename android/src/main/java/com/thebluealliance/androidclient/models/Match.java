package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listitems.MatchListElement;

import java.util.Date;


public class Match extends BasicModel<Match> {

    String key;
    String eventKey;
    String timeString;

    String selectedTeam;
    MatchHelper.TYPE type;
    JsonObject alliances;
    JsonArray videos;
    int year,
            matchNumber,
            setNumber;
    Date time;
    long last_updated;

    public Match() {
        super(Database.TABLE_MATCHES);
        this.key = "";
        this.eventKey = "";
        this.timeString = "";
        this.selectedTeam = "";
        this.time = new Date(0);
        this.type = MatchHelper.TYPE.NONE;
        this.alliances = new JsonObject();
        this.videos = new JsonArray();
        this.year = -1;
        this.matchNumber = -1;
        this.setNumber = -1;
        this.last_updated = -1;
    }

    public Match(String key, MatchHelper.TYPE type, int matchNumber, int setNumber, JsonObject alliances, String timeString, long timestamp, JsonArray videos, long last_updated) {
        super(Database.TABLE_MATCHES);
        if (!MatchHelper.validateMatchKey(key))
            throw new IllegalArgumentException("Invalid match key.");
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
        this.selectedTeam = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (!MatchHelper.validateMatchKey(key))
            throw new IllegalArgumentException("Invalid match key: " + key);
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

    public MatchHelper.TYPE getType() {
        return type;
    }

    public void setType(MatchHelper.TYPE type) {
        this.type = type;
    }

    public void setTypeFromShort(String type) {
        this.type = MatchHelper.TYPE.fromShortType(type);
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

    public String getTitle(boolean lineBreak) {
        if (type == MatchHelper.TYPE.QUAL) {
            return MatchHelper.LONG_TYPES.get(MatchHelper.TYPE.QUAL) + (lineBreak ? "\n" : " ") + matchNumber;
        } else {
            return MatchHelper.LONG_TYPES.get(type) + (lineBreak ? "\n" : " ") + setNumber + " - " + matchNumber;
        }
    }

    public String getTitle() {
        return getTitle(false);
    }

    public Integer getDisplayOrder() {
        return MatchHelper.PLAY_ORDER.get(type) * 1000000 + setNumber * 1000 + matchNumber;
    }
    public Integer getPlayOrder() {
        return MatchHelper.PLAY_ORDER.get(type) * 1000000 + matchNumber * 1000 + setNumber;
    }

    public String getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(String selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public boolean didSelectedTeamWin() {
        if (selectedTeam.isEmpty()) return false;
        JsonArray redTeams = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                blueTeams = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
        int redScore = alliances.get("red").getAsJsonObject().get("score").getAsInt(),
                blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsInt();

        if (redTeams.toString().contains(selectedTeam + "\"")) {
            return redScore > blueScore;
        } else if (blueTeams.toString().contains(selectedTeam + "\"")) {
            return blueScore > redScore;
        } else {
            // team did not play in match
            return false;
        }
    }

    public void addToRecord(String teamKey, int[] currentRecord /* {win, loss, tie} */) {
        if (currentRecord == null || alliances == null || !(alliances.has("red") && alliances.has("blue"))) {
            return;
        }
        JsonArray redTeams = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                blueTeams = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
        int redScore = alliances.get("red").getAsJsonObject().get("score").getAsInt(),
                blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsInt();

        if (hasBeenPlayed(redScore, blueScore)) {
            if (redTeams.toString().contains(teamKey + "\"")) {
                if (redScore > blueScore) {
                    currentRecord[0]++;
                } else if (redScore < blueScore) {
                    currentRecord[1]++;
                } else {
                    currentRecord[2]++;
                }
            } else if (blueTeams.toString().contains(teamKey + "\"")) {
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
        int redScore = alliances.get("red").getAsJsonObject().get("score").getAsInt(),
                blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsInt();

        return redScore >= 0 && blueScore >= 0;
    }

    @Override
    public void addFields(String... fields) {

    }

    /**
     * Renders a MatchListElement for displaying this match.
     * ASSUMES 3v3 match structure with red/blue alliances
     * Use different render methods for other structures
     *
     * @return A MatchListElement to be used to display this match
     */
    public MatchListElement render() {
        JsonArray redTeams = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray(),
                blueTeams = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
        String redScore = alliances.get("red").getAsJsonObject().get("score").getAsString(),
                blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsString();

        if (Integer.parseInt(redScore) < 0) redScore = "?";
        if (Integer.parseInt(blueScore) < 0) blueScore = "?";

        String youTubeVideoKey = null;
        for (int i = 0; i < videos.size(); i++) {
            JsonObject video = videos.get(i).getAsJsonObject();
            if (video.get("type").getAsString().equals("youtube")) {
                youTubeVideoKey = video.get("key").getAsString();
            }
        }

        String[] redAlliance, blueAlliance;
        // Add teams based on alliance size (or none if there isn't for some reason)
        if (redTeams.size() == 3) {
            redAlliance = new String[]{redTeams.get(0).getAsString().substring(3), redTeams.get(1).getAsString().substring(3), redTeams.get(2).getAsString().substring(3)};
        } else if (redTeams.size() == 2) {
            redAlliance = new String[]{redTeams.get(0).getAsString().substring(3), redTeams.get(1).getAsString().substring(3)};
        } else {
            redAlliance = new String[]{"", "", ""};
        }

        if (blueTeams.size() == 3) {
            blueAlliance = new String[]{blueTeams.get(0).getAsString().substring(3), blueTeams.get(1).getAsString().substring(3), blueTeams.get(2).getAsString().substring(3)};
        } else if (blueTeams.size() == 2) {
            blueAlliance = new String[]{blueTeams.get(0).getAsString().substring(3), blueTeams.get(1).getAsString().substring(3)};
        } else {
            blueAlliance = new String[]{"", "", ""};
        }

        return new MatchListElement(youTubeVideoKey, getTitle(true),
                redAlliance, blueAlliance,
                redScore, blueScore, key, selectedTeam);
    }

    @Override
    public ContentValues getParams() {
        ContentValues values = new ContentValues();
        values.put(Database.Matches.KEY, key);
        values.put(Database.Matches.EVENT, eventKey);
        values.put(Database.Matches.TIMESTRING, timeString);
        values.put(Database.Matches.TIME, time.getTime());
        values.put(Database.Matches.ALLIANCES, alliances.toString());
        values.put(Database.Matches.VIDEOS, videos.toString());
        return values;
    }

}