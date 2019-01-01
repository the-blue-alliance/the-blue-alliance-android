package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2017Renderer extends EventInsightsRenderer {


    public EventInsights2017Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }

    @Override
    void generateYearSpecificInsights(JsonObject quals, JsonObject elims) {
        generate2017MatchInsights(quals, elims);
        generate2017BonusInsights(quals, elims);
    }

    /* Strings for basic match insights */
    private static @StringRes int[] matchTitles = {R.string.breakdown_avg_score,
            R.string.breakdown_avg_win_score, R.string.breakdown_avg_win_margin,
            R.string.breakdown2017_average_mobility, R.string.breakdown2017_average_rotor,
            R.string.breakdown2017_average_fuel, R.string.breakdown_avg_high_goal,
            R.string.breakdown_avg_low_goal, R.string.breakdown2017_average_takeoff,
            R.string.breakdown_avg_foul_score};
    private static String[] matchKeys = {"average_score", "average_win_score", "average_win_margin",
            "average_mobility_points_auto", "average_rotor_points", "average_fuel_points",
            "average_high_goals", "average_low_goals", "average_takeoff_points_teleop",
            "average_foul_score"};

    private void generate2017MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchTitles, matchKeys);
    }

    private static @StringRes int[] bonusTitles = {R.string.breakdown2017_auto_mobility_points,
            R.string.breakdown2017_teleop_takeoff, R.string.breakdown2017_pressure_bonus,
            R.string.breakdown2017_rotor1_auto, R.string.breakdown2017_rotor2_auto,
            R.string.breakdown2017_rotor1, R.string.breakdown2017_rotor2,
            R.string.breakdown2017_rotor3, R.string.breakdown2017_rotor4};
    private static String[] bonusKeys = {"mobility_counts", "takeoff_counts", "kpa_achieved",
            "rotor_1_engaged_auto", "rotor_2_engaged_auto", "rotor_1_engaged", "rotor_2_engaged",
            "rotor_3_engaged", "rotor_4_engaged"};

    private void generate2017BonusInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2017_bonus_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, bonusTitles, bonusKeys);
    }
}
