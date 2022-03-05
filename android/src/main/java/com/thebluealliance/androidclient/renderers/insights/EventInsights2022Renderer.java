package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2022Renderer extends EventInsightsRenderer {

    public EventInsights2022Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }

    @Override
    void generateYearSpecificInsights(JsonObject quals, JsonObject elims) {
        generate2022MatchInsights(quals, elims);
        generate2022BonusInsights(quals, elims);
    }


    private static @StringRes
    final int[] matchInsightTitles = {
            R.string.breakdown_avg_score,
            R.string.breakdown_avg_win_score,
            R.string.breakdown_avg_win_margin,
            R.string.breakdown2022_avg_taxi_points,
            R.string.breakdown2022_avg_cargo_lower,
            R.string.breakdown2022_avg_cargo_upper,
            R.string.breakdown2022_avg_cargo,
            R.string.breakdown2022_avg_cargo_points,
            R.string.breakdown2022_avg_endgame_points,
            R.string.breakdown_avg_foul_score,
            R.string.breakdown_avg_score,
    };
    private static final String[] matchInsightKeys = {
            "average_score",
            "average_win_score",
            "average_win_margin",
            "average_taxi_points",
            "average_lower_cargo_count",
            "average_upper_cargo_count",
            "average_cargo_count",
            "average_cargo_points",
            "average_endgame_points",
            "average_foul_score",
            "average_score",
    };
    private void generate2022MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchInsightTitles, matchInsightKeys);
    }

    private static @StringRes int[] bonusInsightTitles = {
            R.string.breakdown2022_taxi_line,
            R.string.breakdown2022_quintet_achieved,
            R.string.breakdown2022_low_climb,
            R.string.breakdown2022_mid_climb,
            R.string.breakdown2022_high_climb,
            R.string.breakdown2022_traversal_climb,
            R.string.breakdown2022_cargo_bonus_achieved,
            R.string.breakdown2022_hangar_bonus_achieved,
            R.string.breakdown2018_unicorn_matches,
    };
    private static String[] bonusInsightKeys = {
            "taxi_count",
            "quintet_count",
            "low_climb_count",
            "mid_climb_count",
            "high_climb_count",
            "traversal_climb_count",
            "cargo_bonus_rp",
            "hangar_bonus_rp",
            "unicorn_matches",
    };
    private void generate2022BonusInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2017_bonus_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, bonusInsightTitles, bonusInsightKeys);
    }
}
