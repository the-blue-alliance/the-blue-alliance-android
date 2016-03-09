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
import android.support.annotation.Nullable;
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
        JsonObject qualData;
        JsonObject elimData;
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

        generate2016MatchInsights(qualData, elimData);
        generate2016DefenseInsights(qualData, elimData);
        generate2016TowerInsights(qualData, elimData);
    }

    private @StringRes int[] matchTitles = {R.string.breakdown2016_avg_low_goal, R.string
            .breakdown2016_avg_high_goal, R.string.breakdown2016_avg_score, R.string
            .breakdown2016_avg_win_score, R.string.breakdown2016_avg_win_margin, R.string
            .breakdown2016_avg_auto_score, R.string.breakdown2016_teleop_cross, R.string
            .breakdown2016_avg_boulder_score, R.string.breakdown2016_avg_tower_score, R.string.breakdown2016_avg_foul_score};
    private String[] matchKeys = {"average_low_goals", "average_high_goals", "average_score",
            "average_win_score", "average_win_margin", "average_auto_score",
            "average_crossing_score", "average_boulder_score", "average_tower_score", "average_foul_score"};

    private void generate2016MatchInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader("Match Stats"));

        String qualFormat = mResources.getString(R.string.breakdown2016_qual);
        String elimFormat = mResources.getString(R.string.breakdown2016_elim);

        // high_score
        String qualHighScore = null, elimHighScore = null;
        if (quals.has("high_score") && quals.get("high_score").isJsonArray()) {
            JsonArray qualHigh = quals.get("high_score").getAsJsonArray();
            qualHighScore = mResources.getString(R.string.breakdown2016_match_stat, qualHigh
                    .get(0).getAsInt(), qualHigh.get(2).getAsString());
        }
        if (elims.has("high_score") && elims.get("high_score").isJsonArray()) {
            JsonArray elimHigh = elims.get("high_score").getAsJsonArray();
            elimHighScore = mResources.getString(R.string.breakdown2016_match_stat, elimHigh
                    .get(0).getAsInt(), elimHigh.get(2).getAsString());
        }
        mEventStats.add(new LabelValueListItem(mResources.getString(R.string
                .breakdown2016_high_score), combineQualAndElimStat(qualHighScore, elimHighScore)));
        
        for (int i = 0; i < matchKeys.length; i++) {
            String qualStat = null, elimStat = null;
            if (quals.has(matchKeys[i]) && quals.get(matchKeys[i]).isJsonPrimitive()) {
                qualStat = df.format(quals.get(matchKeys[i]).getAsDouble());
            }
            if (elims.has(matchKeys[i]) && elims.get(matchKeys[i]).isJsonPrimitive()) {
                elimStat = df.format(elims.get(matchKeys[i]).getAsDouble());
            }
            mEventStats.add(new LabelValueListItem(mResources.getString(matchTitles[i]),
                    combineQualAndElimStat(qualStat, elimStat), true));
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
        mEventStats.add(new EventTypeHeader("Defense Stats"));
        String defenseFormat = mResources.getString(R.string.defense2016_cross_format);
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
                    combineQualAndElimStat(qualStat, elimStat), true));
        }
    }

    private @StringRes int[] towerTitles = {R.string.breakdown2016_challenge, R.string
            .breakdown2016_scales, R.string.breakdown2016_captures};
    private String[] towerKeys = {"challenges", "scales", "captures"};

    private void generate2016TowerInsights(JsonObject quals, JsonObject elims) {
        mEventStats.add(new EventTypeHeader("Tower Stats"));
        String defenseFormat = mResources.getString(R.string.defense2016_tower_format);
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
                    combineQualAndElimStat(qualStat, elimStat), true));
        }
    }

    private String combineQualAndElimStat(@Nullable String qualStat, @Nullable  String elimStat) {
        if (qualStat != null && elimStat != null) {
            return mResources.getString(R.string.breakdown2016_qual_and_elim, qualStat, elimStat);
        } else if (qualStat != null) {
            return mResources.getString(R.string.breakdown2016_qual, qualStat);
        } else {
            return mResources.getString(R.string.breakdown2016_elim, elimStat);
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