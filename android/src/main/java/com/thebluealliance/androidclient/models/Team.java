package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.TeamListElement;

import java.util.Arrays;

public class Team extends BasicModel<Team> {

    private JsonArray yearsParticipated;

    public Team() {
        super(Database.TABLE_TEAMS);
        yearsParticipated = null;
    }

    public Team(String teamKey, int teamNumber, String nickname, String location) {
        this();
        setTeamKey(teamKey);
        setTeamNumber(teamNumber);
        setNickname(nickname);
        setLocation(location);
    }

    public String getFullName() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Teams.NAME) && fields.get(Database.Teams.NAME) instanceof String) {
            return (String) fields.get(Database.Teams.NAME);
        }
        return getNickname();
    }

    public void setFullName(String fullName) {
        fields.put(Database.Teams.NAME, fullName);
    }

    public String getWebsite() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Teams.WEBSITE) && fields.get(Database.Teams.WEBSITE) instanceof String) {
            return (String) fields.get(Database.Teams.WEBSITE);
        }
        throw new FieldNotDefinedException("Field Database.Teams.WEBSITE is not defined");
    }

    public void setWebsite(String website) {
        fields.put(Database.Teams.WEBSITE, website);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Teams.KEY) && fields.get(Database.Teams.KEY) instanceof String) {
            return (String) fields.get(Database.Teams.KEY);
        }
        throw new FieldNotDefinedException("Field Database.Teams.KEY is not defined");
    }

    public void setTeamKey(String teamKey) {
        fields.put(Database.Teams.KEY, teamKey);
    }

    public String getNickname() {
        if (fields.containsKey(Database.Teams.SHORTNAME) && fields.get(Database.Teams.SHORTNAME) instanceof String) {
            return (String) fields.get(Database.Teams.SHORTNAME);
        } else {
            return "";
        }
    }

    public void setNickname(String nickname) {
        fields.put(Database.Teams.SHORTNAME, nickname);
    }

    public String getLocation() {
        if (fields.containsKey(Database.Teams.LOCATION) && fields.get(Database.Teams.LOCATION) instanceof String) {
            return (String) fields.get(Database.Teams.LOCATION);
        }
        return "";
    }

    public void setLocation(String location) {
        fields.put(Database.Teams.LOCATION, location);
    }

    public Integer getTeamNumber() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Teams.NUMBER) && fields.get(Database.Teams.NUMBER) instanceof Integer) {
            return (Integer) fields.get(Database.Teams.NUMBER);
        }
        throw new FieldNotDefinedException("Field Database.Teams.NUMBER is not defined");
    }

    public void setTeamNumber(int teamNumber) {
        fields.put(Database.Teams.NUMBER, teamNumber);
    }

    public void setYearsParticipated(JsonArray years) {
        fields.put(Database.Teams.YEARS_PARTICIPATED, years.toString());
        this.yearsParticipated = years;
    }

    public void setYearsParticipated(String yearsJson) {
        fields.put(Database.Teams.YEARS_PARTICIPATED, yearsJson);
    }

    public JsonArray getYearsParticipated() throws FieldNotDefinedException {
        if (yearsParticipated != null) {
            return yearsParticipated;
        }
        if (fields.containsKey(Database.Teams.YEARS_PARTICIPATED) && fields.get(Database.Teams.YEARS_PARTICIPATED) instanceof String) {
            yearsParticipated = JSONManager.getasJsonArray((String) fields.get(Database.Teams.YEARS_PARTICIPATED));
            return yearsParticipated;
        }
        throw new FieldNotDefinedException("Field Database.Teams.YEARS_PARTICIPATED is not defined");
    }

    public String getSearchTitles() {
        try {
            return getTeamKey() + "," + getNickname() + "," + getTeamNumber();
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for creating search titles\n" +
                    "Required: Database.Teams.KEY, Database.Teams.SHORTNAME, Database.Teams.NUMBER");
            return null;
        }
    }

    @Override
    public TeamListElement render() {
        try {
            return new TeamListElement(getTeamKey(), getTeamNumber(), getNickname(), getLocation());
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    public TeamListElement render(boolean showTeamInfoButton) {
        try {
            return new TeamListElement(getTeamKey(), getTeamNumber(), getNickname(), getLocation(), showTeamInfoButton);
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    public static synchronized APIResponse<Team> query(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying teams table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_TEAMS, fields, whereClause, whereArgs, null, null, null, null);
        Team team;
        if (cursor != null && cursor.moveToFirst()) {
            team = ModelInflater.inflateTeam(cursor);
            cursor.close();
        } else {
            team = new Team();
        }

        Log.e(Constants.DATAMANAGER_LOG, "Starting team: " + team.getParams());

        APIResponse.CODE code = forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Team updatedTeam;
                if (url.contains("years_participated")) {
                    Log.w(Constants.DATAMANAGER_LOG, "Fetching years participated");
                    updatedTeam = new Team();
                    team.setYearsParticipated(response.getData());
                } else {
                    updatedTeam = JSONManager.getGson().fromJson(response.getData(), Team.class);
                }
                team.merge(updatedTeam);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            Log.e(Constants.DATAMANAGER_LOG, "New team: " + team.getParams());
            team.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(team, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getTeamsTable().add(this);
    }
}
