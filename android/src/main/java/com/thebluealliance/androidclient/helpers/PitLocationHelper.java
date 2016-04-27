package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.APIStatus;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulation of special-cased stuff for showing pit locations at 2016 Champs
 */
public class PitLocationHelper {

    private static final String LAST_UPDATED_PREF_KEY = "cmp_pit_locations_update_time";
    private static final String PIT_LOCATIONS_FILENAME = "2016_pit_locations.json";

    private static Map<String, TeamPitLocation> sTeamLocationCache = new HashMap<>();


    // TODO improve in the future; perhaps read dates/events from the Status API?
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

    public static void updateFromRemoteUrl(Context context, String newContent, long updateTimeSeconds) {
        // Write to the file
        try {
            File locationFile = getLocationsFile(context);
            if (!locationFile.exists()) {
                locationFile.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(locationFile));
            writer.write(newContent);
            writer.flush();
            writer.close();

            // Note the update time, but only if it was successful
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATED_PREF_KEY, updateTimeSeconds).commit();

            // Wipe the in-memory cache of teams so that they are lazily re-loaded
            sTeamLocationCache.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldUpdateFromRemoteUrl(Context context, APIStatus status) {
        long lastUpdateTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_PREF_KEY, -1);
        long remoteUpdateTime = status.getChampsPitLocationsUpdateTime();
        return (lastUpdateTime == -1 || remoteUpdateTime > lastUpdateTime);
    }

    private static void populateLocationsFileFromPackagedResourceIfNeeded(Context context) {
        File locationsFile = getLocationsFile(context);
        if (locationsFile.exists()) {
            // Don't overwrite!
            return;
        }

        InputStream input = null;
        OutputStream output = null;
        try {
            locationsFile.createNewFile();
            input = context.getResources().openRawResource(R.raw.pit_addresses_2016);
            output = new FileOutputStream(locationsFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Finds a specified team in the pit location json file and caches it if found.
     *
     * The file should be in the following format:
     *
     * {"frc111": {"div": "Tesla", "addr": "Q16"}, "frc254": {...}, ...}
     *
     * @param teamKey the team key to search for in the locations dict (frcXXXX)
     * @return the location object if one was found
     */
    private static TeamPitLocation readTeamLocationFromJson(Context context, String teamKey) {
        // Load the resource into our file if we haven't done so yet
        populateLocationsFileFromPackagedResourceIfNeeded(context);

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(getLocationsFile(context)));
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

    private static File getLocationsFile(Context context) {
        return new File(context.getFilesDir().getPath() + File.pathSeparator + PIT_LOCATIONS_FILENAME);
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
