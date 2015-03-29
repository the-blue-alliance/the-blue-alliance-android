package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.District;

import java.lang.reflect.Type;

/**
 * Created by phil on 3/29/15.
 */
public class DistrictDeserializer implements JsonDeserializer<District> {
    @Override
    public District deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final District district= new District();

        if (object.has("name") && !object.get("name").isJsonNull()) {
            district.setName(object.get("name").getAsString());
        }

        if (object.has("key") && !object.get("key").isJsonNull()) {
            String abbrev = object.get("key").getAsString();
            district.setAbbreviation(abbrev);
            district.setEnum(DistrictHelper.DISTRICTS.fromAbbreviation(abbrev).ordinal());
        }

        //TODO need to add year and generate full key

        return district;
    }
}
