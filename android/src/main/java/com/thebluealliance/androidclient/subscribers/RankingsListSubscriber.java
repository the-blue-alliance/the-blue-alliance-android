package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventHelper.CaseInsensitiveMap;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.RankingListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class RankingsListSubscriber extends BaseAPISubscriber<JsonArray, List<ListItem>> {

    private Database mDb;

    public RankingsListSubscriber(Database db) {
        super();
        mDb = db;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null || mAPIData.size() == 0) {
            return;
        }
        JsonArray headerRow = mAPIData.remove(0).getAsJsonArray();
        for (int i=1; i < mAPIData.size(); i++) {
            JsonArray row = mAPIData.get(i).getAsJsonArray();
            /* Assume that the list of lists has rank first and team # second, always */
            String teamKey = "frc" + row.get(1).getAsString();
            String rankingString;
            // use a CaseInsensitiveMap in order to find wins, losses, and ties below
            CaseInsensitiveMap<String> rankingElements = new CaseInsensitiveMap<>();
            for (int j = 2; j < row.size(); j++) {
                rankingElements.put(headerRow.get(j).getAsString(), row.get(j).getAsString());
            }

            String record = EventHelper.extractRankingString(rankingElements);

            if (record == null) {
                Set<String> keys = rankingElements.keySet();
                if (keys.contains("wins") && keys.contains("losses") && keys.contains("ties")) {
                    record = String.format("(%1$s-%2$s-%3$s",
                      rankingElements.get("wins"),
                      rankingElements.get("losses"),
                      rankingElements.get("ties"));
                    rankingElements.remove("wins");
                    rankingElements.remove("losses");
                    rankingElements.remove("ties");
                }
            }
            if (record == null) {
                record = "";
            }

            rankingString = EventHelper.createRankingBreakdown(rankingElements);

            Team team = mDb.getTeamsTable().get(teamKey);
            String nickname;
            if (team != null) {
                nickname = team.getNickname();
            } else {
                nickname = "Team " + teamKey.substring(3);
            }
            mDataToBind.add(
              new RankingListElement(
                teamKey,
                row.get(1).getAsInt(), // team number
                nickname,
                row.get(0).getAsInt(), // rank
                record,
                rankingString));
        }
        EventBus.getDefault().post(new EventRankingsEvent(generateTopRanksString()));
    }

    private String generateTopRanksString() {
        String rankString = "";
        if (mAPIData.size() <= 1) {
            return rankString;
        }
        for (int i = 1; i < Math.min(EventRankingsEvent.SIZE + 1, mAPIData.size()); i++) {
            rankString += ((i) + ". <b>" + mAPIData.get(i).getAsJsonArray().get(1).getAsString()) + "</b>";
            if (i < Math.min(6, mAPIData.size()) - 1) {
                rankString += "<br>";
            }
        }
        rankString = rankString.trim();
        return rankString;
    }
}
