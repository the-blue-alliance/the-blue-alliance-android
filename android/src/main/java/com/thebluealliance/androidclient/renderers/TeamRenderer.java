package com.thebluealliance.androidclient.renderers;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TeamRenderer implements ModelRenderer<Team, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_DETAILS_BUTTON})
    public @interface RenderType{}
    public static final int RENDER_BASIC = 0;
    public static final int RENDER_DETAILS_BUTTON = 1;

    APICache mDatafeed;

    @Inject
    public TeamRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    @WorkerThread
    @Override
    public @Nullable TeamListElement renderFromKey(String key, ModelType type) {
        Team team = mDatafeed.fetchTeam(key).toBlocking().first();
        if (team == null) {
            return null;
        }
        return renderFromModel(team, RENDER_BASIC);
    }

    @WorkerThread
    @Override
    public @Nullable TeamListElement renderFromModel(Team team, @RenderType Integer renderType) {
        try {
            return new TeamListElement(
              team.getKey(),
              team.getTeamNumber(),
              team.getNickname(),
              team.getLocation(),
              renderType == RENDER_DETAILS_BUTTON);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
              "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }
}
