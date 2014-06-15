package com.thebluealliance.androidclient.test.datafeed;

import android.test.suitebuilder.annotation.MediumTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

/**
 * File created by phil on 5/8/14.
 */
public class TestTBAApi extends TestCase {

    @MediumTest
    public void testParseEvent(){
        String eventJson = "{\"key\": \"2014ctgro\", \"end_date\": \"2014-03-09\", \"name\": \"Groton District Event\", \"short_name\": \"Groton\", \"facebook_eid\": null, \"official\": true, \"location\": \"Groton, CT, USA\", \"event_code\": \"ctgro\", \"year\": 2014, \"event_type_string\": \"District\", \"start_date\": \"2014-03-08\", \"event_type\": 1}";
        Event event = JSONManager.getGson().fromJson(eventJson, Event.class);

        //now, assert that all the properties are there
        assertEquals(event.getEventKey(),"2014ctgro");
        assertEquals(event.getStartDate(), new Date(114,2,8));
        assertEquals(event.getEndDate(), new Date(114,2,9));
        assertEquals(event.getEventName(), "Groton District Event");
        assertEquals(event.getShortName(), "Groton");
        assertEquals(event.isOfficial(), true);
        assertEquals(event.getLocation(), "Groton, CT, USA");
        assertEquals(event.getEventType(), EventHelper.TYPE.DISTRICT);
    }

    @MediumTest
    public void testParseTeam(){
        //TODO implement once https://github.com/the-blue-alliance/the-blue-alliance/issues/1012 happens
    }

    @MediumTest
    public void testParseMedia(){
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
        JsonArray mediaArray = JSONManager.getasJsonArray(mediaJson);
        for (JsonElement media : mediaArray) {
            medias.add(JSONManager.getGson().fromJson(media, Media.class));
        }

        assertEquals(medias.size(), mediaArray.size());
        assertEquals(medias.size(), 2);

        Media cd = medias.get(0);
        assertEquals(cd.getForeignKey(), "39894");
        assertEquals(cd.getMediaType(), Media.TYPE.CD_PHOTO_THREAD);
        assertEquals(cd.getDetails(), JSONManager.getasJsonObject("{\"image_partial\": \"fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg\"}"));

        Media yt = medias.get(1);
        assertEquals(yt.getMediaType(), Media.TYPE.YOUTUBE);
        assertEquals(yt.getForeignKey(), "RpSgUrsghv4");
        assertEquals(yt.getDetails(), new JsonObject());
    }

    @MediumTest
    public void testParseMatch(){
        String matchJson = "{\"comp_level\": \"f\", \"match_number\": 1, \"videos\": [{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}], \"time_string\": \"3:36 PM\", \"set_number\": 1, \"key\": \"2014ctgro_f1m1\", \"time\": \"1394393760\", \"alliances\": {\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}, \"event_key\": \"2014ctgro\"}";
        Match match = JSONManager.getGson().fromJson(matchJson, Match.class);

        assertEquals(match.getKey(), "2014ctgro_f1m1");
        assertEquals(match.getEventKey(), "2014ctgro");
        assertEquals(match.getMatchNumber(), 1);
        assertEquals(match.getSetNumber(), 1);
        assertEquals(match.getType(), MatchHelper.TYPE.FINAL);
        assertEquals(match.getAlliances(), JSONManager.getasJsonObject("{\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}"));
        assertEquals(match.getTimeString(), "3:36 PM");
        assertEquals(match.getTime(), new Date(1394393760));
        assertEquals(match.getVideos(), JSONManager.getasJsonArray("[{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}]"));
    }

    @MediumTest
    public void testParseAwardNoAwardee(){
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
        Award award = JSONManager.getGson().fromJson(json, Award.class);

        assertEquals(award.getEventKey(), "2010sc");
        assertEquals(award.getName(), "Winner");
        assertEquals(award.getYear(), 2010);

        JsonArray recips = award.getWinners();
        String[] winners = {"343", "1261", "1398"};
        assertEquals(recips.size(), 3);
        for(int i=0;i<3;i++){
            assertEquals(winners[i], recips.get(i).getAsJsonObject().get("team_number").getAsString());
        }
    }

    @MediumTest
    public void testParseAwardNoTeam(){
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
        Award award = JSONManager.getGson().fromJson(json, Award.class);

        assertEquals(award.getEventKey(), "2010sc");
        assertEquals(award.getName(), "FIRST Dean's List Finalist Award");
        assertEquals(award.getYear(), 2010);

        JsonArray recips = award.getWinners();
        String[] winners = {"Brandon Dean", "Megan Shew"};
        assertEquals(recips.size(), 2);
        for(int i=0;i<2;i++){
            assertEquals(winners[i], recips.get(i).getAsJsonObject().get("awardee").getAsString());
        }
    }

}
