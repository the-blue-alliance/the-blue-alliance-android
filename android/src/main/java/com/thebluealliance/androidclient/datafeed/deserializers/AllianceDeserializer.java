package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.thebluealliance.androidclient.models.EventAlliance;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AllianceDeserializer implements JsonDeserializer<EventAlliance>,
                                             JsonSerializer<EventAlliance> {
    @Override
    public EventAlliance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        EventAlliance alliance = new EventAlliance();

        if (!isNull(data.get("name"))) {
            alliance.setName(data.get("name").getAsString());
        }

        if (!isNull(data.get("event_key"))) {
            alliance.setEventKey(data.get("event_key").getAsString());
        }

        List<String> pickKeys = new ArrayList<>();
        if (!isNull(data.get("picks"))) {
            JsonArray picks = data.get("picks").getAsJsonArray();
            for (int i = 0; i < picks.size(); i++) {
                pickKeys.add(picks.get(i).getAsString());
            }
        }
        alliance.setPicks(pickKeys);

        if (!isNull(data.get("declines"))) {
            JsonArray declines = data.get("declines").getAsJsonArray();
            if (declines.size() > 0) {
                List<String> declineKeys = new ArrayList<>();
                for (int i = 0; i < declines.size(); i++) {
                    declineKeys.add(declines.get(i).getAsString());
                }
                alliance.setDeclines(declineKeys);
            }
        }

        if (!isNull(data.get("backup"))) {
            JsonObject backupJson = data.get("backup").getAsJsonObject();
            EventAlliance.Backup backup = new EventAlliance.Backup();
            backup.setIn(backupJson.get("in").getAsString());
            backup.setOut(backupJson.get("out").getAsString());
            alliance.setBackup(backup);
        }
        return alliance;
    }

    @Override
    public JsonElement serialize(EventAlliance src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject alliance = new JsonObject();

        if (src.getName() != null) {
            alliance.addProperty("name", src.getName());
        }

        if (src.getEventKey() != null) {
            alliance.addProperty("event_key", src.getEventKey());
        }

        JsonArray picks = new JsonArray();
        for (int i = 0; i < src.getPicks().size(); i++) {
            picks.add(src.getPicks().get(i));
        }
        alliance.add("picks", picks);

        if (src.getDeclines() != null) {
            JsonArray declines = new JsonArray();
            for (int i = 0; i < src.getDeclines().size(); i++) {
                declines.add(src.getDeclines().get(i));
            }
            alliance.add("declines", declines);
        }

        if (src.getBackup() != null) {
            JsonObject backup = new JsonObject();
            backup.addProperty("in", src.getBackup().getIn());
            backup.addProperty("out", src.getBackup().getOut());
            alliance.add("backup", backup);
        }
        return alliance;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }
}
