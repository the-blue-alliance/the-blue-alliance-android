package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.models.Team;

public class TeamInfoSubscriber extends BaseAPISubscriber<Team, TeamInfoBinder.Model>{

    public TeamInfoSubscriber() {
        mDataToBind = null;
    }

    @Override
    public void parseData() {
        mDataToBind = new TeamInfoBinder.Model();
        mDataToBind.teamKey = mAPIData.getKey();
        mDataToBind.fullName = mAPIData.getName();
        mDataToBind.nickname = mAPIData.getNickname();
        mDataToBind.teamNumber = mAPIData.getTeamNumber();
        mDataToBind.location = mAPIData.getLocation();
        if (mAPIData.getParams().containsKey(TeamsTable.WEBSITE)) {
            mDataToBind.website = mAPIData.getWebsite();
        } else {
            mDataToBind.website = "";
        }
        if (mAPIData.getParams().containsKey(TeamsTable.MOTTO)) {
            mDataToBind.motto = mAPIData.getMotto();
        } else {
            mDataToBind.motto = "";
        }
    }
}
