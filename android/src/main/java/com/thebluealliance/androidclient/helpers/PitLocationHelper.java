package com.thebluealliance.androidclient.helpers;

import com.google.api.client.json.Json;

import com.thebluealliance.androidclient.R;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulation of special-cased stuff for showing pit locations at 2016 Champs
 */
public class PitLocationHelper {

    private static Map<String, TeamPitLocation> sTeamLocationCache = new HashMap<>();

    public static boolean shouldShowPitLocation(Context context, String teamKey) {
        Date champsStart = new GregorianCalendar(2016, GregorianCalendar.APRIL, 25).getTime();
        Date champsEnd = new GregorianCalendar(2016, GregorianCalendar.MAY, 1).getTime();

        Date now = new Date();
        boolean isChamps = (now.after(champsStart) && now.before(champsEnd));
        if (isChamps) {
            return (getPitLocation(context, teamKey) != null);
        }
        return false;
    }

    public static TeamPitLocation getPitLocation(Context context, String teamKey) {
        // First, check cache
        if (sTeamLocationCache.containsKey(teamKey)) {
            return sTeamLocationCache.get(teamKey);
        }

        // If that fails, try to load it into the cache
        if (readTeamLocationFromJson(context, teamKey) != null) {
            return sTeamLocationCache.get(teamKey);
        }

        // Didn't find anything
        return null;
    }

    /**
     * Finds a specified team in the pit location json file and caches it if found.
     *
     * The file should be in the following format:
     *
     * {"frc111": {"div": "Tesla", "addr": "Q16"}, "frc254": {...}, ...}
     *
     * @param context
     * @param teamKey the team key to search for in the locations dict (frcXXXX)
     * @return the location object if one was found
     */
    private static TeamPitLocation readTeamLocationFromJson(Context context, String teamKey) {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(context.getResources().openRawResource(R.raw.pit_addresses_2016)));
            reader.beginObject();
            while (reader.hasNext()) {
                String currentTeamKey = reader.nextName();
                if (!currentTeamKey.equals(teamKey)) {
                    reader.skipValue();
                } else {
                    // We found the team!
                    reader.beginObject();
                    String location = null, division = null;
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("div")) {
                            division = reader.nextString();
                        } else if (name.equals("addr")) {
                            location = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    if (location != null && division != null) {
                        TeamPitLocation loc = new TeamPitLocation(division, location);
                        sTeamLocationCache.put(currentTeamKey, loc);
                        return loc;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public static class TeamPitLocation {
        private String division, location;

        public TeamPitLocation(String division, String location) {
            this.division = division;
            this.location = location;
        }

        public String getDivision() {
            return division;
        }

        public String getLocation() {
            return location;
        }

        public String getAddressString() {
            return String.format("%s (%s)", location, division);
        }
    }
}
