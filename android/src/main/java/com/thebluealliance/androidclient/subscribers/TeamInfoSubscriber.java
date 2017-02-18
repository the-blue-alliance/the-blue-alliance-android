package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.TeamInfoBinder;
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
        mDataToBind.location = mAPIData.getAddress();
        if (mAPIData.getWebsite() != null) {
            mDataToBind.website = mAPIData.getWebsite();
        } else {
            mDataToBind.website = "";
        }
        if (mAPIData.getMotto() != null) {
            mDataToBind.motto = mAPIData.getMotto();
        } else {
            mDataToBind.motto = "";
        }
    }
}
