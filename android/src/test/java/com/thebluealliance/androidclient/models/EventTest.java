package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventTest {
    Event mEvent;

    @Before
    public void readJsonData(){
       mEvent = ModelMaker.getModel(Event.class, "2015cthar");
    }

    @Test
    public void testEventModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mEvent);
        assertEquals(mEvent.getKey(), "2015cthar");
        assertEquals(mEvent.getWebsite(), "http://www.nefirst.org/");
        assertTrue(mEvent.isOfficial());
        assertEquals(mEvent.getCompetitionWeek(), 5);
        assertEquals(mEvent.getEventName(), "NE District - Hartford Event");
        assertEquals(mEvent.getEventShortName(), "Hartford");
        assertEquals(mEvent.getDistrictEnum(),
                     DistrictHelper.DISTRICTS.NEW_ENGLAND.ordinal());
        assertEquals(mEvent.getVenue(), "Hartford Public High School\n55 Forest Street\nHartford, CT 06105\nUSA");
        assertEquals(mEvent.getLocation(), "Hartford, CT, USA");
        assertEquals(mEvent.getYearAgnosticEventKey(), "cthar");
        assertEquals(mEvent.getEventYear(), 2015);
        assertEquals(mEvent.getEventType(),
                     EventHelper.TYPE.DISTRICT);
        assertFalse(mEvent.getWebcasts().isJsonNull());
        assertTrue(mEvent.getWebcasts().isJsonArray());
        assertFalse(mEvent.getAlliances().isJsonNull());
        assertTrue(mEvent.getAlliances().isJsonArray());

        JsonArray webcast = mEvent.getWebcasts().getAsJsonArray();
        assertEquals(webcast.size(), 1);
        assertTrue(webcast.get(0).isJsonObject());
        JsonObject castObject = webcast.get(0).getAsJsonObject();
        assertEquals(castObject.get("type").getAsString(), "twitch");
        assertEquals(castObject.get("channel").getAsString(), "nefirst_red");

        JsonArray alliances = mEvent.getAlliances().getAsJsonArray();
        assertEquals(alliances.size(), 8);
        assertTrue(alliances.get(0).isJsonObject());
        JsonObject alliance1 = alliances.get(0).getAsJsonObject();
        assertTrue(alliance1.has("declines") && alliance1.get("declines").isJsonArray());
        assertEquals(alliance1.get("declines").getAsJsonArray().size(), 0);
        assertTrue(alliance1.has("picks") && alliance1.get("picks").isJsonArray());
        assertEquals(alliance1.get("picks").getAsJsonArray().size(), 3);
        assertEquals(alliance1.get("picks").getAsJsonArray().get(0).getAsString(), "frc195");
    }
}
