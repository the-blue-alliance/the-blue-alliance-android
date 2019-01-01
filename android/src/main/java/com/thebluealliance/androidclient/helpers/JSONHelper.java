package com.thebluealliance.androidclient.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

@Deprecated
public final class JSONHelper {
    private static Gson gson;
    private static JsonParser parser;

    private JSONHelper() {
        // unused
    }

    /**
     * Returns true if the given element is null or JsonNull. This is handy for checking the result
     * of {@link JsonObject#get}, which is null if the requested key is absent.
     */
    public static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    @Deprecated
    public static JsonParser getParser() {
        if (parser == null)
            parser = new JsonParser();
        return parser;
    }

    @Deprecated
    public static Gson getGson() {
        if (gson == null) {
            /* Construct new gson with our custom deserializers */
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Award.class, new AwardDeserializer());
            builder.registerTypeAdapter(Event.class, new EventDeserializer());
            builder.registerTypeAdapter(Match.class, new MatchDeserializer());
            builder.registerTypeAdapter(Team.class, new TeamDeserializer());
            builder.registerTypeAdapter(Media.class, new MediaDeserializer());
            builder.registerTypeAdapter(DistrictRanking.class, new DistrictTeamDeserializer());
            gson = builder.create();
        }
        return gson;
    }

    @Deprecated
    public static JsonObject getasJsonObject(String input) {
        if (input == null || input.equals(""))
            return new JsonObject();
        JsonElement e = null;
        try {
            e = getParser().parse(input);
        } catch (JsonSyntaxException ex) {
            TbaLogger.w("Couldn't parse bad json: " + input);
        }
        if (isNull(e)) {
            return new JsonObject();
        }
        try {
            return e.getAsJsonObject();
        } catch (IllegalStateException err) {
            TbaLogger.w("getAsJsonObject failed: " + err);
            return new JsonObject();
        }
    }

    @Deprecated
    public static JsonArray getasJsonArray(String input) {
        if (input == null || input.equals(""))
            return new JsonArray();
        try {
            return getParser().parse(input).getAsJsonArray();
        } catch (IllegalStateException err) {
            TbaLogger.w("getAsJsonArray failed: " + err);
            return new JsonArray();
        } catch (Exception ex) {
            TbaLogger.w("Attempted to parse invalid json");
            ex.printStackTrace();
            return new JsonArray();
        }

    }
}
