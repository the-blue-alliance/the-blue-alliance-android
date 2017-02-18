package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventHelper.CaseInsensitiveMap;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.viewmodels.TeamRankingViewModel;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingSortOrder;
import com.thebluealliance.api.model.ITeamRecord;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class RankingsListSubscriber extends BaseAPISubscriber<RankingResponseObject, List<Object>> {

    private Database mDb;
    private EventBus mEventBus;

    public RankingsListSubscriber(Database db, EventBus eventBus) {
        super();
        mDb = db;
        mDataToBind = new ArrayList<>();
        mEventBus = eventBus;
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
            // use a CaseInsensitiveMap in order to find wins, losses, and ties below
            CaseInsensitiveMap<String> rankingElements = new CaseInsensitiveMap<>();
            if (row.getQualAverage() != null) {
                rankingElements.put("Qual Average",
                                    ThreadSafeFormatters
                                            .formatDoubleOnePlace(row.getQualAverage()));
            }
            for (int j = 0; j < mAPIData.getSortOrderInfo().size(); j++) {
                String rankString;
                Double rankValue = row.getSortOrders().get(j);
                IRankingSortOrder sort = mAPIData.getSortOrderInfo().get(j);
                switch (sort.getPrecision()) {
                    case 0:
                        rankString = ThreadSafeFormatters.formatDoubleNoPlaces(rankValue);
                        break;
                    case 1:
                        rankString = ThreadSafeFormatters.formatDoubleOnePlace(rankValue);
                        break;
                    default:
                    case 2:
                        rankString = ThreadSafeFormatters.formatDoubleTwoPlaces(rankValue);
                        break;
                }
                rankingElements.put(sort.getName(), rankString);
            }

            @Nullable ITeamRecord teamRecord = row.getRecord();
            if (teamRecord != null
                    && teamRecord.getWins() != null
                    && teamRecord.getLosses() != null
                    && teamRecord.getTies() != null) {
                StringBuilder recordBuilder = new StringBuilder();
                recordBuilder.append("(");
                recordBuilder.append(teamRecord.getWins());
                recordBuilder.append("-");
                recordBuilder.append(teamRecord.getLosses());
                if (teamRecord.getTies() > 0) {
                    recordBuilder.append("-");
                    recordBuilder.append(teamRecord.getTies());
                }
                recordBuilder.append(")");
                record = recordBuilder.toString();
            } else {
                record = "";
            }
            rankingElements.put("Played", Integer.toString(row.getMatchesPlayed()));
            rankingElements.put("DQ", Integer.toString(row.getDq()));
            rankingString = EventHelper.createRankingBreakdown(rankingElements);

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
