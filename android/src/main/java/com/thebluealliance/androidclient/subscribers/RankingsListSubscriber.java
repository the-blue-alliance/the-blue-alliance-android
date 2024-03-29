package com.thebluealliance.androidclient.subscribers;

import static com.thebluealliance.androidclient.helpers.RankingFormatter.BOLD_TITLES;
import static com.thebluealliance.androidclient.helpers.RankingFormatter.LINE_BREAKS;
import static com.thebluealliance.androidclient.helpers.RankingFormatter.buildRankingString;
import static com.thebluealliance.androidclient.helpers.RankingFormatter.formatSortOrder;

import android.content.res.Resources;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.models.RankingItem;
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
        List<IRankingSortOrder> sortOrders = mAPIData.getSortOrderInfo();
        List<IRankingSortOrder> extraStats = mAPIData.getExtraStatsInfo();
        IRankingSortOrder firstSortInfo = sortOrders.remove(0);
        for (int i = 0; i < rankings.size(); i++) {
            IRankingItem row = rankings.get(i);
            /* Assume that the list of lists has rank first and team # second, always */
            String teamKey = row.getTeamKey();
            String rankingString;
            String rankingSummary;
            String record;

            @Nullable ITeamRecord teamRecord = row.getRecord();
            if (teamRecord != null) {
                record = "(" + RankingItem.TeamRecord.buildRecordString(teamRecord) + ")";
            } else {
                record = "";
            }


            // Remove the first sort item so we don't show duplicates
            Double firstSort = row.getSortOrders().remove(0);

            rankingSummary = mResources.getString(R.string.rank_item_summary,
                                                  firstSortInfo.getName(),
                                                  formatSortOrder(firstSortInfo, firstSort));

            rankingString = buildRankingString(row,
                                               sortOrders,
                                               extraStats,
                                               mResources,
                                               BOLD_TITLES | LINE_BREAKS);

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
                            rankingSummary,
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
