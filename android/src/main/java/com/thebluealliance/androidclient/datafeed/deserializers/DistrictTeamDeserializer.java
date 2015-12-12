package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.DistrictTeam;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictTeamDeserializer implements JsonDeserializer<DistrictTeam> {
    @Override
    public DistrictTeam deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final DistrictTeam districtTeam = new DistrictTeam();


        if (!isNull(object.get("team_key"))) {
            districtTeam.setTeamKey(object.get("team_key").getAsString());
        }

        if (!isNull(object.get("rank"))) {
            districtTeam.setRank(object.get("rank").getAsInt());
        }

        if (!isNull(object.get("rookie_bonus"))) {
            districtTeam.setRookiePoints(object.get("rookie_bonus").getAsInt());
        }

        if (!isNull(object.get("point_total"))) {
            districtTeam.setTotalPoints(object.get("point_total").getAsInt());
        }

        if (!isNull(object.get("event_points"))) {
            Set<Map.Entry<String, JsonElement>> events = object.get("event_points").getAsJsonObject().entrySet();
            int regularEvents = 0;
            for (Map.Entry<String, JsonElement> e : events) {
                JsonObject event = e.getValue().getAsJsonObject();
                if (event.get("district_cmp").getAsBoolean()) {
                    districtTeam.setCmpKey(e.getKey());
                    districtTeam.setCmpPoints(event.get("total").getAsInt());
                } else if (regularEvents == 0) {
                    districtTeam.setEvent1Key(e.getKey());
                    districtTeam.setEvent1Points(event.get("total").getAsInt());
                    regularEvents++;
                } else if (regularEvents == 1) {
                    districtTeam.setEvent2Key(e.getKey());
                    districtTeam.setEvent2Points(event.get("total").getAsInt());
                    regularEvents++;
                }
            }
        }

        districtTeam.setJson(object.toString());

        return districtTeam;
    }
}
