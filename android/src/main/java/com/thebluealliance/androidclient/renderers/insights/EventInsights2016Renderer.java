package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class EventInsights2016Renderer extends EventInsightsRenderer {

    public EventInsights2016Renderer(List<ListItem> eventStats, Resources resources) {
        super(eventStats, resources);
    }

    @Override
    void generateYearSpecificInsights(JsonObject qualData, JsonObject elimData) {
        generate2016MatchInsights(qualData, elimData);
        generate2016DefenseInsights(qualData, elimData);
        generate2016TowerInsights(qualData, elimData);
    }

    private @StringRes int[] matchTitles = {R.string.breakdown_avg_low_goal, R.string
            .breakdown_avg_high_goal, R.string.breakdown_avg_score, R.string
            .breakdown_avg_win_score, R.string.breakdown_avg_win_margin, R.string
            .breakdown_avg_auto_score, R.string.breakdown2016_teleop_cross, R.string
            .breakdown2016_avg_boulder_score, R.string.breakdown2016_avg_tower_score, R.string
            .breakdown_avg_foul_score};
    private String[] matchKeys = {"average_low_goals", "average_high_goals", "average_score",
            "average_win_score", "average_win_margin", "average_auto_score",
            "average_crossing_score", "average_boulder_score", "average_tower_score",
            "average_foul_score"};

    private void generate2016MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown_match_stats)));

        addHighScore(quals, elims, "high_score");
        addQualVsElimInsights(quals, elims, matchTitles, matchKeys);
    }

    private String[] defenseTitle = {"LowBar", "A_ChevalDeFrise", "A_Portcullis", "B_Ramparts",
            "B_Moat", "C_SallyPort", "C_Drawbridge", "D_RoughTerrain", "D_RockWall", "breaches"};
    private @StringRes int[] defenseName = {R.string.defense2016_low_bar, R.string
            .defense2016_cdf, R.string.defense2016_portcullis, R.string.defense2016_ramparts, R
            .string.defense2016_moat, R.string.defense2016_sally_port, R.string
            .defense2016_drawbridge, R.string.defense2016_rough_terrain, R.string
            .defense2016_rock_wall, R.string.defense2016_breaches};

    private void generate2016DefenseInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2016_defense_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, defenseName, defenseTitle);
    }

    private @StringRes int[] towerTitles = {R.string.breakdown2016_challenge, R.string
            .breakdown2016_scales, R.string.breakdown2016_captures};
    private String[] towerKeys = {"challenges", "scales", "captures"};

    private void generate2016TowerInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2016_tower_stats)));
        addQualVsElimInsightsWithPercentage(quals, elims, towerTitles, towerKeys);
    }
}
