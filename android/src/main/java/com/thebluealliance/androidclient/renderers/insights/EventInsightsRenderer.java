package com.thebluealliance.androidclient.renderers.insights;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.content.res.Resources;
import android.support.annotation.Nullable;

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

    String combineQualAndElimStat(@Nullable String qualStat, @Nullable String elimStat) {
        if (qualStat != null && elimStat != null) {
            return mResources.getString(R.string.breakdown_qual_and_elim, qualStat, elimStat);
        } else if (qualStat != null) {
            return mResources.getString(R.string.breakdown_qual, qualStat);
        } else {
            return mResources.getString(R.string.breakdown_elim, elimStat);
        }
    }
}
