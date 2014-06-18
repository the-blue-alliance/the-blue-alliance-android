package com.thebluealliance.androidclient.helpers;

/**
 * File created by phil on 6/15/14.
 */
public class TeamHelper {
    public static boolean validateTeamKey(String key) {
        return !(key == null || key.isEmpty()) && key.matches("^frc\\d{1,4}$");
    }
}
