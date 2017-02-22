package com.thebluealliance.androidclient.renderers.insights;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.content.res.Resources;
import android.support.annotation.StringRes;

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

        // high_score
        String qualHighScore = null, elimHighScore = null;
        if (quals.has("high_score") && quals.get("high_score").isJsonArray()) {
            JsonArray qualHigh = quals.get("high_score").getAsJsonArray();
            qualHighScore = mResources.getString(R.string.breakdown_match_stat,
                                                 qualHigh.get(0).getAsInt(),
                                                 qualHigh.get(2).getAsString());
        }
        if (elims.has("high_score") && elims.get("high_score").isJsonArray()) {
            JsonArray elimHigh = elims.get("high_score").getAsJsonArray();
            elimHighScore = mResources.getString(R.string.breakdown_match_stat,
                                                 elimHigh.get(0).getAsInt(),
                                                 elimHigh.get(2).getAsString());
        }
        mEventStats.add(new LabelValueListItem(mResources.getString(R.string.breakdown_high_score),
                                               combineQualAndElimStat(qualHighScore,
                                                                      elimHighScore),
                                               true));

        for (int i = 0; i < matchKeys.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(matchKeys[i]) && quals.get(matchKeys[i]).isJsonPrimitive()) {
                qualStat = df.format(quals.get(matchKeys[i]).getAsDouble());
            }
            if (elims.has(matchKeys[i]) && elims.get(matchKeys[i]).isJsonPrimitive()) {
                elimStat = df.format(elims.get(matchKeys[i]).getAsDouble());
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(matchTitles[i]),
                                                   combineQualAndElimStat(qualStat, elimStat),
                                                   true));
        }
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
        String defenseFormat = mResources.getString(R.string.breakdown_percent_format);
        for (int i = 0; i < defenseName.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(defenseTitle[i]) && quals.get(defenseTitle[i]).isJsonArray()) {
                JsonArray qualData = quals.get(defenseTitle[i]).getAsJsonArray();
                qualStat = String.format(defenseFormat, qualData.get(0).getAsInt(), qualData.get(1).getAsInt(),
                                         qualData.get(2).getAsDouble());
            }
            if (elims.has(defenseTitle[i]) && elims.get(defenseTitle[i]).isJsonArray()) {
                JsonArray elimData = elims.get(defenseTitle[i]).getAsJsonArray();
                elimStat = String.format(defenseFormat, elimData.get(0).getAsInt(), elimData.get(1)
                                                                                            .getAsInt(), elimData.get(2).getAsDouble());
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(defenseName[i]),
                                                   combineQualAndElimStat(qualStat, elimStat),
                                                   true));
        }
    }

    private @StringRes int[] towerTitles = {R.string.breakdown2016_challenge, R.string
            .breakdown2016_scales, R.string.breakdown2016_captures};
    private String[] towerKeys = {"challenges", "scales", "captures"};

    private void generate2016TowerInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader(mResources.getString(R.string.breakdown2016_tower_stats)));
        String defenseFormat = mResources.getString(R.string.breakdown_percent_format);
        for (int i = 0; i < towerTitles.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(towerKeys[i]) && quals.get(towerKeys[i]).isJsonArray()) {
                JsonArray qualData = quals.get(towerKeys[i]).getAsJsonArray();
                qualStat = String.format(defenseFormat, qualData.get(0).getAsInt(), qualData.get(1)
                                                                                            .getAsInt(), qualData.get(2).getAsDouble());
            }
            if (elims.has(towerKeys[i]) && elims.get(towerKeys[i]).isJsonArray()) {
                JsonArray elimData = elims.get(towerKeys[i]).getAsJsonArray();
                elimStat = String.format(defenseFormat, elimData.get(0).getAsInt(), elimData.get(1)
                                                                                            .getAsInt(), elimData.get(2).getAsDouble());
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(towerTitles[i]),
                                                   combineQualAndElimStat(qualStat, elimStat),
                                                   true));
        }
    }
}
