package com.thebluealliance.androidclient.models;

import android.util.Log;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.types.ModelType;

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
        super(Database.TABLE_TEAMS, ModelType.TEAM);
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

}
