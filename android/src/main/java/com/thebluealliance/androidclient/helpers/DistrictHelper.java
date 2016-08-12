package com.thebluealliance.androidclient.helpers;


import com.thebluealliance.androidclient.types.DistrictType;

public class DistrictHelper {

    public static boolean validateDistrictKey(String key) {
        // District keys have the same format as event keys
        // We don't want to be too strict here, so we don't get issues when we districts are added
        return EventHelper.validateEventKey(key);
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
        String districtAbbrev = extractAbbrevFromKey(districtKey);
        return DistrictType.fromAbbreviation(districtAbbrev);
    }
}
