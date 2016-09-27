package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.viewmodels.TeamViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Team extends com.thebluealliance.api.model.Team implements TbaDatabaseModel,
                                                             ViewModelRenderer<TeamViewModel, Integer> {

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
        yearsParticipated = null;
    }

    public Team(String teamKey, int teamNumber, String nickname, String location) {
        this();
        setKey(teamKey);
        setTeamNumber(teamNumber);
        setNickname(nickname);
        setLocation(location);
    }

    public JsonArray getYearsParticipated() {
        return yearsParticipated;
    }

    public void setYearsParticipated(String yearsParticipated) {
        this.yearsParticipated = JSONHelper.getasJsonArray(yearsParticipated);
    }

    public void setYearsParticipated(JsonArray yearsParticipated) {
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
        return null;
    }
}
