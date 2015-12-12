package com.thebluealliance.androidclient.subscribers;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.types.DistrictType;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;

import java.util.ArrayList;
import java.util.List;

public class DistrictListSubscriber extends BaseAPISubscriber<List<District>, List<ListItem>> {

    private Database mDb;
    private DistrictRenderer mRenderer;

    public DistrictListSubscriber(Database db, DistrictRenderer renderer) {
        super();
        mDb = db;
        mRenderer = renderer;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }

        for (int i = 0; i < mAPIData.size(); i++) {
            District district = mAPIData.get(i);
            int numEvents = getNumEventsForDistrict(district.getKey());
            district.setNumEvents(numEvents);
            DistrictRenderer.RenderArgs args = new DistrictRenderer.RenderArgs(numEvents, false);
            mDataToBind.add(mRenderer.renderFromModel(district, args));
        }
    }

    private int getNumEventsForDistrict(String districtKey) {
        String[] fields = new String[]{DistrictsTable.KEY};
        String year = districtKey.substring(0, 4);
        int districtEnum = DistrictType.fromAbbreviation(districtKey.substring(4)).ordinal();
        String whereClause = EventsTable.YEAR + " = ? AND " + EventsTable.DISTRICT + " = ?";
        String[] whereArgs = new String[]{year, Integer.toString(districtEnum)};
        Cursor cursor =
                mDb.getEventsTable().query(fields, whereClause, whereArgs, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }
}
