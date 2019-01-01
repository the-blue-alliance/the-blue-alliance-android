package com.thebluealliance.androidclient.helpers;

import android.content.res.Resources;
import android.support.annotation.IntDef;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingSortOrder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class RankingFormatter {

    public static final int NONE = 0;
    public static final int BOLD_TITLES = 1;
    public static final int LINE_BREAKS = 1<<1;

    @IntDef(flag=true,
            value={NONE, BOLD_TITLES, LINE_BREAKS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RankingStringOptions{}

    private RankingFormatter() {
        // unused
    }

    public static String formatSortOrder(IRankingSortOrder sort, Double rankValue) {
        switch (sort.getPrecision()) {
            case 0:
                return ThreadSafeFormatters.formatDoubleNoPlaces(rankValue);
            case 1:
                return ThreadSafeFormatters.formatDoubleOnePlace(rankValue);
            default:
            case 2:
                return ThreadSafeFormatters.formatDoubleTwoPlaces(rankValue);
        }
    }

    public static String buildRankingString(IRankingItem rankData,
                                            List<IRankingSortOrder> sortOrders,
                                            @Nullable List<IRankingSortOrder> extraStats,
                                            Resources resources,
                                            @RankingStringOptions int flags) {
        Map<String, String> rankingElements = new LinkedHashMap<>();
        if (rankData.getQualAverage() != null) {
            rankingElements.put(resources.getString(R.string.rank_qual_average),
                                ThreadSafeFormatters.formatDoubleOnePlace(rankData.getQualAverage()));
        }
        for (int j = 0; j < Math.min(sortOrders.size(), rankData.getSortOrders().size()); j++) {
            String rankString;
            Double rankValue = rankData.getSortOrders().get(j);
            IRankingSortOrder sort = sortOrders.get(j);
            rankString = formatSortOrder(sort, rankValue);
            rankingElements.put(sort.getName(), rankString);
        }

        for (int j = 0;
             extraStats != null && j < Math.min(extraStats.size(), rankData.getExtraStats().size());
             j++) {
            String rankString;
            Double rankValue = rankData.getExtraStats().get(j);
            IRankingSortOrder sort = extraStats.get(j);
            rankString = formatSortOrder(sort, rankValue);
            rankingElements.put(sort.getName(), rankString);
        }

        rankingElements.put(resources.getString(R.string.rank_played),
                            Integer.toString(rankData.getMatchesPlayed()));
        rankingElements.put(resources.getString(R.string.rank_dq),
                            Integer.toString(rankData.getDq()));
        return createRankingBreakdown(rankingElements, flags);
    }

    private static String createRankingBreakdown(Map<String, String> rankingElements,
                                                 @RankingStringOptions int flags) {
        String rankingString = "";
        // Construct rankings string
        Iterator it = rankingElements.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String value = entry.getValue().toString();

            // Capitalization hack
            String rankingKey = entry.getKey().toString();
            if (rankingKey.length() <= 3) {
                rankingKey = rankingKey.toUpperCase();
            } else {
                rankingKey = capitalize(rankingKey);
            }
            if ((flags & BOLD_TITLES) != 0) {
                rankingKey = "<b>" + rankingKey + "</b>";
            }
            rankingString += rankingKey + ": " + value;
            if (it.hasNext()) {
                rankingString +=  (flags & LINE_BREAKS) != 0 ? "<br>" : ", ";
            }
        }
        return rankingString;
    }

    /**
     * Hacky capitalize method to remove dependency on apache lib for only one method Stupid DEX
     * limit...
     *
     * @param string Input string
     * @return Input string with first letter of each word capitalized
     */
    private static String capitalize(String string) {
        StringBuilder sb = new StringBuilder();
        String[] split = string.split(" ");
        for (String s : split) {
            sb.append(s.substring(0, 1).toUpperCase());
            sb.append(s.substring(1));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
