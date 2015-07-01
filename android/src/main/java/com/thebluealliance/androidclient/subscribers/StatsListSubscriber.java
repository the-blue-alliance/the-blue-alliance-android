package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Stat;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import de.greenrobot.event.EventBus;

public class StatsListSubscriber extends BaseAPISubscriber<JsonObject, List<ListItem>> {

    private String mStatToSortBy;
    private Context mContext;
    private Database mDb;

    public StatsListSubscriber(Context context, Database db) {
        super();
        mContext = context;
        mDataToBind = new ArrayList<>();
        mDb = db;
    }

    public void setStatToSortBy(String stat) {
        mStatToSortBy = stat;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null ||
          !mAPIData.has("oprs") ||
          !mAPIData.has("dprs") ||
          !mAPIData.has("ccwms")) {
            return;
        }

        JsonObject oprs = mAPIData.get("oprs").getAsJsonObject();
        JsonObject dprs = mAPIData.get("dprs").getAsJsonObject();
        JsonObject ccwms = mAPIData.get("ccwms").getAsJsonObject();

        for (Entry<String, JsonElement> stat : oprs.entrySet()) {
            Team team = mDb.getTeamsTable().get("frc"+stat.getKey());
            String teamKey = team.getKey();
            double opr = stat.getValue().getAsDouble();
            double dpr = dprs.has(stat.getKey()) ? dprs.get(stat.getKey()).getAsDouble() : 0;
            double ccwm = ccwms.has(stat.getKey()) ? ccwms.get(stat.getKey()).getAsDouble() : 0;
            String displayString = mContext.getString(
              R.string.stats_format,
              Stat.displayFormat.format(opr),
              Stat.displayFormat.format(dpr),
              Stat.displayFormat.format(ccwm));
            mDataToBind.add(new StatsListElement(
              teamKey,
              Integer.toString(team.getTeamNumber()),
              team.getNickname(),
              displayString,
              opr,
              dpr,
              ccwm
            ));
        }
        Collections.sort(mDataToBind, new StatListElementComparator(mStatToSortBy));
        EventBus.getDefault().post(new EventStatsEvent(getTopStatsString()));
    }

    private String getTopStatsString() {
        String statsString = "";
        for (int i = 0; i < Math.min(EventStatsEvent.SIZE, mDataToBind.size()); i++) {
            String opr = ((StatsListElement)mDataToBind.get(i)).getFormattedOpr();
            statsString += (i + 1) + ". <b>" + opr + "</b>";
            if (i < Math.min(EventStatsEvent.SIZE, mDataToBind.size()) - 1) {
                statsString += "<br>";
            }
        }
        return statsString.trim();
    }
}