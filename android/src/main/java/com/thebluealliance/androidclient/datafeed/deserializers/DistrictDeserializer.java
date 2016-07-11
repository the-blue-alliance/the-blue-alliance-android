package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.types.DistrictType;

import java.lang.reflect.Type;

public class DistrictDeserializer implements JsonDeserializer<District> {
    @Override
    public District deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject data = jsonElement.getAsJsonObject();
        District district = new District();
        String key = data.get("key").getAsString();
        district.setAbbreviation(key);
        district.setEnum(DistrictType.fromAbbreviation(key).ordinal());
        district.setName(data.get("name").getAsString());
        return district;
    }
}
