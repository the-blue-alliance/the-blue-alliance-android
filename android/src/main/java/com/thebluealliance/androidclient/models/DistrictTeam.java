package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by phil on 7/23/14.
 */
public class DistrictTeam extends BasicModel<DistrictTeam> {

    public DistrictTeam(){
        super(Database.TABLE_DISTRICTTEAMS);
    }

    public void setKey(String key){
        fields.put(Database.DistrictTeams.KEY, key);
    }

    public String getKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.KEY) && fields.get(Database.DistrictTeams.KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.KEY is not defined");
        }
    }

    public void setDistrictKey(String key){
        fields.put(Database.DistrictTeams.DISTRICT_KEY, key);
    }

    public String getDistrictKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.DISTRICT_KEY) && fields.get(Database.DistrictTeams.DISTRICT_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.DISTRICT_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.DISTRICT_KEY is not defined");
        }
    }

    public void setTeamKey(String key){
        fields.put(Database.DistrictTeams.TEAM_KEY, key);
    }

    public String getTeamKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.TEAM_KEY) && fields.get(Database.DistrictTeams.TEAM_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.TEAM_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TEAM_KEY is not defined");
        }
    }

    public void setDistrictEnum(int districtEnum){
        fields.put(Database.DistrictTeams.DISTRICT_ENUM, districtEnum);
    }

    public int getDistrictEnum() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.DISTRICT_ENUM) && fields.get(Database.DistrictTeams.DISTRICT_ENUM) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.DISTRICT_ENUM);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.DISTRICT_ENUM is not defined");
        }
    }

    public void setYear(int year){
        fields.put(Database.DistrictTeams.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.YEAR) && fields.get(Database.DistrictTeams.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.YEAR);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.YEAR is not defined");
        }
    }

    public void setRank(int rank){
        fields.put(Database.DistrictTeams.RANK, rank);
    }

    public int getRank() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.RANK) && fields.get(Database.DistrictTeams.RANK) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.RANK);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.RANK is not defined");
        }
    }

    public void setEvent1Key(String key){
        fields.put(Database.DistrictTeams.EVENT1_KEY, key);
    }

    public String getEvent1Key() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.EVENT1_KEY) && fields.get(Database.DistrictTeams.EVENT1_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.EVENT1_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_KEY is not defined");
        }
    }

    public void setEvent1Points(int points){
        fields.put(Database.DistrictTeams.EVENT1_POINTS, points);
    }

    public int getEvent1Points() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.EVENT1_POINTS) && fields.get(Database.DistrictTeams.EVENT1_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.EVENT1_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setEvent2Key(String key){
        fields.put(Database.DistrictTeams.EVENT2_KEY, key);
    }

    public String getEvent2Key() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.EVENT2_KEY) && fields.get(Database.DistrictTeams.EVENT2_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.EVENT2_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT2_KEY is not defined");
        }
    }

    public void setEvent2Points(int points){
        fields.put(Database.DistrictTeams.EVENT2_POINTS, points);
    }

    public int getEvent2Points() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.EVENT2_POINTS) && fields.get(Database.DistrictTeams.EVENT2_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.EVENT2_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setCmpKey(String key){
        fields.put(Database.DistrictTeams.CMP_KEY, key);
    }

    public String getCmpKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.CMP_KEY) && fields.get(Database.DistrictTeams.CMP_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.CMP_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_KEY is not defined");
        }
    }

    public void setCmpPoints(int points){
        fields.put(Database.DistrictTeams.CMP_POINTS, points);
    }

    public int getCmpPoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.CMP_POINTS) && fields.get(Database.DistrictTeams.CMP_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.CMP_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setRookiePoints(int points){
        fields.put(Database.DistrictTeams.ROOKIE_POINTS, points);
    }

    public int getRookiePoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.ROOKIE_POINTS) && fields.get(Database.DistrictTeams.ROOKIE_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.ROOKIE_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setTotalPoints(int points){
        fields.put(Database.DistrictTeams.TOTAL_POINTS, points);
    }

    public int getTotalPoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.TOTAL_POINTS) && fields.get(Database.DistrictTeams.TOTAL_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.TOTAL_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TOTAL_POINTS is not defined");
        }
    }

    public void setJson(String json){
        fields.put(Database.DistrictTeams.JSON, json);
    }

    public String getJson() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.JSON) && fields.get(Database.DistrictTeams.JSON) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.JSON);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.JSON is not defined");
        }
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getDistrictTeamsTable().add(this);
    }

    @Override
    public ListElement render() {
        try {
            return new DistrictTeamListElement(getTeamKey(), getDistrictKey(), getRank(), getTotalPoints());
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Unable to render districtTeam. Missing fields");
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized APIResponse<DistrictTeam> query(Context c, String key, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districtTeams table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_DISTRICTTEAMS, fields, whereClause, whereArgs, null, null, null, null);
        DistrictTeam team;
        if (cursor != null && cursor.moveToFirst()) {
            team = ModelInflater.inflateDistrictTeam(cursor);
            cursor.close();
        } else {
            team = new DistrictTeam();
        }

        APIResponse.CODE code = forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        ArrayList<DistrictTeam> allTeams = null;
        boolean changed = false;
        allTeams = new ArrayList<>();
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                DistrictTeam updatedTeam = new DistrictTeam();
                if (url.contains("district") && url.contains("rankings")) {
                    /* We're requesting the rankings for an entire district (there isn't yet a single endpoint for this) */
                    JsonArray teamList = JSONManager.getasJsonArray(response.getData());
                    for (JsonElement t : teamList) {
                        DistrictTeam inflated = JSONManager.getGson().fromJson(t, DistrictTeam.class);
                        DistrictTeamHelper.addFieldsFromKey(inflated, key);

                        try {
                            if (inflated.getKey().equals(key)) {
                                updatedTeam = inflated;
                                //this match will be added to the list below
                            } else {
                                allTeams.add(inflated);
                            }
                        } catch (FieldNotDefinedException e) {
                            Log.e(Constants.LOG_TAG, "Unable to get districtTeam key");
                            allTeams.add(inflated);
                        }
                    }
                } else {
                    updatedTeam = JSONManager.getGson().fromJson(response.getData(), DistrictTeam.class);
                }
                team.merge(updatedTeam);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        allTeams.add(team);

        if (changed) {
            team.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(team, forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304);
    }

    public static synchronized APIResponse<ArrayList<DistrictTeam>> queryList(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districtTeams table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_DISTRICTTEAMS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<DistrictTeam> districtTeams = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                districtTeams.add(ModelInflater.inflateDistrictTeam(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray districtList = JSONManager.getasJsonArray(response.getData());
                districtTeams = new ArrayList<>();
                for (JsonElement d : districtList) {
                    DistrictTeam next = JSONManager.getGson().fromJson(d, DistrictTeam.class);
                    try {
                        DistrictTeamHelper.addFieldsFromAPIUrl(next, next.getTeamKey(), url);
                        districtTeams.add(next);
                    } catch (FieldNotDefinedException e) {
                        Log.e(Constants.LOG_TAG, "Unable to complete generation of districtTeam");
                    }
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            Database.getInstance(c).getDistrictTeamsTable().add(districtTeams);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + districtTeams.size() + " districtTeams, updated in db? " + changed);
        return new APIResponse<>(districtTeams, code);
    }
}
