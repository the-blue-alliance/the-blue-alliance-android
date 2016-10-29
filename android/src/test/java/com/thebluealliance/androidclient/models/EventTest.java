package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.types.DistrictType;
import com.thebluealliance.androidclient.types.EventType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventTest {
    private Event mEvent;
    private Event mCleanEvent;
    private Event mOffseasonEvent;

    @Before
    public void readJsonData(){
        mCleanEvent = new Event();
        mEvent = ModelMaker.getModel(Event.class, "2015cthar");
        mOffseasonEvent = ModelMaker.getModel(Event.class, "2016cc");
    }

    @Test
    public void testEventModel() throws ParseException {
        assertNotNull(mEvent);
        assertEquals(mEvent.getKey(), "2015cthar");
        assertEquals(mEvent.getWebsite(), "http://www.nefirst.org/");
        assertNotNull(mEvent.getOfficial());
        assertTrue(mEvent.getOfficial());
        assertNotNull(mEvent.getCompetitionWeek());
        assertEquals(mEvent.getCompetitionWeek().intValue(), 5);
        assertEquals(mEvent.getName(), "NE District - Hartford Event");
        assertEquals(mEvent.getShortName(), "Hartford");
        assertEquals(mEvent.getEventDistrictEnum(),
                     DistrictType.NEW_ENGLAND);
        assertEquals(mEvent.getVenueAddress(), "Hartford Public High School\n55 Forest Street\nHartford, "
                                      + "CT 06105\nUSA");
        assertEquals(mEvent.getLocation(), "Hartford, CT, USA");
        assertEquals(mEvent.getYearAgnosticEventKey(), "cthar");
        assertEquals(mEvent.getYear().intValue(), 2015);
        assertEquals(mEvent.getEventTypeEnum(),
                     EventType.DISTRICT);
        assertNotNull(mEvent.getWebcasts());

        Date start = ThreadSafeFormatters.parseEventDate("2015-03-27");
        Date end = ThreadSafeFormatters.parseEventDate("2015-03-29");
        assertNotNull(mEvent.getStartDate());
        assertNotNull(mEvent.getEndDate());
        assertEquals(mEvent.getStartDate().longValue(), start.getTime());
        assertEquals(mEvent.getEndDate().longValue(), end.getTime());
        assertEquals(mEvent.getFormattedStartDate(), start);
        assertEquals(mEvent.getFormattedEndDate(), end);
        assertEquals(mEvent.getDateString(), "Mar 27 to Mar 29, 2015");
        assertFalse(mEvent.isHappeningNow());
        assertFalse(mEvent.isChampsEvent());

        JsonArray webcast = JSONHelper.getasJsonArray(mEvent.getWebcasts());
        assertEquals(webcast.size(), 1);
        assertTrue(webcast.get(0).isJsonObject());
        JsonObject castObject = webcast.get(0).getAsJsonObject();
        assertEquals(castObject.get("type").getAsString(), "twitch");
        assertEquals(castObject.get("channel").getAsString(), "nefirst_red");

        JsonArray alliances = mEvent.getAlliancesJson();
        assertEquals(alliances.size(), 8);
        assertTrue(alliances.get(0).isJsonObject());
        JsonObject alliance1 = alliances.get(0).getAsJsonObject();
        assertTrue(alliance1.has("declines") && alliance1.get("declines").isJsonArray());
        assertEquals(alliance1.get("declines").getAsJsonArray().size(), 0);
        assertTrue(alliance1.has("picks") && alliance1.get("picks").isJsonArray());
        assertEquals(alliance1.get("picks").getAsJsonArray().size(), 3);
        assertEquals(alliance1.get("picks").getAsJsonArray().get(0).getAsString(), "frc195");
    }

    @Test
    public void testOffseasonEventModel() throws ParseException {
        assertNotNull(mOffseasonEvent);
        assertEquals(mOffseasonEvent.getKey(), "2016cc");
        assertEquals(mOffseasonEvent.getWebsite(), "");
        assertNotNull(mOffseasonEvent.getOfficial());
        assertFalse(mOffseasonEvent.getOfficial());
        assertNotNull(mOffseasonEvent.getCompetitionWeek());
        assertEquals(mOffseasonEvent.getCompetitionWeek().intValue(), 31);
        assertEquals(mOffseasonEvent.getName(), "Chezy Champs");
        assertEquals(mOffseasonEvent.getShortName(), "Chezy Champs");
        assertEquals(mOffseasonEvent.getEventDistrictEnum(),
                     DistrictType.NO_DISTRICT);
        assertEquals(mOffseasonEvent.getVenueAddress(), "Bellarmine College Prep\n850 Elm St.\nSan Jose," +
                                               " California 95126\nUSA");
        assertEquals(mOffseasonEvent.getLocation(), "San Jose, CA, USA");
        assertEquals(mOffseasonEvent.getYearAgnosticEventKey(), "cc");
        assertEquals(mOffseasonEvent.getYear().intValue(), 2016);
        assertEquals(mOffseasonEvent.getEventTypeEnum(),
                     EventType.OFFSEASON);
        assertNotNull(mOffseasonEvent.getWebcasts());

        Date start = ThreadSafeFormatters.parseEventDate("2016-09-24");
        Date end = ThreadSafeFormatters.parseEventDate("2016-09-25");
        assertNotNull(mOffseasonEvent.getStartDate());
        assertNotNull(mOffseasonEvent.getEndDate());
        assertEquals(mOffseasonEvent.getStartDate().longValue(), start.getTime());
        assertEquals(mOffseasonEvent.getEndDate().longValue(), end.getTime());
        assertEquals(mOffseasonEvent.getFormattedStartDate(), start);
        assertEquals(mOffseasonEvent.getFormattedEndDate(), end);
        assertEquals(mOffseasonEvent.getDateString(), "Sep 24 to Sep 25, 2016");
        assertFalse(mOffseasonEvent.isHappeningNow());
        assertFalse(mOffseasonEvent.isChampsEvent());

        JsonArray webcast = JSONHelper.getasJsonArray(mOffseasonEvent.getWebcasts());
        assertEquals(webcast.size(), 1);
        assertTrue(webcast.get(0).isJsonObject());
        JsonObject castObject = webcast.get(0).getAsJsonObject();
        assertEquals(castObject.get("type").getAsString(), "twitch");
        assertEquals(castObject.get("channel").getAsString(), "frcgamesense");

        JsonArray alliances = mOffseasonEvent.getAlliancesJson();
        assertEquals(alliances.size(), 8);
        assertTrue(alliances.get(0).isJsonObject());
        JsonObject alliance1 = alliances.get(0).getAsJsonObject();
        assertTrue(alliance1.has("declines") && alliance1.get("declines").isJsonArray());
        assertEquals(alliance1.get("declines").getAsJsonArray().size(), 0);
        assertTrue(alliance1.has("picks") && alliance1.get("picks").isJsonArray());
        assertEquals(alliance1.get("picks").getAsJsonArray().size(), 4);
        assertEquals(alliance1.get("picks").getAsJsonArray().get(0).getAsString(), "frc971");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadStartDate() {
        mCleanEvent.setStartDate("foobar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadEndDate() {
        mCleanEvent.setEndDate("foobar");
    }

    @Test
    public void testNoDatesSet(){
        String dateString = mCleanEvent.getDateString();
        Date start = mCleanEvent.getFormattedStartDate();
        Date end = mCleanEvent.getFormattedEndDate();
        Date expected = new Date(0);

        assertEquals("", dateString);
        assertEquals(expected, start);
        assertEquals(expected, end);

        // Now, test when only the end date is done
        // Should still have an empty date string
        mCleanEvent.setEndDate("2015-03-27");
        dateString = mCleanEvent.getDateString();
        assertEquals("", dateString);
    }

    @Test
    public void testEventTypeEnum() {
        // Default case - none set
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.NONE);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(0);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.REGIONAL);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(1);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.DISTRICT);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(2);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.DISTRICT_CMP);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(3);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.CMP_DIVISION);
        assertTrue(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(4);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.CMP_FINALS);
        assertTrue(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(99);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.OFFSEASON);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(100);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.PRESEASON);
        assertFalse(mCleanEvent.isChampsEvent());

        mCleanEvent.setEventType(999);
        assertEquals(mCleanEvent.getEventTypeEnum(), EventType.NONE);
        assertFalse(mCleanEvent.isChampsEvent());
    }

    @Test
    public void testDefaultDistrictEnum() {
        DistrictType districtType = mCleanEvent.getEventDistrictEnum();
        assertEquals(districtType, DistrictType.NO_DISTRICT);
    }

    @Test
    public void testSingleDayEventDateString() throws ParseException {
        mCleanEvent.setStartDate("2015-03-27");
        mCleanEvent.setEndDate("2015-03-27");

        String dateString = mCleanEvent.getDateString();
        assertEquals("Mar 27, 2015", dateString);
    }

    @Test
    public void testDefaultShortName() {
        String name = "Test Event";
        mCleanEvent.setName(name);
        assertEquals(mCleanEvent.getShortName(), name);

        mCleanEvent.setShortName("");
        assertEquals(mCleanEvent.getShortName(), name);
    }

    @Test
    public void testLoadYearFromEventKey() {
        mCleanEvent.setKey("2015cthar");
        assertNotNull(mCleanEvent.getYear());
        assertEquals(mCleanEvent.getYear().intValue(), 2015);
    }
}
