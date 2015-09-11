package com.thebluealliance.androidclient.helpers;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.District;

import java.util.ArrayList;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictHelper {

    /* DO NOT CHANGE ORDER. */
    public enum DISTRICTS {
        NO_DISTRICT,
        MICHIGAN,
        MID_ATLANTIC,
        NEW_ENGLAND,
        PACIFIC_NORTHWEST,
        INDIANA,
        CHESAPEAKE,
        NORTH_CAROLINA,
        GEORGIA;

        public static DISTRICTS fromEnum(int districtEnum) {
            switch (districtEnum) {
                default:
                case 0: return NO_DISTRICT;
                case 1: return MICHIGAN;
                case 2: return MID_ATLANTIC;
                case 3: return NEW_ENGLAND;
                case 4: return PACIFIC_NORTHWEST;
                case 5: return INDIANA;
                case 6: return CHESAPEAKE;
                case 7: return NORTH_CAROLINA;
                case 8: return GEORGIA;
            }
        }

        public static DISTRICTS fromAbbreviation(String abbrev) {
            switch (abbrev) {
                case "fim": return MICHIGAN;
                case "mar": return MID_ATLANTIC;
                case "ne":  return NEW_ENGLAND;
                case "pnw": return PACIFIC_NORTHWEST;
                case "in":  return INDIANA;
                case "chs": return CHESAPEAKE;
                case "nc":  return NORTH_CAROLINA;
                case "pch": return GEORGIA;
                default:
                    return NO_DISTRICT;
            }
        }

        public String getName() {
            switch (this) {
                default:
                case NO_DISTRICT:
                    return "";
                case MICHIGAN:
                    return "Michigan";
                case MID_ATLANTIC:
                    return "Mid Atlantic";
                case NEW_ENGLAND:
                    return "New England";
                case PACIFIC_NORTHWEST:
                    return "Pacific Northwest";
                case INDIANA:
                    return "Indiana";
                case CHESAPEAKE:
                    return "Chesapeake";
                case NORTH_CAROLINA:
                    return "North Carolina";
                case GEORGIA:
                    return "Georgia";
            }
        }

        public String getAbbreviation() {
            switch (this) {
                default:
                case NO_DISTRICT:
                    return "";
                case MICHIGAN:
                    return "fim";
                case MID_ATLANTIC:
                    return "mar";
                case NEW_ENGLAND:
                    return "ne";
                case PACIFIC_NORTHWEST:
                    return "pnw";
                case INDIANA:
                    return "in";
                case CHESAPEAKE:
                    return "chs";
                case NORTH_CAROLINA:
                    return "nc";
                case GEORGIA:
                    return "pch";
            }
        }

    }

    public static boolean validateDistrictKey(String key) {
        if (key == null || key.length() <= 4) {
            return false;
        }
        try {
            int year = Integer.parseInt(key.substring(0, 4));
            String districtAbbrev = key.substring(4);
            return DISTRICTS.fromAbbreviation(districtAbbrev) != DISTRICTS.NO_DISTRICT;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String generateKey(String districtAbbrev, int year) {
        return year + districtAbbrev;
    }

    public static DISTRICTS districtTypeFromKey(String districtKey) {
        String districtAbbrev = districtKey.substring(4);
        return DISTRICTS.fromAbbreviation(districtAbbrev);
    }

    public static District buildDistrictFromUrl(String districtKey, String url) {
        int year = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1));
        Log.d(Constants.LOG_TAG, "Creating district for " + year);
        District out = new District();
        out.setKey(year + districtKey);
        out.setYear(year);
        out.setAbbreviation(districtKey);
        out.setEnum(DISTRICTS.fromAbbreviation(districtKey).ordinal());
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
