package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.binders.ListViewBinder;
import com.thebluealliance.androidclient.comparators.PointBreakdownComparater;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DistrictPointsListSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>>{

    private Database mDb;
    private String mEventKey;
    private Gson mGson;

    public DistrictPointsListSubscriber(Database db, Gson gson) {
        super();
        mDb = db;
        mGson = gson;
        mDataToBind = new Type();
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null || !mAPIData.isJsonObject()) {
            return;
        }

        JsonObject rankingsData = mAPIData.getAsJsonObject();
        if (!rankingsData.has("points")) {
            return;
        }
        JsonObject points = rankingsData.get("points").getAsJsonObject();
        String districtKey = "";
        Event event = mDb.getEventsTable().get(mEventKey);

        if (event != null) {
            DistrictHelper.DISTRICTS type = DistrictHelper.DISTRICTS.fromEnum(event.getDistrictEnum());
            boolean isDistrict = type != DistrictHelper.DISTRICTS.NO_DISTRICT;
            ((Type)mDataToBind).isDistrict = isDistrict;
            if (isDistrict) {
                districtKey = mEventKey.substring(0, 4) + type.getAbbreviation();
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
            mDataToBind.add(pointBreakdowns.get(i).render());
        }
    }

    /**
     * Custom bind datatype, extend List<ListItem> in order to use {@link ListViewBinder} within
     * the fragment.
     */
    public static class Type extends ArrayList<ListItem> {
        public boolean isDistrict;

        public Type() {
            super();
            this.isDistrict = false;
        }
    }
}
