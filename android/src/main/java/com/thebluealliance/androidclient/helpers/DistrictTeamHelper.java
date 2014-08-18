package com.thebluealliance.androidclient.helpers;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictTeamHelper {

    public static boolean validateDistrictTeamKey(String key) {
        if(key == null || key.isEmpty() || !key.contains("_")){
            return false;
        }
        String districtKey = key.split("_")[0];
        String teamKey = key.split("_")[1];
        return TeamHelper.validateTeamKey(teamKey) && DistrictHelper.validateDistrictKey(districtKey);
    }

    public static String getDistrictKey(String districtTeamKey) {
        return districtTeamKey.split("_")[0];
    }

    public static String getTeamKey(String districtTeamKey){
        return districtTeamKey.split("_")[1];
    }

    public static String generateKey(String teamKey, String districtKey) {
        return districtKey + "_" + teamKey;
    }

    public static void addFieldsFromKey(DistrictTeam districtTeam, String key) {
        String districtKey = key.split("_")[0];
        String dtKey;
        try {
            dtKey = DistrictTeamHelper.generateKey(districtTeam.getTeamKey(), districtKey);
            districtTeam.setKey(dtKey);
            districtTeam.setDistrictEnum(DistrictHelper.districtTypeFromKey(districtKey).ordinal());
            districtTeam.setYear(Integer.parseInt(districtKey.substring(0, 4)));
            districtTeam.setDistrictKey(districtKey);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to add fields from " + key + ". DistrictTeam missing fields");
        }

    }

    public static void addFieldsFromAPIUrl(DistrictTeam districtTeam, String teamKey, String url) {
        // http://www.thebluealliance.com/api/v2/district/ne/2014/rankings
        //   0  1            2             3  4     5      6  7     8

        String[] split = url.split("/");
        String districtKey = DistrictHelper.generateKey(split[6], Integer.parseInt(split[7]));
        String dtKey = DistrictTeamHelper.generateKey(teamKey, districtKey);
        districtTeam.setKey(dtKey);
        districtTeam.setDistrictEnum(DistrictHelper.districtTypeFromKey(districtKey).ordinal());
        districtTeam.setTeamKey(teamKey);
        districtTeam.setYear(Integer.parseInt(districtKey.substring(0, 4)));
        districtTeam.setDistrictKey(districtKey);
    }

}
