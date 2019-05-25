package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;
import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2018Renderer extends EventInsightsRenderer {

    public EventInsights2018Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }

    @Override
    void generateYearSpecificInsights(JsonObject quals, JsonObject elims) {
        generate2018MatchInsights(quals, elims);
        generate2018BonusInsights(quals, elims);
    }

    /* Strings for basic match insights */
    private static @StringRes
    int[] matchTitles = {R.string.breakdown_avg_score,
            R.string.breakdown_avg_win_score,
            R.string.breakdown_avg_win_margin,
            R.string.breakdown2018_average_auto_run,
            R.string.breakdown2018_average_scale,
            R.string.breakdown2018_average_switch,
            R.string.breakdown2018_scale_neutral_pct,
            R.string.breakdown2018_winner_scale_ownership,
            R.string.breakdown2018_winner_switch_ownersihp,
            R.string.breakdown2018_winner_switch_denial,
            R.string.breakdown2018_avg_force,
            R.string.breakdown2018_avg_boost,
            R.string.breakdown2018_avg_vault,
            R.string.breakdown2018_avg_endgame,
            R.string.breakdown_avg_foul_score};
    private static String[] matchKeys = {"average_score",
            "average_win_score",
            "average_win_margin",
            "average_run_points_auto",
            "average_scale_ownership_points",
            "average_switch_ownership_points",
            "scale_neutral_percentage",
            "winning_scale_ownership_percentage",
            "winning_own_switch_ownership_percentage",
            "winning_opp_switch_denial_percentage_teleop",
            "average_force_played",
            "average_boost_played",
            "average_vault_points",
            "average_endgame_points",
            "average_foul_score"};

    private void generate2018MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchTitles, matchKeys);
    }

    private static @StringRes int[] bonusTitles = {R.string.breakdown2018_auto_run,
            R.string.breakdown2018_auto_switch_owned, R.string.breakdown2018_auto_quest,
            R.string.breakdown2018_force_played, R.string.breakdown2018_levitate_played,
            R.string.breakdown2018_boot_played, R.string.breakdown2018_climbs,
            R.string.breakdown2018_face_the_boss, R.string.breakdown2018_unicorn_matches};
    private static String[] bonusKeys = {"run_counts_auto", "switch_owned_counts_auto",
            "auto_quest_achieved", "force_played_counts", "levitate_played_counts",
            "boost_played_counts", "climb_counts", "face_the_boss_achieved", "unicorn_matches"};
    private void generate2018BonusInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2017_bonus_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, bonusTitles, bonusKeys);
    }
}
