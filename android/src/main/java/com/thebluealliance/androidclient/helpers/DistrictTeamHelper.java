package com.thebluealliance.androidclient.helpers;

public class DistrictTeamHelper {

    public static boolean validateDistrictTeamKey(String key) {
        if (key == null || key.isEmpty() || !key.contains("_")) {
            return false;
        }
        String districtKey = key.split("_")[0];
        String teamKey = key.split("_")[1];
        return TeamHelper.validateTeamKey(teamKey) && DistrictHelper.validateDistrictKey(districtKey);
    }

    public static String getDistrictKey(String districtTeamKey) {
        return districtTeamKey.split("_")[0];
    }

    public static String getTeamKey(String districtTeamKey) {
        return districtTeamKey.split("_")[1];
    }

    public static String generateKey(String teamKey, String districtKey) {
        return districtKey + "_" + teamKey;
    }
}
