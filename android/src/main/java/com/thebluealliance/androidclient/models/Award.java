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
import com.thebluealliance.androidclient.helpers.AwardHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.AwardListElement;

import java.util.ArrayList;
import java.util.Iterator;

public class Award extends BasicModel<Award> {

    public Award() {
        super(Database.TABLE_AWARDS);
    }
    public Award(String eventKey, String name, int year, JsonArray winners) {
        this();
        setEventKey(eventKey);
        setName(name);
        setYear(year);
        setWinners(winners);
    }

    public String getKey() throws FieldNotDefinedException {
        return getEventKey()+":"+getName();
    }

    public JsonArray getWinners() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Awards.WINNERS) && fields.get(Database.Awards.WINNERS) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Awards.WINNERS));
        }
        throw new FieldNotDefinedException("Field Database.Awards.WINNERS is not defined");
    }

    public void setWinners(JsonArray winners) {
        fields.put(Database.Awards.WINNERS, winners.toString());
    }

    public int getYear() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Awards.YEAR) && fields.get(Database.Awards.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Awards.WINNERS);
        }
        throw new FieldNotDefinedException("Field Database.Awards.YEAR is not defined");
    }

    public void setYear(int year) {
        fields.put(Database.Awards.YEAR, year);
    }

    public String getName() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Awards.NAME) && fields.get(Database.Awards.NAME) instanceof String) {
            return (String) fields.get(Database.Awards.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Awards.NAME is not defined");
    }

    public void setName(String name) {
        fields.put(Database.Awards.NAME, name);
    }

    public String getEventKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Awards.EVENTKEY) && fields.get(Database.Awards.EVENTKEY) instanceof String) {
            return (String) fields.get(Database.Awards.EVENTKEY);
        }
        throw new FieldNotDefinedException("Field Database.Awards.EVENTKEY is not defined");
    }

    public void setEventKey(String eventKey) {
        fields.put(Database.Awards.EVENTKEY, eventKey);
    }

    public ArrayList<Award> splitByWinner() throws FieldNotDefinedException{
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

    public static APIResponse<Award> query(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_AWARDS, fields, whereClause, whereArgs, null, null, null, null);
        Award award;
        if(cursor != null && cursor.moveToFirst()){
            award = ModelInflater.inflateAward(cursor);
        }else{
            award = new Award();
        }

        APIResponse.CODE code = forceFromCache?APIResponse.CODE.LOCAL: APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Award updatedAward = JSONManager.getGson().fromJson(response.getData(), Award.class);
                award.merge(updatedAward);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            award.write(c);
        }
        return new APIResponse<>(award, code);
    }

    public static APIResponse<ArrayList<Award>> queryList(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_AWARDS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Award> awards = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                awards.add(ModelInflater.inflateAward(cursor));
            } while (cursor.moveToNext());
        }

        APIResponse.CODE code = forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray awardList = JSONManager.getasJsonArray(response.getData());
                awards = new ArrayList<>();
                for (JsonElement a : awardList) {
                    awards.add(JSONManager.getGson().fromJson(a, Award.class));
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            Database.getInstance(c).getAwardsTable().add(awards);
        }
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
