package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamListSubscriber extends BaseAPISubscriber<List<Team>, List<ListItem>> {

    private TeamSortByNumberComparator mComparator;
    private boolean mShowTeamInfoButton = false;

    public TeamListSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
        mComparator = new TeamSortByNumberComparator();
    }

    public void setShowTeamInfoButton(boolean show) {
        mShowTeamInfoButton = show;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        Collections.sort(mAPIData, mComparator);
        for (int i=0; i < mAPIData.size(); i++) {
            Team team = mAPIData.get(i);
            if (team == null) {
                continue;
            }
            ListItem item = team.render(mShowTeamInfoButton);
            if (item == null) {
                continue;
            }
            mDataToBind.add(item);
        }
    }
}
