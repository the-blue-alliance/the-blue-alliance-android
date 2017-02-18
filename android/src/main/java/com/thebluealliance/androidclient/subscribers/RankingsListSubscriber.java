package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.ITeamRecord;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class RankingsListSubscriber extends BaseAPISubscriber<RankingResponseObject, List<Object>> {

    private final Database mDb;
    private final EventBus mEventBus;
    private final Resources mResources;

    public RankingsListSubscriber(Database db, EventBus eventBus, Resources resources) {
        super();
        mDb = db;
        mDataToBind = new ArrayList<>();
        mEventBus = eventBus;
        mResources = resources;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        if (mAPIData == null || mAPIData.getRankings() == null || mAPIData.getRankings().isEmpty()) {
            return;
        }

        List<IRankingItem> rankings = mAPIData.getRankings();
        for (int i = 1; i < rankings.size(); i++) {
            IRankingItem row = rankings.get(i);
            /* Assume that the list of lists has rank first and team # second, always */
            String teamKey = row.getTeamKey();
            String rankingString;
            String record;

            @Nullable ITeamRecord teamRecord = row.getRecord();
            if (teamRecord != null) {
                record = RankingItem.TeamRecord.buildRecordString(teamRecord);
            } else {
                record = "";
            }

            rankingString = EventHelper.buildRankingString(row,
                                                           mAPIData.getSortOrderInfo(),
                                                           mResources);

            Team team = mDb.getTeamsTable().get(teamKey);
            String nickname;
            if (team != null) {
                nickname = team.getNickname();
            } else {
                nickname = "Team " + teamKey.substring(3);
            }

            mDataToBind.add(
                    new TeamRankingViewModel(
                            teamKey,
                            nickname,
                            teamKey.substring(3), // team number
                            row.getRank(), // rank
                            record,
                            rankingString));
        }
        mEventBus.post(new EventRankingsEvent(generateTopRanksString(mAPIData)));
    }

    @Override public boolean isDataValid() {
        return super.isDataValid()
               && mAPIData.getRankings() != null
               && !mAPIData.getRankings().isEmpty()
               && mAPIData.getSortOrderInfo() != null;
    }

    private String generateTopRanksString(RankingResponseObject rankings) {
        String rankString = "";
        if (rankings.getRankings().isEmpty()) {
            return rankString;
        }
        List<IRankingItem> rankingsData = rankings.getRankings();
        for (int i = 0; i < Math.min(EventRankingsEvent.SIZE, rankingsData.size()); i++) {
            rankString += ((i+1) + ". <b>" + rankingsData.get(i).getTeamKey().substring(3)) + "</b>";
            if (i < Math.min(6, rankingsData.size()) - 1) {
                rankString += "<br>";
            }
        }
        rankString = rankString.trim();
        return rankString;
    }
}
