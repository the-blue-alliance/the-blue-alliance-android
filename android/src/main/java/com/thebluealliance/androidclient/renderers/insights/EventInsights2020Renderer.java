package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2020Renderer extends EventInsightsRenderer {

    public EventInsights2020Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }

    @Override
    void generateYearSpecificInsights(JsonObject quals, JsonObject elims) {
        generate2020MatchInsights(quals, elims);
        generate2020BonusInsights(quals, elims);
    }

    private static @StringRes
    int[] matchInsightTitles = {R.string.breakdown_avg_score,
            R.string.breakdown_avg_win_score, R.string.breakdown_avg_win_margin,
            R.string.breakdown2020_avg_init_line_auto, R.string.breakdown2020_avg_cell_points_auto,
            R.string.breakdown2020_avg_cell_points_teleop, R.string.breakdown2020_avg_controL_panel_points,
            R.string.breakdown2020_avg_num_hang, R.string.breakdown2020_avg_endgame_score,
            R.string.breakdown_avg_foul_score};
    private static String[] matchInsightKeys = {"average_score", "average_win_score", "average_win_margin",
             "average_init_line_points_auto", "average_cell_points_auto", "average_cell_points_teleop",
            "average_control_panel_points", "average_num_robots_hanging", "average_endgame_points",
            "average_foul_score"};
    private void generate2020MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchInsightTitles, matchInsightKeys);
    }

    private static @StringRes int[] bonusInsightTitles = {R.string.breakdown2020_exit_init_line,
            R.string.breakdown2020_stage1_achieved, R.string.breakdown2020_stage2_achieved,
            R.string.breakdown2020_stage3_achieved, R.string.breakdown2020_robots_parking,
            R.string.breakdown2020_robots_hanging, R.string.breakdown2020_generator_level,
            R.string.breakdown2020_operational_rp, R.string.breakdown2020_energized_rp,
            R.string.breakdown2018_unicorn_matches};
    private static String[] bonusInsightKeys = {"exit_init_line_count", "achieve_stage1_count",
            "achieve_stage2_count", "achieve_stage3_count", "park_count", "hang_count",
            "generator_level_count", "generator_operational_rp_achieved",
            "generator_energized_rp_achieved", "unicorn_matches"};
    private void generate2020BonusInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2017_bonus_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, bonusInsightTitles, bonusInsightKeys);
    }
}
