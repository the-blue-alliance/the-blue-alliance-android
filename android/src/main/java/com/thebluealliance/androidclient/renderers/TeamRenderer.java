package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.thebluealliance.androidclient.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TeamRenderer implements ModelRenderer<Team, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_DETAILS_BUTTON, RENDER_MYTBA_DETAILS})
    public @interface RenderType{}
    public static final int RENDER_BASIC = 0;
    public static final int RENDER_DETAILS_BUTTON = 1;
    public static final int RENDER_MYTBA_DETAILS = 2;

    APICache mDatafeed;

    @Inject
    public TeamRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    @WorkerThread
    @Override
    public @Nullable TeamListElement renderFromKey(String key, ModelType type, Integer args) {
        Team team = mDatafeed.fetchTeam(key).toBlocking().first();
        if (team == null) {
            return null;
        }
        return renderFromModel(team, RENDER_BASIC);
    }

    @WorkerThread
    @Override
    public @Nullable TeamListElement renderFromModel(Team team, @RenderType Integer renderType) {
        int safeRenderType = renderType == null ? RENDER_BASIC : renderType;
        try {
            return new TeamListElement(
              team.getKey(),
              team.getNumber(),
              team.getNickname(),
              team.getLocation(),
              safeRenderType == RENDER_DETAILS_BUTTON,
              safeRenderType == RENDER_MYTBA_DETAILS);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w("Missing fields for rendering.\n"
              + "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, "
              + "Database.Teams.LOCATION");
            return null;
        }
    }
}
