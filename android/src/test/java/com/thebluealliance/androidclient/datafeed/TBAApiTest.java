package com.thebluealliance.androidclient.datafeed;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.types.MediaType;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;

@Ignore
@RunWith(RobolectricTestRunner.class)
public class TBAApiTest {

    @org.junit.Test
    public void testParseEvent() {
        String eventJson = "{\"key\": \"2014ctgro\", \"end_date\": \"2014-03-09\", \"name\": \"Groton District Event\", \"short_name\": \"Groton\", \"facebook_eid\": null, \"official\": true, \"location\": \"Groton, CT, USA\", \"event_code\": \"ctgro\", \"year\": 2014, \"event_type_string\": \"District\", \"start_date\": \"2014-03-08\", \"event_type\": 1}";
        Event event = JSONHelper.getGson().fromJson(eventJson, Event.class);

        //now, assert that all the properties are there
        assertEquals(event.getKey(), "2014ctgro");
        assertEquals(event.getStartDate().getTime(), new Date(114, 2, 8).getTime());
        assertEquals(event.getEndDate().getTime(), new Date(114, 2, 9).getTime());
        assertEquals(event.getName(), "Groton District Event");
        assertEquals(event.getShortName(), "Groton");
        assertEquals(event.getAddress(), "Groton, CT, USA");
        assertEquals(event.getEventTypeEnum(), EventType.DISTRICT);
    }

    @org.junit.Test
    public void testParseTeam() {
        String teamJson = "{\n"
                + "  \"website\": \"http://www.uberbots.org\",\n"
                + "  \"name\": \"UTC Fire and Security & Avon High School\",\n"
                + "  \"locality\": \"Avon\",\n"
                + "  \"rookie_year\": 2003,\n"
                + "  \"region\": \"CT\",\n"
                + "  \"team_number\": 1124,\n"
                + "  \"location\": \"Avon, CT, USA\",\n"
                + "  \"key\": \"frc1124\",\n"
                + "  \"country_name\": \"USA\",\n"
                + "  \"nickname\": \"ÜberBots\"\n"
                + "}";
        Team team = JSONHelper.getGson().fromJson(teamJson, Team.class);

        assertEquals(team.getWebsite(), "http://www.uberbots.org");
        assertEquals(team.getName(), "UTC Fire and Security & Avon High School");
        assertEquals(team.getAddress(), "Avon, CT, USA");
        assertEquals((int) team.getTeamNumber(), 1124);
        assertEquals(team.getKey(), "frc1124");
        assertEquals(team.getNickname(), "ÜberBots");
    }

    @org.junit.Test
    public void testParseMedia() {
        String mediaJson = "["
                + "  {"
                + "    \"type\": \"cdphotothread\","
                + "    \"details\": {"
                + "      \"image_partial\": \"fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg\""
                + "    },"
                + "    \"foreign_key\": \"39894\""
                + "  },"
                + "  {"
                + "    \"type\": \"youtube\","
                + "    \"details\": {},"
                + "    \"foreign_key\": \"RpSgUrsghv4\""
                + "  }"
                + "]";

        ArrayList<Media> medias = new ArrayList<>();
        JsonArray mediaArray = JSONHelper.getasJsonArray(mediaJson);
        for (JsonElement media : mediaArray) {
            medias.add(JSONHelper.getGson().fromJson(media, Media.class));
        }

        assertEquals(medias.size(), mediaArray.size());
        assertEquals(medias.size(), 2);

        Media cd = medias.get(0);
        Media yt = medias.get(1);
        assertEquals(cd.getForeignKey(), "39894");
        assertEquals(MediaType.fromString(cd.getType()), MediaType.CD_PHOTO_THREAD);
        assertEquals(cd.getDetailsJson(),
                     JSONHelper.getasJsonObject(
                             "{\"image_partial\": \"fe3/fe38d320428adf4f51ac969efb3db32c_l"
                             + ".jpg\"}"));

        assertEquals(MediaType.fromString(yt.getType()), MediaType.YOUTUBE);
        assertEquals(yt.getForeignKey(), "RpSgUrsghv4");
        assertEquals(yt.getDetailsJson(), new JsonObject());
    }

    @org.junit.Test
    public void testParseMatch() {
        /*
        String matchJson = "{\"comp_level\": \"f\", \"match_number\": 1, \"videos\": [{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}], \"time_string\": \"3:36 PM\", \"set_number\": 1, \"key\": \"2014ctgro_f1m1\", \"time\": \"1394393760\", \"alliances\": {\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}, \"event_key\": \"2014ctgro\"}";
        Match match = JSONHelper.getGson().fromJson(matchJson, Match.class);

        assertEquals(match.getKey(), "2014ctgro_f1m1");
        assertEquals(match.getEventKey(), "2014ctgro");
        assertEquals(match.getMatchNumber().intValue(), 1);
        assertEquals(match.getSetNumber().intValue(), 1);
        assertEquals(MatchType.fromShortType(match.getCompLevel()), MatchType.FINAL);
        assertEquals(match.getAlliancesJson(),
                     JSONHelper.getasJsonObject(
                             "{\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", "
                             + "\"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", "
                             + "\"frc237\", \"frc2064\"]}}"));
        assertEquals(match.getTime().longValue(), new Date(1394393760).getTime());
        assertEquals(match.getVideosJson(),
                     JSONHelper
                             .getasJsonArray("[{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}]"));
                             */
    }

    @org.junit.Test
    public void testParseAwardNoAwardee() {
        String json = "{\n"
                + "    \"event_key\": \"2010sc\",\n"
                + "    \"name\": \"Winner\",\n"
                + "    \"recipient_list\": [\n"
                + "      {\n"
                + "        \"team_number\": 343,\n"
                + "        \"awardee\": null\n"
                + "      },\n"
                + "      {\n"
                + "        \"team_number\": 1261,\n"
                + "        \"awardee\": null\n"
                + "      },\n"
                + "      {\n"
                + "        \"team_number\": 1398,\n"
                + "        \"awardee\": null\n"
                + "      }\n"
                + "    ],\n"
                + "    \"year\": 2010\n"
                + "  }";
        Award award = JSONHelper.getGson().fromJson(json, Award.class);

        assertEquals(award.getEventKey(), "2010sc");
        assertEquals(award.getName(), "Winner");
        assertEquals(award.getYear().intValue(), 2010);

        /*
        JsonArray recips = award.getWinners();
        String[] winners = {"343", "1261", "1398"};
        assertNotNull(recips);
        assertEquals(recips.size(), 3);
        for (int i = 0; i < 3; i++) {
            assertEquals(winners[i],
                         recips.get(i).getAsJsonObject().get("team_number").getAsString());
        }*/
    }

    @org.junit.Test
    public void testParseAwardNoTeam() {
        String json = "{\n"
                + "    \"event_key\": \"2010sc\",\n"
                + "    \"name\": \"FIRST Dean's List Finalist Award\",\n"
                + "    \"recipient_list\": [\n"
                + "      {\n"
                + "        \"team_number\": null,\n"
                + "        \"awardee\": \"Brandon Dean\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"team_number\": null,\n"
                + "        \"awardee\": \"Megan Shew\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"year\": 2010\n"
                + "  }";
        Award award = JSONHelper.getGson().fromJson(json, Award.class);

        assertEquals(award.getEventKey(), "2010sc");
        assertEquals(award.getName(), "FIRST Dean's List Finalist Award");
        assertEquals(award.getYear().intValue(), 2010);
        /*
        JsonArray recips = award.getWinners();
        String[] winners = {"Brandon Dean", "Megan Shew"};
        assertNotNull(recips);
        assertEquals(recips.size(), 2);
        for (int i = 0; i < 2; i++) {
            assertEquals(winners[i], recips.get(i).getAsJsonObject().get("awardee").getAsString());
        }*/
    }

}
