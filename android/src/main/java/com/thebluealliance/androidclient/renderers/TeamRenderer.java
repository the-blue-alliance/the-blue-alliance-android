package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TeamRenderer implements ModelRenderer<Team, Boolean> {

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
        return renderFromModel(team, false);
    }

    @WorkerThread
    @Override
    public @Nullable TeamListElement renderFromModel(Team team, Boolean showTeamDetailsButton) {
        try {
            return new TeamListElement(
              team.getKey(),
              team.getTeamNumber(),
              team.getNickname(),
              team.getLocation(),
              showTeamDetailsButton);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering.\n" +
              "Required: Database.Teams.KEY, Database.Teams.NUMBER, Database.Teams.SHORTNAME, Database.Teams.LOCATION");
            return null;
        }
    }
}
