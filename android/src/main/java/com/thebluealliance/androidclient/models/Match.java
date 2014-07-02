package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.MatchListElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class Match extends BasicModel<Match> {

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

    public String getKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.KEY) && fields.get(Database.Matches.KEY) instanceof String) {
            return (String) fields.get(Database.Matches.KEY);
        }
        throw new FieldNotDefinedException("Field Database.Matches.KEY is not defined");
    }

    public void setKey(String key) {
        if (!MatchHelper.validateMatchKey(key))
            throw new IllegalArgumentException("Invalid match key: " + key);
        fields.put(Database.Matches.KEY, key);
        fields.put(Database.Matches.EVENT, key.split("_")[0]);

        this.year = Integer.parseInt(key.substring(0, 3));
        this.type = MatchHelper.TYPE.fromKey(key);
    }

    public String getEventKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.EVENT) && fields.get(Database.Matches.EVENT) instanceof String) {
            return (String) fields.get(Database.Matches.EVENT);
        }
        throw new FieldNotDefinedException("Field Database.Matches.EVENT is not defined");
    }

    public String getTimeString() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.TIMESTRING) && fields.get(Database.Matches.TIMESTRING) instanceof String) {
            return (String) fields.get(Database.Matches.TIMESTRING);
        }
        throw new FieldNotDefinedException("Field Database.Matches.TIMESTRING is not defined");
    }

    public void setTimeString(String timeString) {
        fields.put(Database.Matches.TIMESTRING, timeString);
    }

    public Date getTime() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.TIME) && fields.get(Database.Matches.TIME) instanceof Long) {
            return new Date((Long) fields.get(Database.Matches.KEY));
        }
        throw new FieldNotDefinedException("Field Database.Matches.TIME is not defined");
    }

    public void setTime(Date time) {
        fields.put(Database.Matches.TIME, time.getTime());
    }

    public void setTime(long timestamp) {
        fields.put(Database.Matches.TIME, timestamp);
    }

    public MatchHelper.TYPE getType() throws FieldNotDefinedException{
        if(type == MatchHelper.TYPE.NONE){
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

    public JsonObject getAlliances() throws FieldNotDefinedException{
        if(alliances != null){
            return alliances;
        }
        if(fields.containsKey(Database.Matches.ALLIANCES) && fields.get(Database.Matches.ALLIANCES) instanceof String) {
            alliances = JSONManager.getasJsonObject((String) fields.get(Database.Matches.ALLIANCES));
            return alliances;
        }
        throw new FieldNotDefinedException("Field Database.Matches.ALLIANCES is not defined");
    }

    public void setAlliances(JsonObject alliances) {
        fields.put(Database.Matches.ALLIANCES, alliances.toString());
        this.alliances = alliances;
    }

    public void setAlliances(String allianceJson){
        fields.put(Database.Matches.ALLIANCES, allianceJson);
    }

    public JsonArray getVideos() throws FieldNotDefinedException{
        if(videos != null){
            return videos;
        }
        if(fields.containsKey(Database.Matches.VIDEOS) && fields.get(Database.Matches.VIDEOS) instanceof String) {
            videos = JSONManager.getasJsonArray((String) fields.get(Database.Matches.VIDEOS));
            return videos;
        }
        throw new FieldNotDefinedException("Field Database.Matches.VIDEOS is not defined");
    }

    public void setVideos(JsonArray videos) {
        fields.put(Database.Matches.VIDEOS, videos.toString());
        this.videos = videos;
    }

    public void setVideos(String videosJson){
        fields.put(Database.Matches.VIDEOS, videosJson);
    }

    public int getYear() throws FieldNotDefinedException{
        if(year == -1){
            throw new FieldNotDefinedException("Fields Database.Matches.KEY is not defined");
        }
        return year;
    }

    public int getMatchNumber() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.MATCHNUM) && fields.get(Database.Matches.MATCHNUM) instanceof Integer) {
            return (Integer) fields.get(Database.Matches.MATCHNUM);
        }
        throw new FieldNotDefinedException("Field Database.Matches.MATCHNUM is not defined");
    }

    public void setMatchNumber(int matchNumber) {
        fields.put(Database.Matches.MATCHNUM, matchNumber);
    }

    public int getSetNumber() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Matches.SETNUM) && fields.get(Database.Matches.SETNUM) instanceof Integer) {
            return (Integer) fields.get(Database.Matches.SETNUM);
        }
        throw new FieldNotDefinedException("Field Database.Matches.MATCHNUM is not defined");
    }

    public void setSetNumber(int setNumber) {
        fields.put(Database.Matches.SETNUM, setNumber);
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
        }catch (FieldNotDefinedException e){
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
        }catch (FieldNotDefinedException e){
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
        }catch (FieldNotDefinedException e){
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
        }catch (FieldNotDefinedException e){
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
        }catch (FieldNotDefinedException e){
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
            int redScore = alliances.get("red").getAsJsonObject().get("score").getAsInt(),
                    blueScore = alliances.get("blue").getAsJsonObject().get("score").getAsInt();

            return redScore >= 0 && blueScore >= 0;
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Required fields for title not present\n" +
                    "Required: Database.Matches.ALLIANCES");
            return false;
        }
    }

    /**
     * Renders a MatchListElement for displaying this match.
     * ASSUMES 3v3 match structure with red/blue alliances
     * Use different render methods for other structures
     *
     * @return A MatchListElement to be used to display this match
     */
    public MatchListElement render() {
        try {
            JsonObject alliances = getAlliances();
            JsonArray videos = getVideos();
            String key = getKey();
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
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Required fields for rendering not present\n" +
                    "Required: Database.Matches.ALLIANCES, Database.Matches.VIDEOS, Database.Matches.KEY, Database.Matches.MATCHNUM, Database.Matches.SETNUM");
            return null;
        }
    }

    public static synchronized APIResponse<Match> query(Context c, String key, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying matches table: "+whereClause+ Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_MATCHES, fields, whereClause, whereArgs, null, null, null, null);
        Match match;
        if(cursor != null && cursor.moveToFirst()){
            match = ModelInflater.inflateMatch(cursor);
        }else{
            match = new Match();
        }

        APIResponse.CODE code = forceFromCache?APIResponse.CODE.LOCAL: APIResponse.CODE.CACHED304;
        ArrayList<Match> allMatches = null;
        boolean changed = false;
        allMatches = new ArrayList<>();
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Match updatedMatch = new Match();
                if(url.contains("event") && url.contains("matches")){
                    /* We're requesting the matches for the whole event (there isn't a single match endpoint */
                    JsonArray matchList = JSONManager.getasJsonArray(response.getData());
                    for(JsonElement m: matchList){
                        Match inflated = JSONManager.getGson().fromJson(m, Match.class);
                        if(m.getAsJsonObject().get("key").getAsString().equals(key)){
                            updatedMatch = inflated;
                            //this match will be added to the list below
                        }else{
                            allMatches.add(inflated);
                        }
                    }
                }else {
                    updatedMatch = JSONManager.getGson().fromJson(response.getData(), Match.class);
                }
                match.merge(updatedMatch);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        allMatches.add(match);

        if(changed){
            Database.getInstance(c).getMatchesTable().add(allMatches);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? "+changed);
        return new APIResponse<>(match, code);
    }

    public static synchronized APIResponse<ArrayList<Match>> queryList(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying matches table: "+whereClause+ Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_MATCHES, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Match> matches = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()){
            do{
                matches.add(ModelInflater.inflateMatch(cursor));
            }while(cursor.moveToNext());
        }

        APIResponse.CODE code = forceFromCache?APIResponse.CODE.LOCAL: APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray matchList = JSONManager.getasJsonArray(response.getData());
                matches = new ArrayList<>();
                for(JsonElement m: matchList){
                    matches.add(JSONManager.getGson().fromJson(m, Match.class));
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            Database.getInstance(c).getMatchesTable().add(matches);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found "+matches.size()+" matches, updated in db? "+changed);
        return new APIResponse<>(matches, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getMatchesTable().add(this);
    }
}