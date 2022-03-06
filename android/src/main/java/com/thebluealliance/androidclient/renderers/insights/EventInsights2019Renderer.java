package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2019Renderer extends EventInsightsRenderer {

    public EventInsights2019Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }
    @Override
    void generateYearSpecificInsights(JsonObject quals, JsonObject elims) {
        generate2019MatchInsights(quals, elims);
        generate2019BonusInsights(quals, elims);
    }

    /* Strings for basic match insights */
    private static @StringRes
    int[] matchTitles = {R.string.breakdown_avg_score,
            R.string.breakdown_avg_win_score, R.string.breakdown_avg_win_margin,
            R.string.breakdown2019_avg_sandstorm, R.string.breakdown2019_hatch_panel,
            R.string.breakdown2019_cargo, R.string.breakdown2019_hab_climb,
            R.string.breakdown_avg_foul_score};
    private static String[] matchKeys = {"average_score", "average_win_score", "average_win_margin",
            "average_sandstorm_bonus_auto", "average_hatch_panel_points", "average_cargo_points",
            "average_hab_climb_teleop", "average_foul_score"};

    private void generate2019MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchTitles, matchKeys);
    }

    private static @StringRes int[] bonusTitles = {R.string.breakdown2019_cross_hab_line,
        R.string.breakdown2019_cross_hab_line_sandstorm, R.string.breakdown2019_complete_rocket,
        R.string.breakdown2019_complete_two_rockets, R.string.breakdown2019_complete_rocket_rp,
        R.string.breakdown2019_level_1_climb, R.string.breakdown2019_level_2_climb,
        R.string.breakdown2019_level_3_climb, R.string.breakdown2019_hab_docking,
        R.string.breakdown2018_unicorn_matches};
    private static String[] bonusKeys = {"cross_hab_line_count", "cross_hab_line_sandstorm_count",
        "complete_1_rocket_count", "complete_2_rockets_count", "rocket_rp_achieved",
        "level1_climb_count", "level2_climb_count", "level3_climb_count", "climb_rp_achieved",
        "unicorn_matches"};

    private void generate2019BonusInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2017_bonus_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, bonusTitles, bonusKeys);
    }
}
