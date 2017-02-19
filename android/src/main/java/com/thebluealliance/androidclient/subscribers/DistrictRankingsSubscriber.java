package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;

public class DistrictRankingsSubscriber
  extends BaseAPISubscriber<List<DistrictRanking>, List<ListItem>> {

    Database mDb;

    public DistrictRankingsSubscriber(Database db) {
        super();
        mDataToBind = new ArrayList<>();
        mDb = db;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        for (int i = 0; i < mAPIData.size(); i++) {
            DistrictRanking districtTeam = mAPIData.get(i);
            Team teamData = mDb.getTeamsTable().get(districtTeam.getTeamKey());
            String nickname;
            if (teamData != null) {
                nickname = teamData.getNickname();
            } else {
                TbaLogger.w("Couldn't find " + districtTeam.getTeamKey() + " in db");
                nickname = "Team " + districtTeam.getTeamKey().substring(3);
            }
            mDataToBind.add(
              new DistrictTeamListElement(
                districtTeam.getTeamKey(),
                districtTeam.getDistrictKey(),
                nickname,
                districtTeam.getRank(),
                districtTeam.getPointTotal()));
        }
    }
}
