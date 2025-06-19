package com.thebluealliance.androidclient.subscribers;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;

import java.util.ArrayList;
import java.util.List;

import thebluealliance.api.model.District;

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
    public void parseData()  {
        mDataToBind.clear();
        for (int i = 0; i < mAPIData.size(); i++) {
            District district = mAPIData.get(i);
            int numEvents = getNumEventsForDistrict(district.getKey());
            DistrictRenderer.RenderArgs args = new DistrictRenderer.RenderArgs(numEvents, false);
            mDataToBind.add(mRenderer.renderFromModel(district, args));
        }
    }

    private int getNumEventsForDistrict(String districtKey) {
        String[] fields = new String[]{DistrictsTable.KEY};
        String year = districtKey.substring(0, 4);
        String whereClause = EventsTable.YEAR + " = ? AND " + EventsTable.DISTRICT_KEY + " = ?";
        String[] whereArgs = new String[]{year, districtKey};
        Cursor cursor =
          mDb.getEventsTable().query(fields, whereClause, whereArgs, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }
}
