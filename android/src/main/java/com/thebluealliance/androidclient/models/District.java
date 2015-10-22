package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.listitems.DistrictListElement;

/**
 * Created by phil on 7/23/14.
 */
public class District extends BasicModel<District> {

    public static final String[] NOTIFICATION_TYPES = {
            // NotificationTypes.DISTRICT_POINTS_UPDATED
    };

    private int numEvents;

    public District() {
        super(Database.TABLE_DISTRICTS);
        numEvents = -1;
    }

    public void setKey(String key) {
        fields.put(DistrictsTable.KEY, key);
    }

    public String getKey() {
        if (fields.containsKey(DistrictsTable.KEY) && fields.get(DistrictsTable.KEY) instanceof String) {
            return (String) fields.get(DistrictsTable.KEY);
        } else {
            return "";
        }
    }

    public void setAbbreviation(String abbrev) {
        fields.put(DistrictsTable.ABBREV, abbrev);
    }

    public String getAbbreviation() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictsTable.ABBREV) && fields.get(DistrictsTable.ABBREV) instanceof String) {
            return (String) fields.get(DistrictsTable.ABBREV);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.ABBREV is not defined");
        }
    }

    public void setEnum(int districtEnum) {
        fields.put(DistrictsTable.ENUM, districtEnum);
    }

    public int getEnum() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictsTable.ENUM) && fields.get(DistrictsTable.ENUM) instanceof Integer) {
            return (Integer) fields.get(DistrictsTable.ENUM);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.ENUM is not defined");
        }
    }

    public void setYear(int year) {
        fields.put(DistrictsTable.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictsTable.YEAR) && fields.get(DistrictsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(DistrictsTable.YEAR);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.YEAR is not defined");
        }
    }

    public String getName() throws FieldNotDefinedException {
        if (fields.containsKey(DistrictsTable.NAME) && fields.get(DistrictsTable.NAME) instanceof String) {
            return (String) fields.get(DistrictsTable.NAME);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.NAME is not defined");
        }
    }

    public void setName(String name) {
        fields.put(DistrictsTable.NAME, name);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getDistrictsTable().add(this);
    }

    public void setNumEvents(int events) {
        numEvents = events;
    }

    public int getNumEvents() {
        return numEvents;
    }

    @Override
    public DistrictListElement render() {
        try {
            return new DistrictListElement(this, numEvents);
        } catch (FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to render district");
            e.printStackTrace();
        }
        return null;
    }

}
