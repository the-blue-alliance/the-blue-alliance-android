package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.ListPair;
import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Stat;
import com.thebluealliance.androidclient.models.Team;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import de.greenrobot.event.EventBus;

public class StatsListSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>> {

    private static DecimalFormat df = new DecimalFormat("#.##");

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
        ((ListPair)mDataToBind).setSelectedList(ListPair.LIST0);
        mDb = db;
        mEventYear = -1;
    }

    public void setEventYear(int year) {
        mEventYear = year;
    }

    public void setStatToSortBy(String stat) {
        mStatToSortBy = stat;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mTeamStats.clear();
        JsonObject statsData = mAPIData.getAsJsonObject();
        if (!statsData.has("oprs") || !statsData.get("oprs").isJsonObject() ||
          !statsData.has("dprs") ||!statsData.get("dprs").isJsonObject() ||
          !statsData.has("ccwms") || !statsData.get("ccwms").isJsonObject()) {
            return;
        }

        JsonObject oprs = statsData.get("oprs").getAsJsonObject();
        JsonObject dprs = statsData.get("dprs").getAsJsonObject();
        JsonObject ccwms = statsData.get("ccwms").getAsJsonObject();

        for (Entry<String, JsonElement> stat : oprs.entrySet()) {
            String teamKey = "frc" + stat.getKey();
            Team team = mDb.getTeamsTable().get(teamKey);
            String teamName = team == null ? "Team " + stat.getKey() : team.getNickname();
            double opr = stat.getValue().getAsDouble();
            double dpr = dprs.has(stat.getKey()) ? dprs.get(stat.getKey()).getAsDouble() : 0;
            double ccwm = ccwms.has(stat.getKey()) ? ccwms.get(stat.getKey()).getAsDouble() : 0;
            String displayString = mResources.getString(
              R.string.stats_format,
              Stat.displayFormat.format(opr),
              Stat.displayFormat.format(dpr),
              Stat.displayFormat.format(ccwm));
            mTeamStats.add(new StatsListElement(
              teamKey,
              stat.getKey(),
              teamName,
              displayString,
              opr,
              dpr,
              ccwm
            ));
        }
        Collections.sort(mTeamStats, new StatListElementComparator(mStatToSortBy));

        // Event stats
        if (mEventYear == 2016 && statsData.has("year_specific") && statsData.get("year_specific").isJsonObject()) {
            generateEventInsights(statsData.get("year_specific").getAsJsonObject());
        }

        mEventBus.post(new EventStatsEvent(getTopStatsString()));
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && mAPIData.isJsonObject();
    }

    private void generateEventInsights(JsonObject eventInsights) {
        mEventStats.clear();
        generate2016MatchInsights(eventInsights);
        generate2016DefenseInsights(eventInsights);
        generate2016TowerInsights(eventInsights);
    }

    private @StringRes int[] matchTitles = {R.string.breakdown2016_avg_low_goal, R.string
            .breakdown2016_avg_high_goal, R.string.breakdown2016_avg_score, R.string
            .breakdown2016_avg_win_score, R.string.breakdown2016_avg_win_margin, R.string
            .breakdown2016_avg_auto_score, R.string.breakdown2016_teleop_cross, R.string
            .breakdown2016_avg_boulder_score, R.string.breakdown2016_avg_tower_score, R.string.breakdown2016_avg_foul_score};
    private String[] matchKeys = {"average_low_goals", "average_high_goals", "average_score",
            "average_win_score", "average_win_margin", "average_auto_score",
            "average_crossing_score", "average_boulder_score", "average_tower_score", "average_foul_score"};

    private void generate2016MatchInsights(JsonObject eventInsights) {
        mEventStats.add(new EventTypeHeader("Match Stats"));

        JsonArray highScore = eventInsights.get("high_score").getAsJsonArray();
        mEventStats.add(new LabelValueListItem("High Score", String.format("%1$d in %2$s",
                highScore.get(0).getAsInt(), highScore.get(2).getAsString()), true));

        for (int i = 0; i < matchKeys.length; i++) {
            mEventStats.add(new LabelValueListItem(mResources.getString(matchTitles[i]), df.format
                    (eventInsights.get(matchKeys[i]).getAsDouble()), true));
        }
    }

    private String[] defenseTitle = {"LowBar", "A_ChevalDeFrise", "A_Portcullis", "B_Ramparts",
            "B_Moat", "C_SallyPort", "C_Drawbridge", "D_RoughTerrain", "D_RockWall", "breaches"};
    private @StringRes int[] defenseName = {R.string.defense2016_low_bar, R.string
            .defense2016_cdf, R.string.defense2016_portcullis, R.string.defense2016_ramparts, R
            .string.defense2016_moat, R.string.defense2016_sally_port, R.string
            .defense2016_drawbridge, R.string.defense2016_rough_terrain, R.string
            .defense2016_rock_wall, R.string.defense2016_breaches};

    private void generate2016DefenseInsights(JsonObject eventInsights) {
        mEventStats.add(new EventTypeHeader("Defense Stats"));
        String defenseFormat = mResources.getString(R.string.defense2016_cross_format);
        for (int i = 0; i < defenseName.length; i++) {
            JsonArray defenseData = eventInsights.get(defenseTitle[i]).getAsJsonArray();
            mEventStats.add(new LabelValueListItem(mResources.getString(defenseName[i]), String.format
                    (defenseFormat, defenseData.get(0).getAsInt(), defenseData.get(1).getAsInt(),
                            defenseData.get(2).getAsDouble()), true));
        }
    }

    private @StringRes int[] towerTitles = {R.string.breakdown2016_challenge, R.string
            .breakdown2016_scales, R.string.breakdown2016_captures};
    private String[] towerKeys = {"challenges", "scales", "captures"};

    private void generate2016TowerInsights(JsonObject eventInsights) {
        mEventStats.add(new EventTypeHeader("Tower Stats"));
        String defenseFormat = mResources.getString(R.string.defense2016_tower_format);
        for (int i = 0; i < towerTitles.length; i++) {
            JsonArray towerData = eventInsights.get(towerKeys[i]).getAsJsonArray();
            mEventStats.add(new LabelValueListItem(mResources.getString(towerTitles[i]), String
                    .format(defenseFormat, towerData.get(0).getAsInt(), towerData.get(1).getAsInt(),
                            towerData.get(2).getAsDouble()), true));
        }
    }

    private String getTopStatsString() {
        String statsString = "";
        for (int i = 0; i < Math.min(EventStatsEvent.SIZE, mTeamStats.size()); i++) {
            String opr = ((StatsListElement)mTeamStats.get(i)).getFormattedOpr();
            String teamName = ((StatsListElement)mTeamStats.get(i)).getTeamNumberString();
            statsString += (i + 1) + ". " + teamName + " - <b>" + opr + "</b>";
            if (i < Math.min(EventStatsEvent.SIZE, mTeamStats.size()) - 1) {
                statsString += "<br>";
            }
        }
        return statsString.trim();
    }
}