package com.thebluealliance.androidclient.helpers;


public final class DistrictHelper {

    private DistrictHelper() {
        // unused
    }

    public static boolean validateDistrictKey(String key) {
        if (key == null || key.isEmpty()) return false;
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+$");
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
}
