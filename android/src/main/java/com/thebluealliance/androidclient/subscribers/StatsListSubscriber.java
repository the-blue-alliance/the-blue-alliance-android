package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.ListPair;
import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.insights.EventInsights2016Renderer;
import com.thebluealliance.androidclient.renderers.insights.EventInsights2017Renderer;
import com.thebluealliance.androidclient.renderers.insights.EventInsightsRenderer;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class StatsListSubscriber extends BaseAPISubscriber<StatsListSubscriber.Model, List<ListItem>> {


    private String mStatToSortBy;
    private Resources mResources;
    private Database mDb;
    private EventBus mEventBus;
    private List<ListItem> mTeamStats;
    private List<ListItem> mEventStats;
    private int mEventYear;

    public StatsListSubscriber(Resources resources, Database db, EventBus eventBus) {
        super();
        mResources = resources;
        mEventBus = eventBus;
        mTeamStats = new ArrayList<>();
        mEventStats = new ArrayList<>();
        mDataToBind = new ListPair<>(mTeamStats, mEventStats);
        ((ListPair) mDataToBind).setSelectedList(ListPair.LIST0);
        mDb = db;
        mEventYear = -1;
        mStatToSortBy = "";
    }

    public void setEventYear(int year) {
        mEventYear = year;
    }

    public void setStatToSortBy(String stat) {
        mStatToSortBy = stat;
    }

    @Override
    public void parseData()  {
        mTeamStats.clear();
        JsonObject statsData = mAPIData.getStats().getAsJsonObject();
        if (!statsData.has("oprs") || !statsData.get("oprs").isJsonObject()
                || !statsData.has("dprs") || !statsData.get("dprs").isJsonObject()
                || !statsData.has("ccwms") || !statsData.get("ccwms").isJsonObject()) {
            return;
        }

        JsonObject oprs = statsData.get("oprs").getAsJsonObject();
        JsonObject dprs = statsData.get("dprs").getAsJsonObject();
        JsonObject ccwms = statsData.get("ccwms").getAsJsonObject();

        for (Entry<String, JsonElement> stat : oprs.entrySet()) {
            String teamKey = stat.getKey();
            String teamNumber = teamKey.substring(3);
            Team team = mDb.getTeamsTable().get(teamKey);
            String teamName = team == null ? "Team " + teamNumber : team.getNickname();
            double opr = stat.getValue().getAsDouble();
            double dpr = dprs.has(stat.getKey()) ? dprs.get(stat.getKey()).getAsDouble() : 0;
            double ccwm = ccwms.has(stat.getKey()) ? ccwms.get(stat.getKey()).getAsDouble() : 0;
            String displayString = mResources.getString(
                    R.string.stats_format,
                    ThreadSafeFormatters.formatDoubleTwoPlaces(opr),
                    ThreadSafeFormatters.formatDoubleTwoPlaces(dpr),
                    ThreadSafeFormatters.formatDoubleTwoPlaces(ccwm));
            mTeamStats.add(new StatsListElement(
                    teamKey,
                    teamNumber,
                    teamName,
                    displayString,
                    opr,
                    dpr,
                    ccwm));
        }
        Collections.sort(mTeamStats, new StatListElementComparator(mStatToSortBy));

        // Event stats
        EventInsightsRenderer insightsRenderer = null;
        switch (mEventYear) {
            case 2016:
                insightsRenderer = new EventInsights2016Renderer(mEventStats, mResources);
                break;
            case 2017:
                insightsRenderer = new EventInsights2017Renderer(mEventStats, mResources);
                break;
        }

        if (insightsRenderer != null) {
            insightsRenderer.generateEventInsights(mAPIData.getInsights());
        }

        mEventBus.post(new EventStatsEvent(getTopStatsString()));
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && mAPIData.getStats() != null && mAPIData.getStats().isJsonObject();
    }

    private String getTopStatsString() {
        String statsString = "";
        for (int i = 0; i < Math.min(EventStatsEvent.SIZE, mTeamStats.size()); i++) {
            String opr = ((StatsListElement) mTeamStats.get(i)).getFormattedOpr();
            String teamName = ((StatsListElement) mTeamStats.get(i)).getTeamNumberString();
            statsString += (i + 1) + ". " + teamName + " - <b>" + opr + "</b>";
            if (i < Math.min(EventStatsEvent.SIZE, mTeamStats.size()) - 1) {
                statsString += "<br>";
            }
        }
        return statsString.trim();
    }

    public static class Model {
        private final JsonElement mStats;
        private final JsonElement mInsights;

        public Model(JsonElement stats, JsonElement insights) {
            mStats = stats;
            mInsights = insights;
        }

        public JsonElement getStats() {
            return mStats;
        }

        public JsonElement getInsights() {
            return mInsights;
        }
    }
}