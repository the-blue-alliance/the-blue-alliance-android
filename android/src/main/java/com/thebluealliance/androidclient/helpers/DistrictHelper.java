package com.thebluealliance.androidclient.helpers;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.District;

import java.util.ArrayList;

public class DistrictHelper {

    public static boolean validateDistrictKey(String key) {
        if (key == null || key.length() <= 4) {
            return false;
        }
        try {
            int year = Integer.parseInt(key.substring(0, 4));
            String districtAbbrev = key.substring(4);
            return DistrictType.fromAbbreviation(districtAbbrev) != DistrictType.NO_DISTRICT;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int extractYearFromKey(String key) {
        return Integer.parseInt(key.substring(0, 4));
    }

    public static String extractAbbrevFromKey(String key) {
        return key.substring(4);
    }

    public static String generateKey(String districtAbbrev, int year) {
        return year + districtAbbrev;
    }

    public static DistrictType districtTypeFromKey(String districtKey) {
        String districtAbbrev = districtKey.substring(4);
        return DistrictType.fromAbbreviation(districtAbbrev);
    }

    public static District buildDistrictFromUrl(String districtKey, String url) {
        int year = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1));
        Log.d(Constants.LOG_TAG, "Creating district for " + year);
        District out = new District();
        out.setKey(year + districtKey);
        out.setYear(year);
        out.setAbbreviation(districtKey);
        out.setEnum(DistrictType.fromAbbreviation(districtKey).ordinal());
        return out;
    }

    public static ArrayList<District> buildVersionedDistrictList(JsonArray districtList, String url, int version) {
        ArrayList<District> districts = new ArrayList<>();
        for (JsonElement d : districtList) {
            if (!d.isJsonNull()) {
                if (version > 1) {
                    JsonObject data = d.getAsJsonObject();
                    District district = DistrictHelper.buildDistrictFromUrl(data.get("key").getAsString(), url);
                    district.setName(data.get("name").getAsString());
                    districts.add(district);
                } else {
                    districts.add(DistrictHelper.buildDistrictFromUrl(d.getAsString(), url));
                }
            }
        }
        return districts;
    }

    public static JsonObject findPointsForTeam(JsonObject points, String teamKey) {
        if (points.has("points")) {
            JsonObject pointsObject = points.get("points").getAsJsonObject();
            if (pointsObject.has(teamKey)) {
                return pointsObject.get(teamKey).getAsJsonObject();
            }
        }
        return new JsonObject();
    }

}
