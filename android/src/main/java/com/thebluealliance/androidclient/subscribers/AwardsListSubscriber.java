package com.thebluealliance.androidclient.subscribers;

import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardsListSubscriber extends BaseAPISubscriber<List<Award>, ListViewAdapter> {

    private String mTeamKey;
    private String mEventKey;
    private Database mDb;

    public AwardsListSubscriber(Context context, Database db) {
        super();
        mDataToBind = new ListViewAdapter(context, new ArrayList<>());
        mDb = db;
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.values.clear();
        if (mAPIData == null || mAPIData.isEmpty()) {
            return;
        }
        Map<String, Team> teams = getMapForPlatform();
        for (int i = 0; i < mAPIData.size(); i++) {
            Award award = mAPIData.get(i);
            for (JsonElement winner : award.getWinners()) {
                if (!winner.getAsJsonObject().get("team_number").isJsonNull()) {
                    String teamKey = "frc" + winner.getAsJsonObject().get("team_number");
                    Team team = mDb.getTeamsTable().get(mTeamKey);
                    teams.put(teamKey, team);
                }
            }
            mDataToBind.values.add(new CardedAwardListElement(
              award.getName(),
              mEventKey,
              award.getWinners(),
              teams,
              mTeamKey));
        }
    }

    /**
     * {@link ArrayMap} is more memory efficient than {@link HashMap}, so prefer that if possible
     */
    private Map<String, Team> getMapForPlatform() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new ArrayMap<>();
        } else {
            return new HashMap<>();
        }
    }
}