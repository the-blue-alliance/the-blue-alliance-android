package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.config.AppConfig;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Encapsulation of special-cased stuff for showing pit locations at 2016 Champs
 */
public final class PitLocationHelper {

    private PitLocationHelper() {
        // unused
    }

    private static final String LAST_UPDATED_PREF_KEY = "cmp_pit_locations_update_time";
    private static final String PIT_LOCATIONS_FILENAME = "2016_pit_locations.json";

    private static final String SHOW_PIT_KEY = "show_pit_location_for_team";
    private static final String SHOW_PIT_EVENTS = "show_pit_location_events";
    private static final String PIT_URL_KEY = "champs_pit_url";
    private static final String PIT_LAST_UPDATE_KEY = "champs_pit_last_update";

    private static Map<String, TeamPitLocation> sTeamLocationCache = new HashMap<>();

    public static boolean shouldShowPitLocation(AppConfig config) {
        return config.getBoolean(SHOW_PIT_KEY);
    }

    public static boolean shouldShowPitLocationAtEvent(AppConfig config, String eventKey) {
        String eventKeys = config.getString(SHOW_PIT_EVENTS);
        return shouldShowPitLocation(config)
                && !TextUtils.isEmpty(eventKeys)
                && eventKeys.contains(eventKey);
    }

    public static @Nullable TeamPitLocation getPitLocation(Context context, String teamKey) {
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

    public static boolean shouldUpdateFromRemoteUrl(Context context, AppConfig config) {
        long lastUpdateTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_PREF_KEY, -1);
        long remoteUpdateTime = config.getLong(PIT_LAST_UPDATE_KEY, 0);
        String url = config.getString(PIT_URL_KEY);
        // TODO better URL validation
        boolean validUrl = !TextUtils.isEmpty(url)
                && (url.startsWith("http://")
                || url.startsWith("https://"));
        return shouldShowPitLocation(config)
                && validUrl
                && (lastUpdateTime == -1 || remoteUpdateTime > lastUpdateTime);
    }

    public static void updateRemoteDataIfNeeded(Context context,
                                                AppConfig appConfig,
                                                OkHttpClient httpClient) {
        String remoteUrl = appConfig.getString(PIT_URL_KEY);
        long lastUpdateTime = appConfig.getLong(PIT_LAST_UPDATE_KEY, 0);
        if (PitLocationHelper.shouldUpdateFromRemoteUrl(context.getApplicationContext(), appConfig)
                && !TextUtils.isEmpty(remoteUrl)) {
            try {
                Request request = new Request.Builder()
                        .url(remoteUrl)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();

                okhttp3.Response champsPitLocation = httpClient.newCall(request).execute();
                String responseString = champsPitLocation.body().string();
                PitLocationHelper.updateFromRemoteUrl(context.getApplicationContext(),
                                                      responseString,
                                                      lastUpdateTime);
            } catch (Exception e) {
                TbaLogger.w("Unable to update champs pit locations", e);
            }
        }
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
     * {"locations": { "frc111": {"div": "Tesla", "addr": "Q16"}, "frc254": {...}, ...} }
     *
     * In the future, the root object may contain additional properties.
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
                String section = reader.nextName();
                if (!section.equals("locations")) {
                    reader.skipValue();
                    continue;
                }
                reader.beginObject();
                while(reader.hasNext()) {
                    String currentTeamKey = reader.nextName();
                    TbaLogger.d("reading team: " + currentTeamKey);
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
                            } else if (name.equals("loc")) {
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
                reader.endObject();
            }
            reader.endObject();
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
