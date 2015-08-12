package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listitems.MatchListElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class Match extends BasicModel<Match> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE
    };

    private String selectedTeam;
    private int year;
    private MatchHelper.TYPE type;
    private JsonObject alliances;
    private JsonArray videos;

    public Match() {
        super(Database.TABLE_MATCHES);
        year = -1;
        type = MatchHelper.TYPE.NONE;
        alliances = null;
        videos = null;
    }

    public Match(String key, MatchHelper.TYPE type, int matchNumber, int setNumber, JsonObject alliances, String timeString, long timestamp, JsonArray videos, long last_updated) {
        super(Database.TABLE_MATCHES);
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
        this.type = MatchHelper.TYPE.fromKey(key);
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

    public MatchHelper.TYPE getType() throws FieldNotDefinedException {
        if (type == MatchHelper.TYPE.NONE) {
            throw new FieldNotDefinedException("Field Database.Matches.KEY is not defined");
        }
        return type;
    }

    public void setType(MatchHelper.TYPE type) {
        this.type = type;
    }

    public void setTypeFromShort(String type) {
        this.type = MatchHelper.TYPE.fromShortType(type);
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

    public String getTitle(boolean lineBreak) {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            if (type == MatchHelper.TYPE.QUAL) {
                return MatchHelper.LONG_TYPES.get(MatchHelper.TYPE.QUAL) + (lineBreak ? "\n" : " ") + matchNumber;
            } else {
                return MatchHelper.LONG_TYPES.get(type) + (lineBreak ? "\n" : " ") + setNumber + " - " + matchNumber;
            }
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields for title not present\n" +
                    "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return null;
        }
    }

    public String getTitle() {
        return getTitle(false);
    }

    public Integer getDisplayOrder() {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            return MatchHelper.PLAY_ORDER.get(type) * 1000000 + setNumber * 1000 + matchNumber;
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields for display order not present\n" +
                    "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return 1000000;
        }
    }

    public Integer getPlayOrder() {
        try {
            int matchNumber = getMatchNumber(),
                    setNumber = getSetNumber();
            return MatchHelper.PLAY_ORDER.get(type) * 1000000 + matchNumber * 1000 + setNumber;
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields for display order not present\n" +
                    "Required: Database.Matches.MATCHNUM, Database.Matches.SETNUM");
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
            Log.w(Constants.LOG_TAG, "Required fields not present\n" +
                    "Required: Database.Matches.ALLIANCES");
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
            Log.w(Constants.LOG_TAG, "Required fields not present\n" +
                    "Required: Database.Matches.ALLIANCES");
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
            Log.w(Constants.LOG_TAG, "Required fields for title not present\n" +
                    "Required: Database.Matches.ALLIANCES");
            return false;
        }
    }

    public MatchListElement render() {
        return render(true, false, true, true);
    }

    /**
     * Renders a MatchListElement for displaying this match. ASSUMES 3v3 match structure with
     * red/blue alliances Use different render methods for other structures
     *
     * @return A MatchListElement to be used to display this match
     */
    public MatchListElement render(boolean showVideo, boolean showHeaders, boolean showMatchTitle, boolean clickable) {
        JsonObject alliances = null;
        try {
            alliances = getAlliances();
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required field for match render: Database.Matches.ALLIANCES");
            return null;
        }
        JsonArray videos = null;
        try {
            videos = getVideos();
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required field for match render: Database.Matches.VIDEOS. Defaulting to none.");
            videos = new JsonArray();
        }
        String key = getKey();
        if (key.isEmpty()) {
            return null;
        }

        JsonArray redTeams = getRedTeams(alliances),
                blueTeams = getBlueTeams(alliances);
        String redScore = getRedAlliance(alliances).get("score").getAsString(),
                blueScore = getBlueAlliance(alliances).get("score").getAsString();

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

        long matchTime;
        try {
            matchTime = getTimeMillis();
        } catch (FieldNotDefinedException e) {
            matchTime = -1;
        }

        return new MatchListElement(youTubeVideoKey, getTitle(true),
                redAlliance, blueAlliance,
                redScore, blueScore, key, matchTime, selectedTeam, showVideo, showHeaders, showMatchTitle, clickable);
    }

    public static APIResponse<Match> query(Context c, String key, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying matches table: " + whereClause + Arrays.toString(whereArgs));
        MatchesTable table = Database.getInstance(c).getMatchesTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        Match match;
        if (cursor != null && cursor.moveToFirst()) {
            match = table.inflate(cursor);
            cursor.close();
        } else {
            match = new Match();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        ArrayList<Match> allMatches = new ArrayList<>();
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Match updatedMatch = new Match();
                if (url.contains("event") && url.contains("matches")) {
                    /* We're requesting the matches for the whole event (there isn't a single match endpoint */
                    JsonArray matchList = JSONHelper.getasJsonArray(response.getData());
                    for (JsonElement m : matchList) {
                        Match inflated = JSONHelper.getGson().fromJson(m, Match.class);
                        if (m.getAsJsonObject().get("key").getAsString().equals(key)) {
                            updatedMatch = inflated;
                            //this match will be added to the list below
                        } else {
                            allMatches.add(inflated);
                        }
                    }
                } else {
                    updatedMatch = JSONHelper.getGson().fromJson(response.getData(), Match.class);
                }
                match.merge(updatedMatch);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        allMatches.add(match);

        if (changed) {
            Database.getInstance(c).getMatchesTable().add(allMatches);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(match, code);
    }

    public static APIResponse<ArrayList<Match>> queryList(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying matches table: " + whereClause + Arrays.toString(whereArgs));
        MatchesTable table = Database.getInstance(c).getMatchesTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Match> allMatches = new ArrayList<>(),
                storedMatches = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                storedMatches.add(table.inflate(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;

        for (String url : apiUrls) {
            /* Hit each API URL requested */
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);

            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                /* If we get back data, parse it */
                JsonArray matchList = JSONHelper.getasJsonArray(response.getData());
                allMatches = new ArrayList<>();
                for (JsonElement m : matchList) {
                    Match match = JSONHelper.getGson().fromJson(m, Match.class);
                    allMatches.add(match);
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            /* Add the new matches to the local db, after deleting the old ones */
            MatchesTable matchTable = Database.getInstance(c).getMatchesTable();
            int deleted = matchTable.delete(whereClause, whereArgs);
            matchTable.add(allMatches);

            Log.d(Constants.DATAMANAGER_LOG, "Downloaded " + allMatches.size() + " matches, deleted " + deleted);
            return new APIResponse<>(allMatches, code);
        } else {
            Log.d(Constants.DATAMANAGER_LOG, "No new matches.");
            return new APIResponse<>(new ArrayList<>(storedMatches), code);
        }
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getMatchesTable().add(this);
    }
}
