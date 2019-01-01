package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.api.model.IDistrictEventPoints;

import java.util.ArrayList;
import java.util.List;

public class TeamAtDistrictBreakdownSubscriber
  extends BaseAPISubscriber<DistrictRanking, List<ListGroup>> {

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
    public synchronized void parseData()  {
        mDataToBind.clear();
        List<IDistrictEventPoints> eventBreakdowns = mAPIData.getEventPoints();
        if (eventBreakdowns == null) {
            return;
        }
        for (IDistrictEventPoints eventData : eventBreakdowns) {
            Event event = mDb.getEventsTable().get(eventData.getEventKey());
            DistrictPointBreakdown breakdown = (DistrictPointBreakdown) eventData;
            ListGroup eventGroup = new ListGroup(event == null
              ? eventData.getEventKey()
              : event.getName());

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
            if (breakdown.getTotal() > -1) {
                eventGroup.children.add(breakdown.renderTotalPoints(mResources));
            }

            mDataToBind.add(eventGroup);
        }
    }
}
