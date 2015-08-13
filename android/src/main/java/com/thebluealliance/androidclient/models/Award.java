package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.helpers.AwardHelper;
import com.thebluealliance.androidclient.listitems.AwardListElement;

import java.util.ArrayList;
import java.util.Arrays;

public class Award extends BasicModel<Award> {

    private JsonArray winners;

    public Award() {
        super(Database.TABLE_AWARDS);
        winners = null;
    }

    public Award(String eventKey, String name, int year, JsonArray winners) {
        this();
        setEventKey(eventKey);
        setName(name);
        setYear(year);
        setWinners(winners);
    }

    public void setKey(String key) {
        if (AwardHelper.validateAwardKey(key)) {
            fields.put(AwardsTable.KEY, key);
        } else {
            throw new IllegalArgumentException("Invalid award key: " + key);
        }
    }

    public String getKey() {
        if (fields.containsKey(AwardsTable.KEY) && fields.get(AwardsTable.KEY) instanceof String) {
            return (String) fields.get(AwardsTable.KEY);
        } else {
            try {
                String newKey = AwardHelper.createAwardKey(getEventKey(), getEnum());
                setKey(newKey);
                return newKey;
            } catch (FieldNotDefinedException e) {
                return "";
            }
        }
    }

    public void generateKey() {

    }

    public int getEnum() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.ENUM) && fields.get(AwardsTable.ENUM) instanceof String) {
            return (Integer) fields.get(AwardsTable.ENUM);
        }
        throw new FieldNotDefinedException("Field Database.Awards.ENUM is not defined");
    }

    public void setEnum(int awardEnum) {
        fields.put(AwardsTable.ENUM, awardEnum);
    }

    public JsonArray getWinners() throws FieldNotDefinedException {
        if (winners != null) {
            return winners;
        }
        if (fields.containsKey(AwardsTable.WINNERS) && fields.get(AwardsTable.WINNERS) instanceof String) {
            winners = JSONHelper.getasJsonArray((String) fields.get(AwardsTable.WINNERS));
            return winners;
        }
        throw new FieldNotDefinedException("Field Database.Awards.WINNERS is not defined");
    }

    public void setWinners(JsonArray winners) {
        fields.put(AwardsTable.WINNERS, winners.toString());
        this.winners = winners;
    }

    public void setWinners(String winnersJson) {
        fields.put(AwardsTable.WINNERS, winnersJson);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.YEAR) && fields.get(AwardsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(AwardsTable.YEAR);
        }
        throw new FieldNotDefinedException("Field Database.Awards.YEAR is not defined");
    }

    public void setYear(int year) {
        fields.put(AwardsTable.YEAR, year);
    }

    public String getName() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.NAME) && fields.get(AwardsTable.NAME) instanceof String) {
            return (String) fields.get(AwardsTable.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Awards.NAME is not defined");
    }

    public void setName(String name) {
        fields.put(AwardsTable.NAME, name);
    }

    public String getEventKey() throws FieldNotDefinedException {
        if (fields.containsKey(AwardsTable.EVENTKEY) && fields.get(AwardsTable.EVENTKEY) instanceof String) {
            return (String) fields.get(AwardsTable.EVENTKEY);
        }
        throw new FieldNotDefinedException("Field Database.Awards.EVENTKEY is not defined");
    }

    public void setEventKey(String eventKey) {
        fields.put(AwardsTable.EVENTKEY, eventKey);
    }

    public ArrayList<Award> splitByWinner() throws FieldNotDefinedException {
        ArrayList<Award> out = new ArrayList<>();
        String eventKey = getEventKey(),
                name = getName();
        int year = getYear();
        JsonArray winners = getWinners();
        for (JsonElement winner : winners) {
            JsonArray winnerArray = new JsonArray();
            winnerArray.add(winner);
            out.add(new Award(eventKey, name, year, winnerArray));
        }
        return out;
    }

    public static APIResponse<Award> query(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying awards table: " + whereClause + Arrays.toString(whereArgs));
        AwardsTable table = Database.getInstance(c).getAwardsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        Award award;
        if (cursor != null && cursor.moveToFirst()) {
            award = table.inflate(cursor);
            cursor.close();
        } else {
            award = new Award();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Award updatedAward = JSONHelper.getGson().fromJson(response.getData(), Award.class);
                award.merge(updatedAward);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            award.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(award, code);
    }

    public static APIResponse<ArrayList<Award>> queryList(Context c, RequestParams requestParams, String teamKey, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying awards table: " + whereClause + Arrays.toString(whereArgs));
        AwardsTable table = Database.getInstance(c).getAwardsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Award> awards = new ArrayList<>(), allAwards = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                awards.add(table.inflate(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false, teamSet = teamKey != null && !teamKey.isEmpty();
        String teamNumber = "";
        if (teamSet) {
            teamNumber = teamKey.substring(3);
        }
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray awardList = JSONHelper.getasJsonArray(response.getData());
                awards = new ArrayList<>();
                for (JsonElement a : awardList) {
                    Award award = JSONHelper.getGson().fromJson(a, Award.class);
                    try {
                        if (teamSet && award.getWinners().toString().contains(teamNumber)) {
                            awards.add(award);
                        } else {
                            allAwards.add(award);
                        }
                    } catch (FieldNotDefinedException e) {
                        Log.w(Constants.LOG_TAG, "Unable to determine if team: " + teamKey + " is involved in award");
                        allAwards.add(award);
                    }
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            allAwards.addAll(awards);
            Database.getInstance(c).getAwardsTable().add(allAwards);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + awards.size() + " awards, updated in db? " + changed);
        return new APIResponse<>(awards, code);
    }

    @Override
    public AwardListElement render() {
        try {
            return new AwardListElement(getName(), getWinners());
        } catch (FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Missing fields for rendering award");
            return null;
        }
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getAwardsTable().add(this);
    }
}
