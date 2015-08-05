package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AwardsListSubscriber extends BaseAPISubscriber<List<Award>, List<ListItem>> {

    private String mTeamKey;
    private Database mDb;

    public AwardsListSubscriber(Database db) {
        super();
        mDataToBind = new ArrayList<>();
        mDb = db;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null || mAPIData.isEmpty()) {
            return;
        }
        Map<String, Team> teams = Utilities.getMapForPlatform(String.class, Team.class);
        for (int i = 0; i < mAPIData.size(); i++) {
            Award award = mAPIData.get(i);
            for (JsonElement winner : award.getWinners()) {
                if (!winner.getAsJsonObject().get("team_number").isJsonNull()) {
                    String teamKey = "frc" + winner.getAsJsonObject().get("team_number");
                    Team team = mDb.getTeamsTable().get(teamKey);
                    teams.put(teamKey, team);
                }
            }
            mDataToBind.add(new CardedAwardListElement(
              award.getName(),
              award.getEventKey(),
              award.getWinners(),
              teams,
              mTeamKey));
        }
    }
}