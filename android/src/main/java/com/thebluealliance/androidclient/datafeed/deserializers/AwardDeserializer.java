package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import com.thebluealliance.androidclient.helpers.AwardHelper;
import com.thebluealliance.androidclient.models.Award;

import java.lang.reflect.Type;
import java.util.List;


public class AwardDeserializer implements JsonDeserializer<Award> {

    @Override
    public Award deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject a = json.getAsJsonObject();
        final Award award = new Award();

        if (a.has("event_key") && a.has("award_type")) {
            int awardEnum = a.get("award_type").getAsInt();
            String eventKey = a.get("event_key").getAsString();

            award.setKey(AwardHelper.createAwardKey(eventKey, awardEnum));
            award.setEnum(awardEnum);
            award.setEventKey(eventKey);
        } else {

            if (a.has("award_type")) {
                award.setEnum(a.get("award_type").getAsInt());
            }

            if (a.has("event_key")) {
                award.setEventKey(a.get("event_key").getAsString());
            }
        }

        if (a.has("name")) {
            award.setName(a.get("name").getAsString());
        }

        if (a.has("year")) {
            award.setYear(a.get("year").getAsInt());
        }

        if (a.has("recipient_list")) {
            award.setRecipientList(context.deserialize(a.get("recipient_list"), new TypeToken<List<Award.AwardRecipient>>(){}.getType()));
        }

        return award;
    }

    public static class AwardRecipientDeserializer implements JsonDeserializer<Award.AwardRecipient>,
                                                              JsonSerializer<Award.AwardRecipient> {

        @Override
        public Award.AwardRecipient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Award.AwardRecipient recipient = new Award.AwardRecipient();
            JsonObject data = json.getAsJsonObject();

            if (data.has("awardee") && !data.get("awardee").isJsonNull()) {
                recipient.setAwardee(data.get("awardee").getAsString());
            }

            if (data.has("team_key") && !data.get("team_key").isJsonNull()) {
                recipient.setTeamKey(data.get("team_key").getAsString());
            }

            return recipient;
        }

        @Override
        public JsonElement serialize(Award.AwardRecipient src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject recipient = new JsonObject();
            recipient.addProperty("awardee", src.getAwardee());
            recipient.addProperty("team_key", src.getTeamKey());
            return recipient;
        }
    }
}
