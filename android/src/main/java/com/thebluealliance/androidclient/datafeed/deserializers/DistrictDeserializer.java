package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thebluealliance.androidclient.models.District;

import java.lang.reflect.Type;

public class DistrictDeserializer implements JsonDeserializer<District>, JsonSerializer<District> {
    @Override
    public District deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject data = jsonElement.getAsJsonObject();
        District district = new District();

        district.setKey(data.get("key").getAsString());
        district.setDisplayName(data.get("display_name").getAsString());
        district.setAbbreviation(data.get("abbreviation").getAsString());
        district.setYear(data.get("year").getAsInt());
        return district;
    }


    @Override
    public JsonElement serialize(District src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject district = new JsonObject();
        district.addProperty("key", src.getKey());
        district.addProperty("display_name", src.getDisplayName());
        district.addProperty("abbreviation", src.getAbbreviation());
        district.addProperty("year", src.getYear());
        return district;
    }
}
