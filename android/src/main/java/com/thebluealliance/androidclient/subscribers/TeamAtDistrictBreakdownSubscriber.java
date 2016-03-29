package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamAtDistrictBreakdownSubscriber
  extends BaseAPISubscriber<DistrictTeam, List<ListGroup>> {

    private Resources mResources;
    private Database mDb;
    private Gson mGson;

    public TeamAtDistrictBreakdownSubscriber(Resources resources, Database db, Gson gson) {
        super();
        mResources = resources;
        mDb = db;
        mGson = gson;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public synchronized void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        Map<String, JsonObject> eventBreakdowns =
          Utilities.getMapForPlatform(String.class, JsonObject.class);
        JsonObject rawDistrictTeam = mGson.fromJson(mAPIData.getJson(), JsonObject.class);
        JsonObject eventPoints = rawDistrictTeam.get("event_points").getAsJsonObject();

        if (mAPIData.hasField(DistrictTeamsTable.EVENT1_KEY)) {
            String event1Key = mAPIData.getEvent1Key();
            if (eventPoints.has(event1Key)) {
                eventBreakdowns.put(event1Key, eventPoints.get(event1Key).getAsJsonObject());
            }
        }

        if (mAPIData.hasField(DistrictTeamsTable.EVENT2_KEY)) {
            String event2Key = mAPIData.getEvent2Key();
            if (eventPoints.has(event2Key)) {
                eventBreakdowns.put(event2Key, eventPoints.get(event2Key).getAsJsonObject());
            }
        }

        if (mAPIData.hasField(DistrictTeamsTable.CMP_KEY)) {
            String cmpKey = mAPIData.getCmpKey();
            if (eventPoints.has(cmpKey)) {
                eventBreakdowns.put(cmpKey, eventPoints.get(cmpKey).getAsJsonObject());
            }
        }

        for (Map.Entry<String, JsonObject> eventData : eventBreakdowns.entrySet()) {
            Event event = mDb.getEventsTable().get(eventData.getKey());
            ListGroup eventGroup = new ListGroup(event == null ?
              eventData.getKey() :
              event.getEventName());

            DistrictPointBreakdown breakdown =
              mGson.fromJson(eventData.getValue(), DistrictPointBreakdown.class);

            if (breakdown.getQualPoints() > -1) {
                eventGroup.children.add(breakdown.renderQualPoints(mResources));
            }
            if (breakdown.getElimPoints() > -1) {
                eventGroup.children.add(breakdown.renderElimPoints(mResources));
            }
            if (breakdown.getAlliancePoints() > -1) {
                eventGroup.children.add(breakdown.renderAlliancePoints(mResources));
            }
            if (breakdown.getAwardPoints() > -1) {
                eventGroup.children.add(breakdown.renderAwardPoints(mResources));
            }
            if (breakdown.getTotalPoints() > -1) {
                eventGroup.children.add(breakdown.renderTotalPoints(mResources));
            }

            mDataToBind.add(eventGroup);
        }
    }
}
