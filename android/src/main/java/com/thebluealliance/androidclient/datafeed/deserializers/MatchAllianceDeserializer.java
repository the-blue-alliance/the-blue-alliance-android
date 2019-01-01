package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.models.MatchAlliancesContainer;

import java.lang.reflect.Type;
import java.util.List;

public class MatchAllianceDeserializer implements JsonDeserializer<MatchAlliancesContainer>,
                                                  JsonSerializer<MatchAlliancesContainer> {
    @Override
    public MatchAlliancesContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MatchAlliancesContainer alliances = new MatchAlliancesContainer();
        JsonObject data = json.getAsJsonObject();

        if (!isNull(data.get("red"))) {
            JsonObject redAlliance = data.getAsJsonObject("red");
            MatchAlliancesContainer.MatchAlliance red = new MatchAlliancesContainer.MatchAlliance();
            red.setScore(redAlliance.get("score").getAsInt());
            red.setTeamKeys(context.deserialize(redAlliance.get("team_keys"), new TypeToken<List<String>>(){}.getType()));
            if (red.getTeamKeys() == null || red.getTeamKeys().isEmpty()) {
                // Fall back to apiv2 format so push notifications don't break
                red.setTeamKeys(context.deserialize(redAlliance.get("teams"), new TypeToken<List<String>>(){}.getType()));
            }
            if (!isNull(data.get("surrogate_team_keys"))) {
                red.setSurrogateTeamKeys(context.deserialize(redAlliance.get("surrogate_team_keys"), new TypeToken<List<String>>(){}.getType()));
            }
            alliances.setRed(red);
        }

        if (!isNull(data.get("blue"))) {
            JsonObject blueAlliance = data.getAsJsonObject("blue");
            MatchAlliancesContainer.MatchAlliance blue = new MatchAlliancesContainer.MatchAlliance();
            blue.setScore(blueAlliance.get("score").getAsInt());
            blue.setTeamKeys(context.deserialize(blueAlliance.get("team_keys"), new TypeToken<List<String>>(){}.getType()));
            if (blue.getTeamKeys() == null || blue.getTeamKeys().isEmpty()) {
                // Fall back to apiv2 format so push notifications don't break
                blue.setTeamKeys(context.deserialize(blueAlliance.get("teams"), new TypeToken<List<String>>(){}.getType()));
            }
            if (!isNull(data.get("surrogate_team_keys"))) {
                blue.setSurrogateTeamKeys(context.deserialize(blueAlliance.get("surrogate_team_keys"), new TypeToken<List<String>>(){}.getType()));
            }
            alliances.setBlue(blue);
        }

        return alliances;
    }

    @Override
    public JsonElement serialize(MatchAlliancesContainer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();

        if (src.getRed() != null) {
            JsonObject red = new JsonObject();
            red.addProperty("score", src.getRed().getScore());
            red.add("team_keys", context.serialize(src.getRed().getTeamKeys(), new TypeToken<List<String>>(){}.getType()));
            if (src.getRed().getSurrogateTeamKeys() != null) {
                red.add("surrogate_team_keys", context.serialize(src.getRed().getSurrogateTeamKeys(), new TypeToken<List<String>>(){}.getType()));
            }
            data.add("red", red);
        }

        if (src.getBlue() != null) {
            JsonObject blue = new JsonObject();
            blue.addProperty("score", src.getBlue().getScore());
            blue.add("team_keys", context.serialize(src.getBlue().getTeamKeys(), new TypeToken<List<String>>(){}.getType()));
            if (src.getBlue().getSurrogateTeamKeys() != null) {
                blue.add("surrogate_team_keys", context.serialize(src.getBlue().getSurrogateTeamKeys(), new TypeToken<List<String>>(){}.getType()));
            }
            data.add("blue", blue);
        }
        return data;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }
}
