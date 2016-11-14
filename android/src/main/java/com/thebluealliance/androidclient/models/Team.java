package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;
import com.thebluealliance.api.model.ITeam;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Team implements ITeam, TbaDatabaseModel, ViewModelRenderer<TeamViewModel, Integer> {

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

    private String countryName = null;
    private String key = null;
    private Long lastModified = null;
    private String locality = null;
    private String location = null;
    private String motto = null;
    private String name = null;
    private String nickname = null;
    private String region = null;
    private Integer rookieYear = null;
    private Integer teamNumber = null;
    private String website = null;

    private List<Integer> yearsParticipated;

    public Team() {
        yearsParticipated = null;
    }

    public Team(String teamKey, int teamNumber, String nickname, String location) {
        this();
        setKey(teamKey);
        setTeamNumber(teamNumber);
        setNickname(nickname);
        setLocation(location);
    }

    @Nullable @Override public String getCountryName() {
        return countryName;
    }

    @Override public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Nullable @Override public String getLocality() {
        return locality;
    }

    @Override public void setLocality(String locality) {
        this.locality = locality;
    }

    @Nullable @Override public String getLocation() {
        return location;
    }

    @Override public void setLocation(String location) {
        this.location = location;
    }

    @Nullable @Override public String getMotto() {
        return motto;
    }

    @Override public void setMotto(String motto) {
        this.motto = motto;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Nullable @Override public String getNickname() {
        return nickname;
    }

    @Override public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Nullable @Override public String getRegion() {
        return region;
    }

    @Override public void setRegion(String region) {
        this.region = region;
    }

    @Nullable @Override public Integer getRookieYear() {
        return rookieYear;
    }

    @Override public void setRookieYear(Integer rookieYear) {
        this.rookieYear = rookieYear;
    }

    @Override public Integer getTeamNumber() {
        return teamNumber;
    }

    @Override public void setTeamNumber(Integer teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Nullable @Override public String getWebsite() {
        return website;
    }

    @Override public void setWebsite(String website) {
        this.website = website;
    }

    public List<Integer> getYearsParticipated() {
        return yearsParticipated;
    }

    public void setYearsParticipated(String yearsParticipated) {
        JsonArray array = JSONHelper.getasJsonArray(yearsParticipated);
        setYearsParticipated(array);
    }

    public void setYearsParticipated(JsonArray yearsParticipated) {
        if (this.yearsParticipated == null) {
            this.yearsParticipated = new ArrayList<>();
        }
        this.yearsParticipated.clear();
        for (int i = 0; i < yearsParticipated.size(); i++) {
            this.yearsParticipated.add(yearsParticipated.get(i).getAsInt());
        }
    }

    public void setYearsParticipated(List<Integer> yearsParticipated) {
        this.yearsParticipated = yearsParticipated;
    }

    public String getSearchTitles() {
        return getKey() + "," + getNickname() + "," + getTeamNumber();
    }

    @Nullable
    @Override
    public TeamViewModel renderToViewModel(
            Context context,
            @Nullable @RenderType Integer renderType) {
        int safeRenderType = renderType == null ? RENDER_BASIC : renderType;
        TeamViewModel model = new TeamViewModel(getKey(), getTeamNumber(), getNickname(),
                                                getLocation());
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
    }

    @Override
    public ContentValues getParams() {
        ContentValues data = new ContentValues();
        data.put(TeamsTable.KEY, getKey());
        data.put(TeamsTable.NUMBER, getTeamNumber());
        data.put(TeamsTable.NAME, getName());
        data.put(TeamsTable.SHORTNAME, getNickname());
        data.put(TeamsTable.LOCATION, getLocation());
        data.put(TeamsTable.WEBSITE, getWebsite());
        if (yearsParticipated != null) {
            data.put(TeamsTable.YEARS_PARTICIPATED, yearsParticipatedToJsonString(yearsParticipated));
        }
        data.put(TeamsTable.MOTTO, getMotto());
        data.put(TeamsTable.LAST_MODIFIED, getLastModified());
        return data;
    }

    private static String yearsParticipatedToJsonString(List<Integer> yearsParticipated) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < yearsParticipated.size(); i++) {
            array.add(yearsParticipated.get(i));
        }
        return array.toString();
    }
}
