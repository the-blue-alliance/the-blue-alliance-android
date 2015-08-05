package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.listitems.TeamListElement;

import java.util.Arrays;

public class Team extends BasicModel<Team> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.ALLIANCE_SELECTION,
            NotificationTypes.AWARDS,
            //NotificationTypes.MEDIA_POSTED
    };

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
        if (fields.containsKey(TeamsTable.NAME) && fields.get(TeamsTable.NAME) instanceof String) {
            return (String) fields.get(TeamsTable.NAME);
        }
        return getNickname();
    }

    public void setFullName(String fullName) {
        fields.put(TeamsTable.NAME, fullName);
    }

    public String getWebsite() throws FieldNotDefinedException {
        if (fields.containsKey(TeamsTable.WEBSITE) && fields.get(TeamsTable.WEBSITE) instanceof String) {
            return (String) fields.get(TeamsTable.WEBSITE);
        }
        throw new FieldNotDefinedException("Field Database.Teams.WEBSITE is not defined");
    }

    public void setWebsite(String website) {
        fields.put(TeamsTable.WEBSITE, website);
    }

    @Override
    public String getKey() {
        if (fields.containsKey(TeamsTable.KEY) && fields.get(TeamsTable.KEY) instanceof String) {
            return (String) fields.get(TeamsTable.KEY);
        }
        return "";
    }

    public void setTeamKey(String teamKey) {
        fields.put(TeamsTable.KEY, teamKey);
    }

    public String getNickname() {
        if (fields.containsKey(TeamsTable.SHORTNAME) && fields.get(TeamsTable.SHORTNAME) instanceof String) {
            return (String) fields.get(TeamsTable.SHORTNAME);
        } else {
            return "";
        }
    }

    public void setNickname(String nickname) {
        fields.put(TeamsTable.SHORTNAME, nickname);
    }

    public String getLocation() {
        if (fields.containsKey(TeamsTable.LOCATION) && fields.get(TeamsTable.LOCATION) instanceof String) {
            return (String) fields.get(TeamsTable.LOCATION);
        }
        return "";
    }

    public void setLocation(String location) {
        fields.put(TeamsTable.LOCATION, location);
    }

    public Integer getTeamNumber() throws FieldNotDefinedException {
        if (fields.containsKey(TeamsTable.NUMBER) && fields.get(TeamsTable.NUMBER) instanceof Integer) {
            return (Integer) fields.get(TeamsTable.NUMBER);
        }
        throw new FieldNotDefinedException("Field Database.Teams.NUMBER is not defined");
    }

    public void setTeamNumber(int teamNumber) {
        fields.put(TeamsTable.NUMBER, teamNumber);
    }

    public void setYearsParticipated(JsonArray years) {
        fields.put(TeamsTable.YEARS_PARTICIPATED, years.toString());
        this.yearsParticipated = years;
    }

    public void setYearsParticipated(String yearsJson) {
        fields.put(TeamsTable.YEARS_PARTICIPATED, yearsJson);
    }

    public JsonArray getYearsParticipated() throws FieldNotDefinedException {
        if (yearsParticipated != null) {
            return yearsParticipated;
        }
        if (fields.containsKey(TeamsTable.YEARS_PARTICIPATED) && fields.get(TeamsTable.YEARS_PARTICIPATED) instanceof String) {
            yearsParticipated = JSONHelper.getasJsonArray((String) fields.get(TeamsTable.YEARS_PARTICIPATED));
            return yearsParticipated;
        }
        throw new FieldNotDefinedException("Field Database.Teams.YEARS_PARTICIPATED is not defined");
    }

    public String getSearchTitles() {
        try {
            return getKey() + "," + getNickname() + "," + getTeamNumber();
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for creating search titles\n" +
                    "Required: Database.Teams.KEY, Database.Teams.SHORTNAME, Database.Teams.NUMBER");
            return null;
        }
    }

    @Override
    public TeamListElement render() {
        try {
            return new TeamListElement(getKey(), getTeamNumber(), getNickname(), getLocation());
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    public TeamListElement render(boolean showTeamInfoButton) {
        try {
            return new TeamListElement(getKey(), getTeamNumber(), getNickname(), getLocation(), showTeamInfoButton);
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
                    "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }

    public static APIResponse<Team> query(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying teams table: " + whereClause + Arrays.toString(whereArgs));
        TeamsTable table = Database.getInstance(c).getTeamsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        Team team;
        if (cursor != null && cursor.moveToFirst()) {
            team = table.inflate(cursor);
            cursor.close();
        } else {
            team = new Team();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Team updatedTeam;
                if (url.contains("years_participated")) {
                    Log.w(Constants.DATAMANAGER_LOG, "Fetching years participated");
                    updatedTeam = new Team();
                    team.setYearsParticipated(response.getData());
                } else {
                    updatedTeam = JSONHelper.getGson().fromJson(response.getData(), Team.class);
                }
                team.merge(updatedTeam);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
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
