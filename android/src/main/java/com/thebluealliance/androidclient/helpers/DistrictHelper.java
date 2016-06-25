package com.thebluealliance.androidclient.helpers;


import com.thebluealliance.androidclient.types.DistrictType;

public final class DistrictHelper {

    private DistrictHelper() {
        // unused
    }

    public static boolean validateDistrictKey(String key) {
        if (key == null || key.length() <= 4) {
            return false;
        }
        try {
            Integer.parseInt(key.substring(0, 4));
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
        String districtAbbrev = extractAbbrevFromKey(districtKey);
        return DistrictType.fromAbbreviation(districtAbbrev);
    }
}
