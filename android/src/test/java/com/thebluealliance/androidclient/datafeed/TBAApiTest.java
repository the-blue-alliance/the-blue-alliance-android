package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchType;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * File created by phil on 5/8/14.
 */
@RunWith(RobolectricTestRunner.class)
public class TBAApiTest {

    @org.junit.Test
    public void testParseEvent() {
        String eventJson = "{\"key\": \"2014ctgro\", \"end_date\": \"2014-03-09\", \"name\": \"Groton District Event\", \"short_name\": \"Groton\", \"facebook_eid\": null, \"official\": true, \"location\": \"Groton, CT, USA\", \"event_code\": \"ctgro\", \"year\": 2014, \"event_type_string\": \"District\", \"start_date\": \"2014-03-08\", \"event_type\": 1}";
        Event event = JSONHelper.getGson().fromJson(eventJson, Event.class);

        //now, assert that all the properties are there
        try {
            assertEquals(event.getKey(), "2014ctgro");
            assertEquals(event.getStartDate(), new Date(114, 2, 8));
            assertEquals(event.getEndDate(), new Date(114, 2, 9));
            assertEquals(event.getEventName(), "Groton District Event");
            assertEquals(event.getEventShortName(), "Groton");
            assertEquals(event.isOfficial(), true);
            assertEquals(event.getLocation(), "Groton, CT, USA");
            assertEquals(event.getEventType(), EventHelper.TYPE.DISTRICT);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get event fields");
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testParseTeam() {
        String teamJson = "{\n" +
                "  \"website\": \"http://www.uberbots.org\",\n" +
                "  \"name\": \"UTC Fire and Security & Avon High School\",\n" +
                "  \"locality\": \"Avon\",\n" +
                "  \"rookie_year\": 2003,\n" +
                "  \"region\": \"CT\",\n" +
                "  \"team_number\": 1124,\n" +
                "  \"location\": \"Avon, CT, USA\",\n" +
                "  \"key\": \"frc1124\",\n" +
                "  \"country_name\": \"USA\",\n" +
                "  \"nickname\": \"ÜberBots\"\n" +
                "}";
        Team team = JSONHelper.getGson().fromJson(teamJson, Team.class);

        try {
            assertEquals(team.getWebsite(), "http://www.uberbots.org");
            assertEquals(team.getFullName(), "UTC Fire and Security & Avon High School");
            assertEquals(team.getLocation(), "Avon, CT, USA");
            assertEquals((int) team.getTeamNumber(), 1124);
            assertEquals(team.getKey(), "frc1124");
            assertEquals(team.getNickname(), "ÜberBots");
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get team fields");
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testParseMedia() {
        String mediaJson = "[" +
                "  {" +
                "    \"type\": \"cdphotothread\"," +
                "    \"details\": {" +
                "      \"image_partial\": \"fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg\"" +
                "    }," +
                "    \"foreign_key\": \"39894\"" +
                "  }," +
                "  {" +
                "    \"type\": \"youtube\"," +
                "    \"details\": {}," +
                "    \"foreign_key\": \"RpSgUrsghv4\"" +
                "  }" +
                "]";

        ArrayList<Media> medias = new ArrayList<>();
        JsonArray mediaArray = JSONHelper.getasJsonArray(mediaJson);
        for (JsonElement media : mediaArray) {
            medias.add(JSONHelper.getGson().fromJson(media, Media.class));
        }

        assertEquals(medias.size(), mediaArray.size());
        assertEquals(medias.size(), 2);

        Media cd = medias.get(0);
        Media yt = medias.get(1);
        try {
            assertEquals(cd.getForeignKey(), "39894");
            assertEquals(cd.getMediaType(), Media.TYPE.CD_PHOTO_THREAD);
            assertEquals(cd.getDetails(), JSONHelper.getasJsonObject("{\"image_partial\": \"fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg\"}"));

            assertEquals(yt.getMediaType(), Media.TYPE.YOUTUBE);
            assertEquals(yt.getForeignKey(), "RpSgUrsghv4");
            assertEquals(yt.getDetails(), new JsonObject());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get media fields");
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testParseMatch() {
        String matchJson = "{\"comp_level\": \"f\", \"match_number\": 1, \"videos\": [{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}], \"time_string\": \"3:36 PM\", \"set_number\": 1, \"key\": \"2014ctgro_f1m1\", \"time\": \"1394393760\", \"alliances\": {\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}, \"event_key\": \"2014ctgro\"}";
        Match match = JSONHelper.getGson().fromJson(matchJson, Match.class);

        try {
            assertEquals(match.getKey(), "2014ctgro_f1m1");
            assertEquals(match.getEventKey(), "2014ctgro");
            assertEquals(match.getMatchNumber(), 1);
            assertEquals(match.getSetNumber(), 1);
            assertEquals(match.getType(), MatchType.FINAL);
            assertEquals(match.getAlliances(), JSONHelper.getasJsonObject("{\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}"));
            assertEquals(match.getTimeString(), "3:36 PM");
            assertEquals(match.getTime(), new Date(1394393760));
            assertEquals(match.getVideos(), JSONHelper.getasJsonArray("[{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}]"));
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get match fields");
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testParseAwardNoAwardee() {
        String json = "{\n" +
                "    \"event_key\": \"2010sc\",\n" +
                "    \"name\": \"Winner\",\n" +
                "    \"recipient_list\": [\n" +
                "      {\n" +
                "        \"team_number\": 343,\n" +
                "        \"awardee\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"team_number\": 1261,\n" +
                "        \"awardee\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"team_number\": 1398,\n" +
                "        \"awardee\": null\n" +
                "      }\n" +
                "    ],\n" +
                "    \"year\": 2010\n" +
                "  }";
        Award award = JSONHelper.getGson().fromJson(json, Award.class);

        try {
            assertEquals(award.getEventKey(), "2010sc");
            assertEquals(award.getName(), "Winner");
            assertEquals(award.getYear(), 2010);

            JsonArray recips = award.getWinners();
            String[] winners = {"343", "1261", "1398"};
            assertEquals(recips.size(), 3);
            for (int i = 0; i < 3; i++) {
                assertEquals(winners[i], recips.get(i).getAsJsonObject().get("team_number").getAsString());
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get award fields");
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testParseAwardNoTeam() {
        String json = "{\n" +
                "    \"event_key\": \"2010sc\",\n" +
                "    \"name\": \"FIRST Dean's List Finalist Award\",\n" +
                "    \"recipient_list\": [\n" +
                "      {\n" +
                "        \"team_number\": null,\n" +
                "        \"awardee\": \"Brandon Dean\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"team_number\": null,\n" +
                "        \"awardee\": \"Megan Shew\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"year\": 2010\n" +
                "  }";
        Award award = JSONHelper.getGson().fromJson(json, Award.class);

        try {
            assertEquals(award.getEventKey(), "2010sc");
            assertEquals(award.getName(), "FIRST Dean's List Finalist Award");
            assertEquals(award.getYear(), 2010);

            JsonArray recips = award.getWinners();
            String[] winners = {"Brandon Dean", "Megan Shew"};
            assertEquals(recips.size(), 2);
            for (int i = 0; i < 2; i++) {
                assertEquals(winners[i], recips.get(i).getAsJsonObject().get("awardee").getAsString());
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Unable to get award fields");
            e.printStackTrace();
        }
    }

}
