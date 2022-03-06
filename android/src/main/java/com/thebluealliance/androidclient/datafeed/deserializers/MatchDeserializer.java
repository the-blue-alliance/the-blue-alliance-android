package com.thebluealliance.androidclient.datafeed.deserializers;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.MatchAlliancesContainer;

import java.lang.reflect.Type;
import java.util.List;


public class MatchDeserializer implements JsonDeserializer<Match> {

    //used elsewhere, so define as constant
    public static final String ALLIANCE_TAG = "alliances";

    @Override
    public Match deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Match match = new Match();

        if (object.has("key")) {
            String matchKey = object.get("key").getAsString();
            String eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
            match.setKey(matchKey);
            match.setEventKey(eventKey);
        }

        if (object.has("comp_level")) {
            match.setCompLevel(object.get("comp_level").getAsString());
        }

        if (object.has("match_number")) {
            match.setMatchNumber(object.get("match_number").getAsInt());
        }

        if (object.has("set_number")) {
            match.setSetNumber(object.get("set_number").getAsInt());
        }

        if (object.has(ALLIANCE_TAG) && object.get(ALLIANCE_TAG).isJsonObject()) {
            match.setAlliances(context.deserialize(object.get(ALLIANCE_TAG),
                                                   MatchAlliancesContainer.class));
        }

        if (object.has("winning_alliance")) {
            match.setWinningAlliance(object.get("winning_alliance").getAsString());
        }

        if (!isNull(object.get("time"))) {
            match.setTime(object.get("time").getAsLong());
        }

        if (object.has("videos") && object.get("videos").isJsonArray()) {
            match.setVideos(context.deserialize(object.get("videos"), new
                    TypeToken<List<Match.MatchVideo>>(){}.getType()));
        }

        if (object.has("score_breakdown") && object.get("score_breakdown").isJsonObject()) {
            match.setScoreBreakdown(object.get("score_breakdown").toString());
        }

        return match;
    }

}
