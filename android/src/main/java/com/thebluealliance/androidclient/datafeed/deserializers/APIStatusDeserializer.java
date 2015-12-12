package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.APIStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class APIStatusDeserializer implements JsonDeserializer<APIStatus> {

    public static final String MAX_SEASON_TAG = "max_season";
    public static final String FMS_API_DOWN_TAG = "is_datafeed_down";
    public static final String DOWN_EVENTS_TAG = "down_events";
    public static final String ANDROID_SETTINGS_TAG = "android";
    public static final String MIN_APP_VERSION_TAG = "min_app_version";
    public static final String LATEST_APP_VERSION_TAG = "latest_app_version";

    @Override
    public APIStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Data is not JsonObject");
        }
        JsonObject data = json.getAsJsonObject();
        APIStatus status = new APIStatus();

        JsonElement maxSeason = data.get(MAX_SEASON_TAG);
        if (maxSeason == null || !maxSeason.isJsonPrimitive()) {
            throw new JsonParseException("Max Season is not a primitive");
        }
        status.setMaxSeason(maxSeason.getAsInt());

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
        status.setLatestAppersion(latestAppVersion.getAsInt());

        status.setJsonBlob(json.toString());
        return status;
    }
}
