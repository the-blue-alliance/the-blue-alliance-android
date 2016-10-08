package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import com.thebluealliance.androidclient.models.ApiStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class APIStatusDeserializer implements JsonDeserializer<ApiStatus> {

    private static final int MS_PER_SECOND = 1000;

    static final String MAX_SEASON_TAG = "max_season";
    static final String CURRENT_SEASON_TAG = "current_season";
    static final String FMS_API_DOWN_TAG = "is_datafeed_down";
    static final String DOWN_EVENTS_TAG = "down_events";
    static final String ANDROID_SETTINGS_TAG = "android";
    static final String MIN_APP_VERSION_TAG = "min_app_version";
    static final String LATEST_APP_VERSION_TAG = "latest_app_version";
    static final String MESSAGE_DICT = "message";
    static final String MESSAGE_TEXT = "text";
    static final String MESSAGE_EXPIRATION = "expiration";
    static final String LAST_OKHTTP_CACHE_CLEAR = "last_cache_clear";
    static final String CHAMPS_PIT_LOCATIONS_URL = "cmp_pit_locations_url";
    static final String CHAMPS_PIT_LOCATIONS_UPDATE_TIME = "cmp_pit_locations_update_time";

    @Override
    public ApiStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Data is not JsonObject");
        }
        JsonObject data = json.getAsJsonObject();
        ApiStatus status = new ApiStatus();

        JsonElement maxSeason = data.get(MAX_SEASON_TAG);
        if (maxSeason == null || !maxSeason.isJsonPrimitive()) {
            throw new JsonParseException("Max Season is not a primitive");
        }
        status.setMaxSeason(maxSeason.getAsInt());

        JsonElement currentSeason = data.get(CURRENT_SEASON_TAG);
        if (currentSeason == null || !currentSeason.isJsonPrimitive()) {
            // Default to the current year if not set
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            status.setCurrentSeason(currentYear);
        } else {
            status.setCurrentSeason(currentSeason.getAsInt());
        }

        // Ensure that maxSeason always is max(current, given max)
        int maxSeasonCheck = Math.max(status.getMaxSeason(), status.getCurrentSeason());
        status.setMaxSeason(maxSeasonCheck);

        JsonElement fmsApiDown = data.get(FMS_API_DOWN_TAG);
        if (fmsApiDown == null || !fmsApiDown.isJsonPrimitive()) {
            throw new JsonParseException("Is Datafeed Down is not a primitive");
        }
        status.setFmsApiDown(fmsApiDown.getAsBoolean());

        JsonElement downEvents = data.get(DOWN_EVENTS_TAG);
        if (downEvents == null || !downEvents.isJsonArray()) {
            throw new JsonParseException("Down Events is not an array");
        }
        List<String> downKeys = new ArrayList<>();
        for (JsonElement eventKey : downEvents.getAsJsonArray()) {
            if (!eventKey.isJsonPrimitive()) {
                continue;
            }
            downKeys.add(eventKey.getAsString());
        }
        status.setDownEvents(downKeys);

        JsonElement androidSpecific = data.get(ANDROID_SETTINGS_TAG);
        if (androidSpecific == null || !androidSpecific.isJsonObject()) {
            throw new JsonParseException("No Android specific settings");
        }
        JsonObject androidSettings = androidSpecific.getAsJsonObject();

        JsonElement minAppVersion = androidSettings.get(MIN_APP_VERSION_TAG);
        if (minAppVersion == null || !minAppVersion.isJsonPrimitive()) {
            throw new JsonParseException("Min App Version not found");
        }
        status.setMinAppVersion(minAppVersion.getAsInt());

        JsonElement latestAppVersion = androidSettings.get(LATEST_APP_VERSION_TAG);
        if (latestAppVersion == null || !latestAppVersion.isJsonPrimitive()) {
            throw new JsonParseException("Latest app version not found");
        }
        status.setLatestAppVersion(latestAppVersion.getAsInt());

        JsonElement message = data.get(MESSAGE_DICT);
        if (message != null && !message.isJsonNull() && message.isJsonObject()) {
            JsonObject adminMessage = data.get(MESSAGE_DICT).getAsJsonObject();
            JsonElement messageText = adminMessage.get(MESSAGE_TEXT);
            JsonElement messageExpiration = adminMessage.get(MESSAGE_EXPIRATION);
            if (messageText == null || messageText.isJsonNull()
                    || messageExpiration == null || messageExpiration.isJsonNull()) {
                throw new JsonParseException("Message requires text and expiration");
            }
            status.setHasMessage(true);
            status.setMessageText(messageText.getAsString());
            status.setMessageExpiration(new Date(messageExpiration.getAsLong() * MS_PER_SECOND)
                                                .getTime());
        } else {
            status.setHasMessage(false);
        }

        JsonElement lastCacheClear = data.get(LAST_OKHTTP_CACHE_CLEAR);
        if (lastCacheClear != null && !lastCacheClear.isJsonNull() && lastCacheClear.isJsonPrimitive()) {
            JsonPrimitive lastCacheTime = lastCacheClear.getAsJsonPrimitive();
            long lastTimestamp = lastCacheTime.getAsLong();
            status.setLastOkHttpCacheClear(lastTimestamp);
        } else {
            status.setLastOkHttpCacheClear((long) -1);
        }

        JsonElement champsPitLocationsUrl = data.get(CHAMPS_PIT_LOCATIONS_URL);
        if (champsPitLocationsUrl != null && !champsPitLocationsUrl.isJsonNull()) {
            status.setChampsPitLocationsUrl(champsPitLocationsUrl.getAsString());
        } else {
            status.setChampsPitLocationsUrl(null);
        }

        JsonElement champsPitLocationsUpdateTime = data.get(CHAMPS_PIT_LOCATIONS_UPDATE_TIME);
        if (champsPitLocationsUpdateTime != null && !champsPitLocationsUpdateTime.isJsonNull()) {
            status.setChampsPitLocationsUpdateTime(champsPitLocationsUpdateTime.getAsLong());
        } else {
            status.setChampsPitLocationsUpdateTime((long)-1);
        }

        status.setJsonBlob(json.toString());
        return status;
    }
}
