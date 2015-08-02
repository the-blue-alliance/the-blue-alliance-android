package com.thebluealliance.androidclient.subscribers;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

import java.util.ArrayList;
import java.util.List;

public class DistrictListSubscriber extends BaseAPISubscriber<List<District>, List<ListItem>> {

    private Database mDb;

    public DistrictListSubscriber(Database db) {
        super();
        mDb = db;
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
            mDataToBind.add(district.render());
        }
    }

    private int getNumEventsForDistrict(String districtKey) {
        String[] fields = new String[]{Database.Districts.KEY};
        String year = districtKey.substring(0, 4);
        int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtKey.substring(4)).ordinal();
        String whereClause = Database.Events.YEAR + " = ? AND " + Database.Events.DISTRICT + " = ?";
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
