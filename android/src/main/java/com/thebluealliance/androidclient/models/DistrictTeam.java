package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by phil on 7/23/14.
 */
public class DistrictTeam extends BasicModel<DistrictTeam> {

    public DistrictTeam() {
        super(Database.TABLE_DISTRICTTEAMS);
    }

    public void setKey(String key) {
        fields.put(DistrictTeamsTable.KEY, key);
    }

    public String getKey() {
        if (fields.containsKey(DistrictTeamsTable.KEY) && fields.get(DistrictTeamsTable.KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.KEY);
        } else {
            return "";
        }
    }

    public void setDistrictKey(String key) {
        fields.put(DistrictTeamsTable.DISTRICT_KEY, key);
    }

    public String getDistrictKey() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.DISTRICT_KEY) && fields.get(DistrictTeamsTable.DISTRICT_KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.DISTRICT_KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.DISTRICT_KEY is not defined");
        }
    }

    public void setTeamKey(String key) {
        fields.put(DistrictTeamsTable.TEAM_KEY, key);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.TEAM_KEY) && fields.get(DistrictTeamsTable.TEAM_KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.TEAM_KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TEAM_KEY is not defined");
        }
    }

    public void setDistrictEnum(int districtEnum) {
        fields.put(DistrictTeamsTable.DISTRICT_ENUM, districtEnum);
    }

    public int getDistrictEnum() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.DISTRICT_ENUM) && fields.get(DistrictTeamsTable.DISTRICT_ENUM) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.DISTRICT_ENUM);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.DISTRICT_ENUM is not defined");
        }
    }

    public void setYear(int year) {
        fields.put(DistrictTeamsTable.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.YEAR) && fields.get(DistrictTeamsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.YEAR);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.YEAR is not defined");
        }
    }

    public void setRank(int rank) {
        fields.put(DistrictTeamsTable.RANK, rank);
    }

    public int getRank() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.RANK) && fields.get(DistrictTeamsTable.RANK) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.RANK);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.RANK is not defined");
        }
    }

    public void setEvent1Key(String key) {
        fields.put(DistrictTeamsTable.EVENT1_KEY, key);
    }

    public String getEvent1Key() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.EVENT1_KEY) && fields.get(DistrictTeamsTable.EVENT1_KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.EVENT1_KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_KEY is not defined");
        }
    }

    public void setEvent1Points(int points) {
        fields.put(DistrictTeamsTable.EVENT1_POINTS, points);
    }

    public int getEvent1Points() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.EVENT1_POINTS) && fields.get(DistrictTeamsTable.EVENT1_POINTS) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.EVENT1_POINTS);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setEvent2Key(String key) {
        fields.put(DistrictTeamsTable.EVENT2_KEY, key);
    }

    public String getEvent2Key() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.EVENT2_KEY) && fields.get(DistrictTeamsTable.EVENT2_KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.EVENT2_KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT2_KEY is not defined");
        }
    }

    public void setEvent2Points(int points) {
        fields.put(DistrictTeamsTable.EVENT2_POINTS, points);
    }

    public int getEvent2Points() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.EVENT2_POINTS) && fields.get(DistrictTeamsTable.EVENT2_POINTS) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.EVENT2_POINTS);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setCmpKey(String key) {
        fields.put(DistrictTeamsTable.CMP_KEY, key);
    }

    public String getCmpKey() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.CMP_KEY) && fields.get(DistrictTeamsTable.CMP_KEY) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.CMP_KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_KEY is not defined");
        }
    }

    public void setCmpPoints(int points) {
        fields.put(DistrictTeamsTable.CMP_POINTS, points);
    }

    public int getCmpPoints() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.CMP_POINTS) && fields.get(DistrictTeamsTable.CMP_POINTS) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.CMP_POINTS);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setRookiePoints(int points) {
        fields.put(DistrictTeamsTable.ROOKIE_POINTS, points);
    }

    public int getRookiePoints() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.ROOKIE_POINTS) && fields.get(DistrictTeamsTable.ROOKIE_POINTS) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.ROOKIE_POINTS);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setTotalPoints(int points) {
        fields.put(DistrictTeamsTable.TOTAL_POINTS, points);
    }

    public int getTotalPoints() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.TOTAL_POINTS) && fields.get(DistrictTeamsTable.TOTAL_POINTS) instanceof Integer) {
            return (Integer) fields.get(DistrictTeamsTable.TOTAL_POINTS);
        } else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TOTAL_POINTS is not defined");
        }
    }

    public void setJson(String json) {
        fields.put(DistrictTeamsTable.JSON, json);
    }

    public String getJson() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictTeamsTable.JSON) && fields.get(DistrictTeamsTable.JSON) instanceof String) {
            return (String) fields.get(DistrictTeamsTable.JSON);
        } else {
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

    public static APIResponse<DistrictTeam> query(Context c, String key, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districtTeams table: " + whereClause + Arrays.toString(whereArgs));
        DistrictTeamsTable table = Database.getInstance(c).getDistrictTeamsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        DistrictTeam team;
        if (cursor != null && cursor.moveToFirst()) {
            team = table.inflate(cursor);
            cursor.close();
        } else {
            team = new DistrictTeam();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        ArrayList<DistrictTeam> allTeams = null;
        boolean changed = false;
        allTeams = new ArrayList<>();
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                DistrictTeam updatedTeam = new DistrictTeam();
                if (url.contains("district") && url.contains("rankings")) {
                    /* We're requesting the rankings for an entire district (there isn't yet a single endpoint for this) */
                    JsonArray teamList = JSONHelper.getasJsonArray(response.getData());
                    for (JsonElement t : teamList) {
                        DistrictTeam inflated = JSONHelper.getGson().fromJson(t, DistrictTeam.class);
                        DistrictTeamHelper.addFieldsFromKey(inflated, key);

                        if (inflated.getKey().equals(key)) {
                            updatedTeam = inflated;
                            //this match will be added to the list below
                        } else {
                            allTeams.add(inflated);
                        }
                    }
                } else {
                    updatedTeam = JSONHelper.getGson().fromJson(response.getData(), DistrictTeam.class);
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
        return new APIResponse<>(team, requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304);
    }

    public static APIResponse<ArrayList<DistrictTeam>> queryList(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districtTeams table: " + whereClause + Arrays.toString(whereArgs));
        DistrictTeamsTable table = Database.getInstance(c).getDistrictTeamsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<DistrictTeam> districtTeams = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                districtTeams.add(table.inflate(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray districtList = JSONHelper.getasJsonArray(response.getData());
                districtTeams = new ArrayList<>();
                for (JsonElement d : districtList) {
                    DistrictTeam next = JSONHelper.getGson().fromJson(d, DistrictTeam.class);
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
