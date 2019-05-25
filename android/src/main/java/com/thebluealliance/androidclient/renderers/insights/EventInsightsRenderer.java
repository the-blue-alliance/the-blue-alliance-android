package com.thebluealliance.androidclient.renderers.insights;

import android.content.res.Resources;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.text.DecimalFormat;
import java.util.List;

public abstract class EventInsightsRenderer {

    static DecimalFormat df = new DecimalFormat("#.##");

    final List<ListItem> mEventStats;
    final Resources mResources;

    public EventInsightsRenderer(List<ListItem> eventStats, Resources resources) {
        mEventStats = eventStats;
        mResources = resources;
    }

    public void generateEventInsights(@Nullable JsonElement apiInsights) {
        mEventStats.clear();
        JsonObject eventInsights;
        JsonObject qualData;
        JsonObject elimData;
        if (apiInsights == null || !apiInsights.isJsonObject()) {
            return;
        }
        eventInsights = apiInsights.getAsJsonObject();
        if (eventInsights.has("qual") && eventInsights.get("qual").isJsonObject()) {
            qualData = eventInsights.get("qual").getAsJsonObject();
        } else {
            qualData = new JsonObject();
        }
        if (eventInsights.has("playoff") && eventInsights.get("playoff").isJsonObject()) {
            elimData = eventInsights.get("playoff").getAsJsonObject();
        } else {
            elimData = new JsonObject();
        }

        generateYearSpecificInsights(qualData, elimData);
    }

    abstract void generateYearSpecificInsights(JsonObject quals, JsonObject elims);

    void addHighScore(JsonObject quals, JsonObject elims, String jsonKey) {
        String qualHighScore = null, elimHighScore = null;
        if (quals.has(jsonKey) && quals.get(jsonKey).isJsonArray()) {
            JsonArray qualHigh = quals.get(jsonKey).getAsJsonArray();
            qualHighScore = mResources.getString(R.string.breakdown_match_stat,
                                                 qualHigh.get(0).getAsInt(),
                                                 qualHigh.get(2).getAsString());
        }
        if (elims.has(jsonKey) && elims.get(jsonKey).isJsonArray()) {
            JsonArray elimHigh = elims.get(jsonKey).getAsJsonArray();
            elimHighScore = mResources.getString(R.string.breakdown_match_stat,
                                                 elimHigh.get(0).getAsInt(),
                                                 elimHigh.get(2).getAsString());
        }
        mEventStats.add(new LabelValueListItem(mResources.getString(R.string.breakdown_high_score),
                                               combineQualAndElimStat(qualHighScore,
                                                                      elimHighScore),
                                               true));
    }

    void addQualVsElimInsights(JsonObject quals, JsonObject elims,
                               @StringRes int[] titles, String[] jsonKeys) {
        for (int i = 0; i < jsonKeys.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(jsonKeys[i]) && quals.get(jsonKeys[i]).isJsonPrimitive()) {
                qualStat = df.format(quals.get(jsonKeys[i]).getAsDouble());
            }
            if (elims.has(jsonKeys[i]) && elims.get(jsonKeys[i]).isJsonPrimitive()) {
                elimStat = df.format(elims.get(jsonKeys[i]).getAsDouble());
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(titles[i]),
                                                   combineQualAndElimStat(qualStat, elimStat),
                                                   true));
        }
    }

    void addQualVsElimInsightsWithPercentage(JsonObject quals, JsonObject elims,
                                             @StringRes int[] titles, String[] jsonKeys) {
        String format = mResources.getString(R.string.breakdown_percent_format);
        for (int i = 0; i < jsonKeys.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(jsonKeys[i]) && quals.get(jsonKeys[i]).isJsonArray()) {
                JsonArray qualData = quals.get(jsonKeys[i]).getAsJsonArray();
                qualStat = String.format(format, qualData.get(0).getAsInt(), qualData.get(1).getAsInt(),
                                         qualData.get(2).getAsDouble());
            }
            if (elims.has(jsonKeys[i]) && elims.get(jsonKeys[i]).isJsonArray()) {
                JsonArray elimData = elims.get(jsonKeys[i]).getAsJsonArray();
                elimStat = String.format(format,
                                         elimData.get(0).getAsInt(),     // # success
                                         elimData.get(1).getAsInt(),     // # Opportunities
                                         elimData.get(2).getAsDouble()); // Completion Percentage
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(titles[i]),
                                                   combineQualAndElimStat(qualStat, elimStat),
                                                   true));
        }
    }

    private String combineQualAndElimStat(@Nullable String qualStat, @Nullable String elimStat) {
        if (qualStat != null && elimStat != null) {
            return mResources.getString(R.string.breakdown_qual_and_elim, qualStat, elimStat);
        } else if (qualStat != null) {
            return mResources.getString(R.string.breakdown_qual, qualStat);
        } else {
            return mResources.getString(R.string.breakdown_elim, elimStat);
        }
    }
}
