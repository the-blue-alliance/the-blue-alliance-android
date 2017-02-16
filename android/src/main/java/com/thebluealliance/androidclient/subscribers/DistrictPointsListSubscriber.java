package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.comparators.PointBreakdownComparater;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DistrictPointsListSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>>{

    private Database mDb;
    private String mEventKey;
    private Gson mGson;
    private DistrictPointBreakdownRenderer mRenderer;

    public DistrictPointsListSubscriber(Database db, Gson gson, DistrictPointBreakdownRenderer renderer) {
        super();
        mDb = db;
        mGson = gson;
        mRenderer = renderer;
        mDataToBind = new Type();
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        JsonObject rankingsData = mAPIData.getAsJsonObject();
        if (!rankingsData.has("points")) {
            return;
        }
        JsonObject points = rankingsData.get("points").getAsJsonObject();
        String districtKey = "";
        Event event = mDb.getEventsTable().get(mEventKey);

        if (event != null) {
            boolean isDistrict = (event.getDistrict() != null);
            ((Type)mDataToBind).isDistrict = isDistrict;
            if (isDistrict) {
                districtKey = event.getDistrict();
            }
        }

        ArrayList<DistrictPointBreakdown> pointBreakdowns = new ArrayList<>();
        for (Map.Entry<String, JsonElement> teamPoints : points.entrySet()) {
            Team team = mDb.getTeamsTable().get(teamPoints.getKey());
            DistrictPointBreakdown b = mGson.fromJson(
              teamPoints.getValue(),
              DistrictPointBreakdown.class);
            b.setTeamKey(teamPoints.getKey());
            b.setTeamName(team != null ? team.getNickname() : "Team " + teamPoints.getKey().substring(3));
            b.setDistrictKey(districtKey);
            pointBreakdowns.add(b);
        }

        Collections.sort(pointBreakdowns, new PointBreakdownComparater());

        for (int i = 0; i < pointBreakdowns.size(); i++) {
            pointBreakdowns.get(i).setRank(i + 1);
            mDataToBind.add(mRenderer.renderFromModel(pointBreakdowns.get(i), null));
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && mAPIData.isJsonObject();
    }

    /**
     * Custom bind datatype, extend List<ListItem> in order to use
     * {@link com.thebluealliance.androidclient.binders.ListViewBinder} within the fragment.
     */
    public static class Type extends ArrayList<ListItem> {
        public boolean isDistrict;

        public Type() {
            super();
            this.isDistrict = false;
        }
    }
}
