package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

public class TeamInfoSubscriber extends BaseAPISubscriber<Team, TeamInfoBinder.Model>{

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException{
        mDataToBind = new TeamInfoBinder.Model();
        mDataToBind.teamKey = mAPIData.getKey();
        mDataToBind.fullName = mAPIData.getFullName();
        mDataToBind.nickname = mAPIData.getNickname();
        mDataToBind.teamNumber = mAPIData.getTeamNumber();
        mDataToBind.location = mAPIData.getLocation();
        mDataToBind.website = mAPIData.getWebsite();
    }
}
