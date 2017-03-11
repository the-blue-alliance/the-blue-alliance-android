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
import com.thebluealliance.androidclient.models.EventAlliance.AllianceBackup;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;

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
            alliance.setBackup(context.deserialize(data.get("backup"), AllianceBackup.class));
        }

        if (!isNull(data.get("status"))) {
            alliance.setStatus(context.deserialize(data.get("status"), ITeamAtEventPlayoff.class));
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

        if (src.getStatus() != null) {
            alliance.add("status", context.serialize(src.getStatus(), ITeamAtEventPlayoff.class));
        }
        return alliance;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    public static class AllianceBackupDeserializer implements JsonDeserializer<AllianceBackup>,
                                                              JsonSerializer<AllianceBackup> {

        @Override
        public AllianceBackup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject backupJson = json.getAsJsonObject();
            AllianceBackup backup = new AllianceBackup();
            backup.setIn(backupJson.get("in").getAsString());
            backup.setOut(backupJson.get("out").getAsString());
            return backup;
        }

        @Override
        public JsonElement serialize(AllianceBackup src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject data = new JsonObject();
            data.addProperty("in", src.getIn());
            data.addProperty("out", src.getOut());
            return data;
        }
    }
}
