package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamAtDistrictBreakdownSubscriber
  extends BaseAPISubscriber<DistrictTeam, List<ListGroup>> {

    private Context mContext;
    private Database mDb;
    private Gson mGson;

    public TeamAtDistrictBreakdownSubscriber(Context context, Database db, Gson gson) {
        super();
        mContext = context;
        mDb = db;
        mGson = gson;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public synchronized void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }
        Map<String, JsonObject> eventBreakdowns =
          Utilities.getMapForPlatform(String.class, JsonObject.class);
        JsonObject rawDistrictTeam = mGson.fromJson(mAPIData.getJson(), JsonObject.class);
        eventBreakdowns.put(
          mAPIData.getEvent1Key(),
          rawDistrictTeam.get("event_points").getAsJsonObject()
            .get(mAPIData.getEvent1Key()).getAsJsonObject());
        eventBreakdowns.put(
          mAPIData.getEvent2Key(),
          rawDistrictTeam.get("event_points").getAsJsonObject()
            .get(mAPIData.getEvent2Key()).getAsJsonObject());
        eventBreakdowns.put(
          mAPIData.getCmpKey(),
          rawDistrictTeam.get("event_points").getAsJsonObject()
            .get(mAPIData.getCmpKey()).getAsJsonObject());

        for (Map.Entry<String, JsonObject> eventData : eventBreakdowns.entrySet()) {
            Event event = mDb.getEventsTable().get(eventData.getKey());
            ListGroup eventGroup = new ListGroup(event.getEventName());

            DistrictPointBreakdown breakdown =
              mGson.fromJson(eventData.getValue(), DistrictPointBreakdown.class);

            if (breakdown.getQualPoints() > -1) {
                eventGroup.children.add(breakdown.renderQualPoints(mContext));
            }
            if (breakdown.getElimPoints() > -1) {
                eventGroup.children.add(breakdown.renderElimPoints(mContext));
            }
            if (breakdown.getAlliancePoints() > -1) {
                eventGroup.children.add(breakdown.renderAlliancePoints(mContext));
            }
            if (breakdown.getAwardPoints() > -1) {
                eventGroup.children.add(breakdown.renderAwardPoints(mContext));
            }
            if (breakdown.getTotalPoints() > -1) {
                eventGroup.children.add(breakdown.renderTotalPoints(mContext));
            }

            mDataToBind.add(eventGroup);
        }
    }
}
