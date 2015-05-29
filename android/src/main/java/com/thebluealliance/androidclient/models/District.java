package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.DistrictListElement;

import java.util.ArrayList;
import java.util.Arrays;

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
        fields.put(Database.Districts.KEY, key);
    }

    public String getKey() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.KEY) && fields.get(Database.Districts.KEY) instanceof String) {
            return (String) fields.get(Database.Districts.KEY);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.KEY is not defined");
        }
    }

    public void setAbbreviation(String abbrev) {
        fields.put(Database.Districts.ABBREV, abbrev);
    }

    public String getAbbreviation() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.ABBREV) && fields.get(Database.Districts.ABBREV) instanceof String) {
            return (String) fields.get(Database.Districts.ABBREV);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.ABBREV is not defined");
        }
    }

    public void setEnum(int districtEnum) {
        fields.put(Database.Districts.ENUM, districtEnum);
    }

    public int getEnum() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.ENUM) && fields.get(Database.Districts.ENUM) instanceof Integer) {
            return (Integer) fields.get(Database.Districts.ENUM);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.ENUM is not defined");
        }
    }

    public void setYear(int year) {
        fields.put(Database.Districts.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.YEAR) && fields.get(Database.Districts.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Districts.YEAR);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.YEAR is not defined");
        }
    }

    public String getName() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.NAME) && fields.get(Database.Districts.NAME) instanceof String) {
            return (String) fields.get(Database.Districts.NAME);
        } else {
            throw new FieldNotDefinedException("Field Database.Districts.NAME is not defined");
        }
    }

    public void setName(String name) {
        fields.put(Database.Districts.NAME, name);
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

    // This method will only return a locally stored district
    public static synchronized APIResponse<District> query(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districts table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_DISTRICTS, fields, whereClause, whereArgs, null, null, null, null);
        District district;
        boolean changed = false;
        if (cursor != null && cursor.moveToFirst()) {
            district = ModelInflater.inflateDistrict(cursor);
            cursor.close();
            changed = true;
        } else {
            district = new District();
        }

        /**
         * There is no individual district endpoint yet.
         * So we don't support individual fetching of district data from the web. Sorry.
         */

        if (changed) {
            district.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(district, requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304);
    }

    public static synchronized APIResponse<ArrayList<District>> queryList(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying districts table: " + whereClause + Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_DISTRICTS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<District> districts = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                districts.add(ModelInflater.inflateDistrict(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray districtList = JSONManager.getasJsonArray(response.getData());
                districts = DistrictHelper.buildVersionedDistrictList(districtList, url, response.getVersion());
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            Database.getInstance(c).getDistrictsTable().add(districts);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + districts.size() + " districts, updated in db? " + changed);
        return new APIResponse<>(districts, code);
    }

}
