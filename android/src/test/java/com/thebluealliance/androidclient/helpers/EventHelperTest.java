package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.EventType;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventHelperTest {

    @Test
    public void testEventShortName() {
        assertEquals("{ Random 2.718 stuff! }", EventHelper.shortName("  { Random 2.718 stuff! }  "));
        assertEquals("Bee's Knee's LX", EventHelper.shortName("UN District -Bee's Knee's LX  "));
        assertEquals("Brussels Int'l", EventHelper.shortName("EUR District - Brussels Int'l Event sponsored by Sprouts"));
        assertEquals("Brussels Int'l", EventHelper.shortName("EUR District - Brussels Int'l Eventapalooza sponsored by TBA"));
        assertEquals("ReallyBig", EventHelper.shortName("RB District - ReallyBigEvent Scaling Up Every Year"));

        assertEquals("Detroit", EventHelper.shortName("FRC Detroit FIRST Robotics District Competition"));
        assertEquals("Detroit", EventHelper.shortName("FIRST Robotics Detroit FRC State Championship"));
        assertEquals("Maui", EventHelper.shortName("Maui FIRST Robotics Regional and Luau"));
        assertEquals("California", EventHelper.shortName("California State Surf and Turf sponsored by TBA"));
        assertEquals("CarTalk Plaza", EventHelper.shortName("CarTalk Plaza Tournament"));
        assertEquals("IRI", EventHelper.shortName("IRI FRC Be-all and End-all"));
        assertEquals("Ada", EventHelper.shortName("   Ada    Field  "));
        assertEquals("Einstein", EventHelper.shortName(" FIRST Robotics Einstein Field Equations "));
        assertEquals("Martin Luther King Jr.", EventHelper.shortName("FRC Martin Luther King Jr. Region Championship"));
        assertEquals("Ada Lovelace", EventHelper.shortName("PNW   Ada Lovelace    Tournament of Software  "));
        assertEquals("Ada Lovelace", EventHelper.shortName("\tPNW   Ada Lovelace    Tournament of Software  "));
        assertEquals("Rosa Parks", EventHelper.shortName(" MAR FIRST Robotics   Rosa Parks    FRC Tournament of Roses  "));
        assertEquals("Washington D.C.", EventHelper.shortName("Washington D.C. FIRST Robotics Region"));
        assertEquals("Washington D.C.", EventHelper.shortName("Washington D.C. FIRST Robotics Region."));
        assertEquals("Washington D.C. FIRST Robotics Regiontonian", EventHelper.shortName("Washington D.C. FIRST Robotics Regiontonian")); // Does not match "Region\b"

        assertEquals("FIRST Robotics Competition", EventHelper.shortName("FIRST Robotics Competition"));
        assertEquals("National Championship", EventHelper.shortName("National Championship"));
        assertEquals("New England", EventHelper.shortName("New England Tournament"));
        assertEquals("FIRST National Championship", EventHelper.shortName("FIRST National Championship"));
        assertEquals("Motorola Midwest", EventHelper.shortName("Motorola Midwest Regional"));
        assertEquals("DEKA New England", EventHelper.shortName("DEKA New England Regional"));
        assertEquals("Johnson & Johnson Mid-Atlantic", EventHelper.shortName("Johnson & Johnson Mid-Atlantic Regional"));
        assertEquals("Great Lakes", EventHelper.shortName("Great Lakes Regional"));
        assertEquals("New England", EventHelper.shortName("New England Regional"));
        assertEquals("Southwest", EventHelper.shortName("Southwest Regional"));
        assertEquals("NASA Ames", EventHelper.shortName("NASA Ames Regional"));
        assertEquals("Kennedy Space Center", EventHelper.shortName("Kennedy Space Center Regional"));
        assertEquals("UTC New England", EventHelper.shortName("UTC New England Regional"));
        assertEquals("Philadelphia Alliance", EventHelper.shortName("Philadelphia Alliance Regional"));
        assertEquals("Kennedy Space Center Southeast", EventHelper.shortName("Kennedy Space Center Southeast Regional"));
        assertEquals("Long Island", EventHelper.shortName("Long Island Regional"));
        assertEquals("Lone Star", EventHelper.shortName("Lone Star Regional"));
        assertEquals("NASA Langley/VCU", EventHelper.shortName("NASA Langley/VCU Regional"));
        assertEquals("Archimedes", EventHelper.shortName("Archimedes Field"));
        assertEquals("Southern California", EventHelper.shortName("Southern California Regional"));
        assertEquals("Silicon Valley", EventHelper.shortName("Silicon Valley Regional"));
        assertEquals("UTC/New England", EventHelper.shortName("UTC/New England Regional"));
        assertEquals("Curie", EventHelper.shortName("Curie Field"));
        assertEquals("NASA KSC Southeast", EventHelper.shortName("NASA KSC Southeast Regional"));
        assertEquals("Galileo", EventHelper.shortName("Galileo Field"));
        assertEquals("West Michigan", EventHelper.shortName("West Michigan Regional"));
        assertEquals("Newton", EventHelper.shortName("Newton Field"));
        assertEquals("J&J Mid-Atlantic", EventHelper.shortName("J&J Mid-Atlantic Regional"));
        assertEquals("New York City", EventHelper.shortName("New York City Regional"));
        assertEquals("NASA Langley", EventHelper.shortName("NASA Langley Regional"));
        assertEquals("SBPLI Long Island", EventHelper.shortName("SBPLI Long Island Regional"));
        assertEquals("Western Michigan", EventHelper.shortName("Western Michigan Regional"));
        assertEquals("St. Louis", EventHelper.shortName("St. Louis Regional"));
        assertEquals("J&J Mid Atlantic", EventHelper.shortName("J&J Mid Atlantic Regional"));
        assertEquals("Buckeye", EventHelper.shortName("Buckeye Regional"));
        assertEquals("Canadian", EventHelper.shortName("Canadian Regional"));
        assertEquals("NASA Langley / VCU", EventHelper.shortName("NASA Langley / VCU Regional"));
        assertEquals("Pacific Northwest", EventHelper.shortName("Pacific Northwest Regional"));
        assertEquals("Arizona", EventHelper.shortName("Arizona Regional"));
        assertEquals("Einstein", EventHelper.shortName("Einstein Field"));
        assertEquals("Central Florida", EventHelper.shortName("Central Florida Regional"));
        assertEquals("Peachtree", EventHelper.shortName("Peachtree Regional"));
        assertEquals("Midwest", EventHelper.shortName("Midwest Regional"));
        assertEquals("Chesapeake", EventHelper.shortName("Chesapeake Regional"));
        assertEquals("BAE SYSTEMS Granite State", EventHelper.shortName("BAE SYSTEMS Granite State Regional"));
        assertEquals("Philadelphia", EventHelper.shortName("Philadelphia Regional"));
        assertEquals("Pittsburgh", EventHelper.shortName("Pittsburgh Regional"));
        assertEquals("Sacramento", EventHelper.shortName("Sacramento Regional"));
        assertEquals("NASA / VCU", EventHelper.shortName("NASA / VCU Regional"));
        assertEquals("Colorado", EventHelper.shortName("Colorado Regional"));
        assertEquals("Detroit", EventHelper.shortName("Detroit Regional"));
        assertEquals("Florida", EventHelper.shortName("Florida Regional"));
        assertEquals("New Jersey", EventHelper.shortName("New Jersey Regional"));
        assertEquals("Greater Toronto", EventHelper.shortName("Greater Toronto Regional"));
        assertEquals("Palmetto", EventHelper.shortName("Palmetto Regional"));
        assertEquals("Boilermaker", EventHelper.shortName("Boilermaker Regional"));
        assertEquals("GM/Technion University Israel Pilot", EventHelper.shortName("GM/Technion University Israel Pilot Regional"));
        assertEquals("Las Vegas", EventHelper.shortName("Las Vegas Regional"));
        assertEquals("Finger Lakes", EventHelper.shortName("Finger Lakes Regional"));
        assertEquals("Waterloo", EventHelper.shortName("Waterloo Regional"));
        assertEquals("GM/Technion Israel", EventHelper.shortName("GM/Technion Israel Regional"));
        assertEquals("Boston", EventHelper.shortName("Boston Regional"));
        assertEquals("Davis Sacramento", EventHelper.shortName("Davis Sacramento Regional"));
        assertEquals("Wisconsin", EventHelper.shortName("Wisconsin Regional"));
        assertEquals("Brazil Pilot", EventHelper.shortName("Brazil Pilot"));
        assertEquals("Los Angeles", EventHelper.shortName("Los Angeles Regional"));
        assertEquals("UTC Connecticut", EventHelper.shortName("UTC Connecticut Regional"));
        assertEquals("Greater Kansas City", EventHelper.shortName("Greater Kansas City Regional"));
        assertEquals("Bayou", EventHelper.shortName("Bayou Regional"));
        assertEquals("San Diego", EventHelper.shortName("San Diego Regional"));
        assertEquals("Brazil", EventHelper.shortName("Brazil Regional"));
        assertEquals("Connecticut", EventHelper.shortName("Connecticut Regional"));
        assertEquals("Hawaii", EventHelper.shortName("Hawaii Regional"));
        assertEquals("Israel", EventHelper.shortName("Israel Regional"));
        assertEquals("Minnesota", EventHelper.shortName("Minnesota Regional"));
        assertEquals("BAE Systems Granite State", EventHelper.shortName("BAE Systems Granite State Regional"));
        assertEquals("Oklahoma City", EventHelper.shortName("Oklahoma City Regional"));
        assertEquals("Oregon", EventHelper.shortName("Oregon Regional"));
        assertEquals("UC Davis Sacramento", EventHelper.shortName("UC Davis Sacramento Regional"));
        assertEquals("Microsoft Seattle", EventHelper.shortName("Microsoft Seattle Regional"));
        assertEquals("Dallas", EventHelper.shortName("Dallas Regional, Sponsored by JCPenney and the JCPenney Afterschool Fund"));
        assertEquals("Washington DC", EventHelper.shortName("Washington DC  Regional"));
        assertEquals("Detroit", EventHelper.shortName("Detroit FIRST Robotics District Competition"));
        assertEquals("Cass Tech", EventHelper.shortName("Cass Tech FIRST Robotics District Competition"));
        assertEquals("Kettering University", EventHelper.shortName("Kettering University FIRST Robotics District Competition"));
        assertEquals("Michigan", EventHelper.shortName("Michigan FIRST Robotics Competition State Championship"));
        assertEquals("Lansing", EventHelper.shortName("Lansing FIRST Robotics District Competition"));
        assertEquals("Traverse City", EventHelper.shortName("Traverse City FIRST Robotics District Competition"));
        assertEquals("West Michigan", EventHelper.shortName("West Michigan FIRST Robotics District Competition"));
        assertEquals("Minnesota 10000 Lakes", EventHelper.shortName("Minnesota 10000 Lakes Regional"));
        assertEquals("Minnesota North Star", EventHelper.shortName("Minnesota North Star Regional"));
        assertEquals("BAE Granite State", EventHelper.shortName("BAE Granite State Regional"));
        assertEquals("Troy", EventHelper.shortName("Troy FIRST Robotics District Competition"));
        assertEquals("NASA VCU", EventHelper.shortName("NASA VCU Regional"));
        assertEquals("Northeast Utilities FIRST Connecticut", EventHelper.shortName("Northeast Utilities FIRST Connecticut Regional"));
        assertEquals("Dallas", EventHelper.shortName("Dallas Regional sponsored by JCPenney and the JCPenney Afterschool Fund"));
        assertEquals("Hawaii", EventHelper.shortName("Hawaii Regional sponsored by BAE Systems"));
        assertEquals("North Carolina", EventHelper.shortName("North Carolina Regional"));
        assertEquals("Oklahoma", EventHelper.shortName("Oklahoma Regional"));
        assertEquals("Autodesk Oregon", EventHelper.shortName("Autodesk Oregon Regional"));
        assertEquals("Silicon Valley", EventHelper.shortName("Silicon Valley Regional sponsored by Google.org and BAE Systems"));
        assertEquals("Utah", EventHelper.shortName("Utah Regional sponsored by NASA & Platt"));
        assertEquals("Virginia", EventHelper.shortName("Virginia Regional"));
        assertEquals("Ann Arbor", EventHelper.shortName("Ann Arbor FIRST Robotics District Competition"));
        assertEquals("WPI", EventHelper.shortName("WPI Regional"));
        assertEquals("Dallas", EventHelper.shortName("Dallas Regional sponsored by jcpenney"));
        assertEquals("Lake Superior", EventHelper.shortName("Lake Superior Regional"));
        assertEquals("Michigan", EventHelper.shortName("Michigan FIRST Robotics District Competition State Championship"));
        assertEquals("BAE Systems/Granite State", EventHelper.shortName("BAE Systems/Granite State Regional"));
        assertEquals("Waterford", EventHelper.shortName("Waterford FIRST Robotics District Competition"));
        assertEquals("Greater Toronto East", EventHelper.shortName("Greater Toronto East Regional"));
        assertEquals("Greater Toronto West", EventHelper.shortName("Greater Toronto West Regional"));
        assertEquals("Alamo", EventHelper.shortName("Alamo Regional"));
        assertEquals("Niles", EventHelper.shortName("Niles FIRST Robotics District Competition"));
        assertEquals("Smoky Mountain", EventHelper.shortName("Smoky Mountain Regional"));
        assertEquals("Utah", EventHelper.shortName("Utah Regional co-sponsored by NASA and Platt"));
        assertEquals("Seattle Olympic", EventHelper.shortName("Seattle Olympic Regional"));
        assertEquals("Seattle Cascade", EventHelper.shortName("Seattle Cascade Regional"));
        assertEquals("Livonia", EventHelper.shortName("Livonia FIRST Robotics District Competition"));
        assertEquals("Central Valley", EventHelper.shortName("Central Valley Regional"));
        assertEquals("Dallas East", EventHelper.shortName("Dallas East Regional sponsored by jcpenney"));
        assertEquals("Dallas West", EventHelper.shortName("Dallas West Regional sponsored by jcpenney"));
        assertEquals("Orlando", EventHelper.shortName("Orlando Regional"));
        assertEquals("Michigan", EventHelper.shortName("Michigan FRC State Championship"));
        assertEquals("Gull Lake", EventHelper.shortName("Gull Lake FIRST Robotics District Competition"));
        assertEquals("Rutgers University", EventHelper.shortName("Rutgers University FIRST Robotics District Competition"));
        assertEquals("Mount Olive", EventHelper.shortName("Mount Olive FIRST Robotics District Competition"));
        assertEquals("Lenape", EventHelper.shortName("Lenape FIRST Robotics District Competition"));
        assertEquals("Queen City", EventHelper.shortName("Queen City Regional"));
        assertEquals("Mid-Atlantic Robotics", EventHelper.shortName("Mid-Atlantic Robotics FRC Region Championship"));
        assertEquals("Hatboro-Horsham", EventHelper.shortName("Hatboro-Horsham FIRST Robotics District Competition"));
        assertEquals("Chestnut Hill", EventHelper.shortName("Chestnut Hill FIRST Robotics District Competition"));
        assertEquals("Festival de Robotique", EventHelper.shortName("Festival de Robotique FRC a Montreal Regional"));
        assertEquals("South Florida", EventHelper.shortName("South Florida Regional"));
        assertEquals("Smoky Mountains", EventHelper.shortName("Smoky Mountains Regional"));
        assertEquals("Spokane", EventHelper.shortName("Spokane Regional"));
        assertEquals("Northville", EventHelper.shortName("Northville FIRST Robotics District Competition"));
        assertEquals("Western Canadian", EventHelper.shortName("Western Canadian FRC Regional"));
        assertEquals("Razorback", EventHelper.shortName("Razorback Regional"));
        assertEquals("Phoenix", EventHelper.shortName("Phoenix Regional"));
        assertEquals("Los Angeles", EventHelper.shortName("Los Angeles Regional sponsored by The Roddenberry Foundation"));
        assertEquals("Inland Empire", EventHelper.shortName("Inland Empire Regional"));
        assertEquals("Connecticut", EventHelper.shortName("Connecticut Regional sponsored by UTC"));
        assertEquals("Crossroads", EventHelper.shortName("Crossroads Regional"));
        assertEquals("Pine Tree", EventHelper.shortName("Pine Tree Regional"));
        assertEquals("Bedford", EventHelper.shortName("Bedford FIRST Robotics District Competition"));
        assertEquals("Grand Blanc", EventHelper.shortName("Grand Blanc FIRST Robotics District Competition"));
        assertEquals("St Joseph", EventHelper.shortName("St Joseph FIRST Robotics District Competition"));
        assertEquals("Northern Lights", EventHelper.shortName("Northern Lights Regional"));
        assertEquals("Bridgewater-Raritan", EventHelper.shortName("Bridgewater-Raritan FIRST Robotics District Competition"));
        assertEquals("TCNJ", EventHelper.shortName("TCNJ FIRST Robotics District Competition"));
        assertEquals("Lenape Seneca", EventHelper.shortName("Lenape Seneca FIRST Robotics District Competition"));
        assertEquals("Springside - Chestnut Hill", EventHelper.shortName("Springside - Chestnut Hill FIRST Robotics District Competition"));
        assertEquals("Festival de Robotique", EventHelper.shortName("Festival de Robotique FRC de Montreal Regional"));
        assertEquals("Dallas", EventHelper.shortName("Dallas Regional"));
        assertEquals("Hub City", EventHelper.shortName("Hub City Regional"));
        assertEquals("Alamo", EventHelper.shortName("Alamo Regional sponsored by Rackspace Hosting"));
        assertEquals("Utah", EventHelper.shortName("Utah Regional co-sponsored by the Larry H. Miller Group & Platt"));
        assertEquals("Seattle", EventHelper.shortName("Seattle Regional"));
        assertEquals("Central Washington", EventHelper.shortName("Central Washington Regional"));
        assertEquals("Western Canada", EventHelper.shortName("Western Canada Regional"));
        assertEquals("Arkansas", EventHelper.shortName("Arkansas Regional"));
        assertEquals("Groton", EventHelper.shortName("Groton District Event"));
        assertEquals("Hartford", EventHelper.shortName("Hartford District Event"));
        assertEquals("Southington", EventHelper.shortName("Southington District Event"));
        assertEquals("Greater DC", EventHelper.shortName("Greater DC Regional"));
        assertEquals("Central Illinois", EventHelper.shortName("Central Illinois Regional"));
        assertEquals("Northeastern University", EventHelper.shortName("Northeastern University District Event"));
        assertEquals("WPI", EventHelper.shortName("WPI District Event"));
        assertEquals("Pine Tree", EventHelper.shortName("Pine Tree District Event"));
        assertEquals("Center Line", EventHelper.shortName("Center Line FIRST Robotics District Competition"));
        assertEquals("Escanaba", EventHelper.shortName("Escanaba FIRST Robotics District Competition"));
        assertEquals("Howell", EventHelper.shortName("Howell FIRST Robotics District Competition"));
        assertEquals("St. Joseph", EventHelper.shortName("St. Joseph FIRST Robotics District Competition"));
        assertEquals("Southfield", EventHelper.shortName("Southfield FIRST Robotics District Competition"));
        assertEquals("Mexico City", EventHelper.shortName("Mexico City Regional"));
        assertEquals("New England", EventHelper.shortName("New England FRC Region Championship"));
        assertEquals("UNH", EventHelper.shortName("UNH District Event"));
        assertEquals("Granite State", EventHelper.shortName("Granite State District Event"));
        assertEquals("Bridgewater-Raritan", EventHelper.shortName("MAR FIRST Robotics Bridgewater-Raritan District Competition"));
        assertEquals("Clifton", EventHelper.shortName("MAR FIRST Robotics Clifton District Competition"));
        assertEquals("Mt. Olive", EventHelper.shortName("MAR FIRST Robotics Mt. Olive District Competition"));
        assertEquals("Lenape-Seneca", EventHelper.shortName("MAR FIRST Robotics Lenape-Seneca District Competition"));
        assertEquals("New York Tech Valley", EventHelper.shortName("New York Tech Valley Regional"));
        assertEquals("North Bay", EventHelper.shortName("North Bay Regional"));
        assertEquals("Windsor Essex Great Lakes", EventHelper.shortName("Windsor Essex Great Lakes Regional"));
        assertEquals("Oregon City", EventHelper.shortName("PNW FIRST Robotics Oregon City District Event"));
        assertEquals("Oregon State University", EventHelper.shortName("PNW FIRST Robotics Oregon State University District Event"));
        assertEquals("Wilsonville", EventHelper.shortName("PNW FIRST Robotics Wilsonville District Event"));
        assertEquals("Hatboro-Horsham", EventHelper.shortName("MAR FIRST Robotics Hatboro-Horsham District Competition"));
        assertEquals("Springside Chestnut Hill", EventHelper.shortName("MAR FIRST Robotics Springside Chestnut Hill District Competition"));
        assertEquals("Greater Pittsburgh", EventHelper.shortName("Greater Pittsburgh Regional"));
        assertEquals("Autodesk PNW", EventHelper.shortName("Autodesk PNW FRC Championship"));
        assertEquals("Rhode Island", EventHelper.shortName("Rhode Island District Event"));
        assertEquals("Utah", EventHelper.shortName("Utah Regional"));
        assertEquals("Auburn", EventHelper.shortName("PNW FIRST Robotics Auburn District Event"));
        assertEquals("Auburn Mountainview", EventHelper.shortName("PNW FIRST Robotics Auburn Mountainview District Event"));
        assertEquals("Eastern Washington University", EventHelper.shortName("PNW FIRST Robotics Eastern Washington University District Event"));
        assertEquals("Central Washington University", EventHelper.shortName("PNW FIRST Robotics Central Washington University District Event"));
        assertEquals("Mt. Vernon", EventHelper.shortName("PNW FIRST Robotics Mt. Vernon District Event"));
        assertEquals("Shorewood", EventHelper.shortName("PNW FIRST Robotics Shorewood District Event"));
        assertEquals("Glacier Peak", EventHelper.shortName("PNW FIRST Robotics Glacier Peak District Event"));
        // 2015 edge cases
        assertEquals("Howell", EventHelper.shortName("FIM District - Howell Event"));
        assertEquals("Granite State", EventHelper.shortName("NE District - Granite State Event"));
        assertEquals("Oregon City", EventHelper.shortName("PNW District - Oregon City Event"));
        assertEquals("Indianapolis", EventHelper.shortName("IN District -Indianapolis"));
        assertEquals("Mt. Olive", EventHelper.shortName("MAR District - Mt. Olive Event"));
        assertEquals("Israel", EventHelper.shortName("Israel Regional - see Site Info for additional information"));
        assertEquals("Kokomo City of Firsts", EventHelper.shortName("IN District - Kokomo City of Firsts Event sponsored by AndyMark"));
    }

    @Test
    public void testValidateKey() {
        String[] validKeys = new String[]{"2015cthar", "2014cmp", "2003moo", "2013moo2"};
        String[] invalidKeys = new String[]{null, "", "foobar", "2012"};

        for (String key : validKeys) {
            assertTrue(EventHelper.validateEventKey(key));
        }

        for (String key : invalidKeys) {
            assertFalse(EventHelper.validateEventKey(key));
        }
    }

    @Test
    public void testGetYear() {
        assertEquals(2015, EventHelper.getYear("2015cthar"));
        assertEquals(2012, EventHelper.getYear("2012ct"));
    }

    @Test
    public void testGetYearWeek() {
        assertEquals(-1, EventHelper.getYearWeek(null));

        Date date = new Date(115, 1, 5);
        assertEquals(EventHelper.getYearWeek(date), 6);
    }

    @Test
    public void testGetCompWeek() {
        assertEquals(-1, EventHelper.competitionWeek(null));

        Date preseason = new Date(115, 1, 5);
        assertEquals(0, EventHelper.competitionWeek(preseason));

        Date week5 = new Date(115, 2, 28);
        assertEquals(5, EventHelper.competitionWeek(week5));
    }

    @Test
    public void testGetLabelForEvent()  {
        Event noType = mockEventType(EventType.NONE);
        assertEquals(EventHelper.generateLabelForEvent(noType), EventHelper.WEEKLESS_LABEL);

        Event preseason = mockEventType(EventType.PRESEASON);
        assertEquals(EventHelper.generateLabelForEvent(preseason), EventHelper.PRESEASON_LABEL);

        Event offseason = mockEventType(EventType.OFFSEASON);
        when(offseason.getFormattedStartDate()).thenReturn(new Date(115, 4, 2));
        assertEquals(EventHelper.generateLabelForEvent(offseason), "May Offseason Events");

        Event cmpDivision = mockEventType(EventType.CMP_DIVISION);
        assertEquals(EventHelper.generateLabelForEvent(cmpDivision), EventHelper.CHAMPIONSHIP_LABEL);

        Event cmpFinals = mockEventType(EventType.CMP_FINALS);
        assertEquals(EventHelper.generateLabelForEvent(cmpFinals), EventHelper.CHAMPIONSHIP_LABEL);

        Event regional = mockRegularEvent(EventType.REGIONAL, 2015, 5);
        assertEquals(EventHelper.generateLabelForEvent(regional), "Week 5");

        Event district = mockRegularEvent(EventType.DISTRICT, 2015, 2);
        assertEquals(EventHelper.generateLabelForEvent(district), "Week 2");

        Event districtCmp = mockRegularEvent(EventType.DISTRICT_CMP, 2012, 1);
        assertEquals(EventHelper.generateLabelForEvent(districtCmp), "Week 1");

        /* Special cases for 2016 events & Week 0.5 */
        Event scmb = ModelMaker.getModel(Event.class, "2016scmb");
        assertEquals(EventHelper.generateLabelForEvent(scmb), "Week 0.5");
        Event regional2016 = mockRegularEvent(EventType.REGIONAL, 2016, 2);
        assertEquals(EventHelper.generateLabelForEvent(regional2016), "Week 1");

        Event district2016 = mockRegularEvent(EventType.DISTRICT, 2016, 4);
        assertEquals(EventHelper.generateLabelForEvent(district2016), "Week 3");

        Event districtCmp2016 = mockRegularEvent(EventType.DISTRICT_CMP, 2016, 7);
        assertEquals(EventHelper.generateLabelForEvent(districtCmp2016), "Week 6");
    }

    @Test
    public void testWeekLabelFromNumber() {
        assertEquals(EventHelper.weekLabelFromNum(2015, -1), EventHelper.PRESEASON_LABEL);
        assertEquals(EventHelper.weekLabelFromNum(2015, 4), "Week 4");
        assertEquals(EventHelper.weekLabelFromNum(2015, 9), EventHelper.CHAMPIONSHIP_LABEL);
        assertEquals(EventHelper.weekLabelFromNum(2015, 12), EventHelper.OFFSEASON_LABEL);

        assertEquals(EventHelper.weekLabelFromNum(2016, -1), EventHelper.PRESEASON_LABEL);
        assertEquals(EventHelper.weekLabelFromNum(2016, 1), "Week 0.5");
        assertEquals(EventHelper.weekLabelFromNum(2016, 4), "Week 3");
        assertEquals(EventHelper.weekLabelFromNum(2016, 10), EventHelper.CHAMPIONSHIP_LABEL);
        assertEquals(EventHelper.weekLabelFromNum(2016, 12), EventHelper.OFFSEASON_LABEL);
    }

    @Test
    public void testGetEventCode() {
        assertEquals("CTHAR", EventHelper.getEventCode("2015cthar"));
        assertEquals("NYNY", EventHelper.getEventCode("2015NYNY"));
        assertEquals("DCVA", EventHelper.getEventCode("2015dcva_f1m1"));
        assertEquals("CTWAT", EventHelper.getEventCode("2015ctwat_frc1124"));
    }

    private static Event mockEventType(EventType type)  {
        Event event = mock(Event.class);
        when(event.getEventTypeEnum()).thenReturn(type);
        return event;
    }

    private static Event mockRegularEvent(EventType type, int year, int week)  {
        Event event = mockEventType(type);
        when(event.getYear()).thenReturn(year);
        when(event.getWeek()).thenReturn(week);
        return event;
    }
}
