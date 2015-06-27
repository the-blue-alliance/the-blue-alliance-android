package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsListSubscriber extends BaseAPISubscriber<JsonObject, ListViewAdapter> {

    private String mStatToSortBy;
    private String mEventKey;
    private Context mContext;
    private Database mDb;

    public StatsListSubscriber(Context context, Database db) {
        super();
        mContext = context;
        mDataToBind = new EventStatsFragmentAdapter(context, new ArrayList<>());
        mDb = db;
    }

    public void setStatToSortBy(String stat) {
        mStatToSortBy = stat;
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.values.clear();
        if (mAPIData == null ||
          !mAPIData.has("oprs") ||
          !mAPIData.has("dprs") ||
          !mAPIData.has("ccwms")) {
            return;
        }

        JsonObject oprs = mAPIData.get("oprs").getAsJsonObject();
        JsonObject dprs = mAPIData.get("dprs").getAsJsonObject();
        JsonObject ccwms = mAPIData.get("ccwms").getAsJsonObject();
        List<Team> teamsAttending = mDb.getEventTeamsTable().getTeams(mEventKey);

        for (int i = 0; i < teamsAttending.size(); i++) {
            Team team = teamsAttending.get(i);
            String teamKey = team.getKey();
            double opr = oprs.has(teamKey) ? oprs.get(teamKey).getAsDouble() : 0;
            double dpr = dprs.has(teamKey) ? dprs.get(teamKey).getAsDouble() : 0;
            double ccwm = ccwms.has(teamKey) ? ccwms.get(teamKey).getAsDouble() : 0;
            String displayString = mContext.getString(R.string.stats_format, opr, dpr, ccwm);
            mDataToBind.values.add(new StatsListElement(
              teamKey,
              Integer.toString(team.getTeamNumber()),
              team.getNickname(),
              displayString,
              opr,
              dpr,
              ccwm
            ));
        }
        Collections.sort(mDataToBind.values, new StatListElementComparator(mStatToSortBy));
    }
}