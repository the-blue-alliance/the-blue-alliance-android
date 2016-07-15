package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Team extends BasicModel<Team> implements ViewModelRenderer<TeamViewModel, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_DETAILS_BUTTON, RENDER_MYTBA_DETAILS})
    public @interface RenderType{}
    public static final int RENDER_BASIC = 0;
    public static final int RENDER_DETAILS_BUTTON = 1;
    public static final int RENDER_MYTBA_DETAILS = 2;

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
        setKey(teamKey);
        setNumber(teamNumber);
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

    public void setKey(String key) {
        fields.put(TeamsTable.KEY, key);
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

    public Integer getNumber() throws FieldNotDefinedException {
        if (fields.containsKey(TeamsTable.NUMBER) && fields.get(TeamsTable.NUMBER) instanceof Integer) {
            return (Integer) fields.get(TeamsTable.NUMBER);
        }
        throw new FieldNotDefinedException("Field Database.Teams.NUMBER is not defined");
    }

    public void setNumber(int number) {
        fields.put(TeamsTable.NUMBER, number);
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
            return getKey() + "," + getNickname() + "," + getNumber();
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for creating search titles\n"
                    + "Required: Database.Teams.KEY, Database.Teams.SHORTNAME, Database.Teams.NUMBER");
            return null;
        }
    }

    public String getMotto() throws FieldNotDefinedException {
        if (fields.containsKey(TeamsTable.MOTTO) && fields.get(TeamsTable.MOTTO) instanceof String) {
            return (String) fields.get(TeamsTable.MOTTO);
        }
        throw new FieldNotDefinedException("Field Database.Teams.MOTTO is not defined");
    }

    public void setMotto(String motto) {
        fields.put(TeamsTable.MOTTO, motto);
    }

    @Nullable
    @Override
    public TeamViewModel renderToViewModel(Context context, @Nullable @RenderType Integer renderType) {
        try {
            int safeRenderType = renderType == null ? RENDER_BASIC : renderType;
            TeamViewModel model = new TeamViewModel(getKey(), getNumber(), getNickname(), getLocation());
            model.setShowLinkToTeamDetails(false);
            model.setShowMyTbaDetails(false);
            switch (safeRenderType) {
                case RENDER_BASIC:
                    break;
                case RENDER_DETAILS_BUTTON:
                    model.setShowLinkToTeamDetails(true);
                    break;
                case RENDER_MYTBA_DETAILS:
                    model.setShowMyTbaDetails(true);
                    break;
            }
            return model;
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Team missing field required for rendering.");
            return null;
        }
    }
}
