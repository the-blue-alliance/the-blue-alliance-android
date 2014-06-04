package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datatypes.EventListElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class Event implements BasicModel {

    /* Do not insert any new entries above the existing enums!!!
     * Things depend on their ordinal values, so you can only to the bottom of the list
     */
    public static enum TYPE {
        NONE,
        REGIONAL,
        DISTRICT,
        DISTRICT_CMP,
        CMP_DIVISION,
        CMP_FINALS,
        OFFSEASON,
        PRESEASON;

        public String toString() {
            switch (ordinal()) {
                default:
                case 0:
                    return "";
                case 1:
                    return "Regional Events";
                case 2:
                    return "District Events";
                case 3:
                    return "District Championship";
                case 4:
                    return "Championship Divisions";
                case 5:
                    return "Championship Finals";
                case 6:
                    return "Offseason Events";
                case 7:
                    return "Preseason Events";
            }
        }

        public static TYPE fromString(String str) {
            switch (str) {
                case "Regional":
                    return REGIONAL;
                case "District":
                    return DISTRICT;
                case "District Championship":
                    return DISTRICT_CMP;
                case "Championship Division":
                    return CMP_DIVISION;
                case "Championship Finals":
                    return CMP_FINALS;
                case "Offseason":
                    return OFFSEASON;
                case "Preseason":
                    return PRESEASON;
                default:
                    return NONE;
            }
        }

        public static TYPE fromInt(int num) {
            switch (num) {
                case 0:
                    return REGIONAL;
                case 1:
                    return DISTRICT;
                case 2:
                    return DISTRICT_CMP;
                case 3:
                    return CMP_DIVISION;
                case 4:
                    return CMP_FINALS;
                case 99:
                    return OFFSEASON;
                case 100:
                    return PRESEASON;
                default:
                    return NONE;
            }
        }

        public static TYPE fromLabel(String label) {
            switch (label) {
                case OFFSEASON_LABEL:
                    return OFFSEASON;
                case PRESEASON_LABEL:
                    return PRESEASON;
                case CHAMPIONSHIP_LABEL:
                    return CMP_DIVISION;
                case WEEKLESS_LABEL:
                    return NONE;
                default:
                    return REGIONAL;
            }
        }
    }

    public static enum DISTRICT {
        NONE,
        FIM,  /* Michigan */
        MAR,  /* Mid Atlantic */
        NE,   /* New England */
        PNW;  /* Pacific Northwest */

        public static DISTRICT fromString(String str) {
            /*
             * Not implemented on TBA yet. Write it here whenever it is...
			 */
            return NONE;
        }
    }

    public static final DateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
    public static final SimpleDateFormat renderDateFormat = new SimpleDateFormat("MMM d, yyyy"),
            shortRenderDateFormat = new SimpleDateFormat("MMM d"),
            weekFormat = new SimpleDateFormat("w");

    public static final String CHAMPIONSHIP_LABEL = "Championship Event",
            REGIONAL_LABEL = "Week %d",
            WEEKLESS_LABEL = "Other Official Events",
            OFFSEASON_LABEL = "Offseason Events",
            PRESEASON_LABEL = "Preseason Events";

    String eventKey,
            eventName,
            location,
            shortName,
            abbreviation,
            website;
    TYPE eventType;
    DISTRICT eventDistrict;
    Date startDate,
            endDate;
    boolean official;
    long last_updated;
    JsonArray rankings,
            webcasts,
            teams,
            matches;
    JsonObject stats;
    int eventYear;

    public Event() {
        this.eventKey = "";
        this.eventName = "";
        this.shortName = "";
        this.abbreviation = "";
        this.eventYear = -1;
        this.location = "";
        this.eventType = TYPE.NONE;
        this.eventDistrict = DISTRICT.NONE;
        this.startDate = new Date(0);
        this.endDate = new Date(0);
        this.official = false;
        this.last_updated = -1;
        website = "";
        rankings = new JsonArray();
        webcasts = new JsonArray();
        teams = new JsonArray();
        stats = new JsonObject();
    }

    public Event(String eventKey, String eventName, String shortName, String abbreviation, String location, boolean official, TYPE eventType, DISTRICT eventDistrict, Date startDate, Date endDate,
                 String website, JsonArray teams, JsonArray rankings, JsonArray webcasts, JsonObject stats, long last_updated) {
        if (!Event.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid event key: " + eventKey + " Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
        this.eventYear = Integer.parseInt(eventKey.substring(0, 4));
        this.eventName = eventName;
        this.shortName = shortName;
        this.abbreviation = abbreviation;
        this.location = location;
        this.eventType = eventType;
        this.eventDistrict = eventDistrict;
        this.startDate = startDate;
        this.endDate = endDate;
        this.official = official;
        this.last_updated = last_updated;
        this.website = website;
        this.rankings = rankings;
        this.webcasts = webcasts;
        this.stats = stats;
        this.teams = teams;
    }

    public String getWebsite() {
        return website;
    }

    public int getEventYear() {
        return eventYear;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public JsonArray getRankings() {
        return rankings;
    }

    public JsonArray getMatches() {
        return matches;
    }

    public void setMatches(JsonArray matches) {
        this.matches = matches;
    }

    public ArrayList<Match> getMatchList() {
        ArrayList<Match> matches = new ArrayList<>();
        if (matches == null) return matches;
        Iterator iterator = matches.iterator();
        while (iterator != null) {
            matches.add(JSONManager.getGson().fromJson((JsonObject) (iterator.next()), Match.class));
        }
        return matches;
    }

    public void setRankings(JsonArray rankings) {
        this.rankings = rankings;
    }

    public JsonArray getWebcasts() {
        return webcasts;
    }

    public void setWebcasts(JsonArray webcasts) {
        this.webcasts = webcasts;
    }

    public JsonObject getStats() {
        return stats;
    }

    public void setStats(JsonObject stats) {
        this.stats = stats;
    }


    public static boolean validateEventKey(String key) {
        if(key == null || key.isEmpty()) return false;
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+$");
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        if (!Event.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid event key: " + eventKey + " Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
        this.eventYear = Integer.parseInt(eventKey.substring(0, 4));
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TYPE getEventType() {
        return eventType;
    }

    public void setEventType(TYPE eventType) {
        this.eventType = eventType;
    }

    public void setEventType(String typeString) {
        this.eventType = TYPE.fromString(typeString);
    }

    public void setEventType(int num) {
        this.eventType = TYPE.fromInt(num);
    }

    public DISTRICT getEventDistrict() {
        return eventDistrict;
    }

    public void setEventDistrict(DISTRICT eventDistrict) {
        this.eventDistrict = eventDistrict;
    }

    public void setEventDistrict(String districtString) {
        this.eventDistrict = DISTRICT.fromString(districtString);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String startString) {
        if (startString == null || startString.isEmpty()) {
            startDate = null;
            return;
        }
        try {
            this.startDate = eventDateFormat.parse(startString);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(String endString) {
        if (endString == null || endString.isEmpty()) {
            endDate = null;
            return;
        }
        try {
            this.endDate = eventDateFormat.parse(endString);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek() {
        if (startDate == null) return -1;
        int week = Integer.parseInt(weekFormat.format(startDate)) - Utilities.getFirstCompWeek(eventYear);
        return week < 0 ? 0 : week;
    }

    public static int competitionWeek(Date date) {
        if (date == null) return -1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = Integer.parseInt(weekFormat.format(date)) - Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
        return week < 0 ? 0 : week;
    }

    public boolean isHappeningNow() {
        if (startDate == null || endDate == null) return false;
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }

    public boolean hasStarted() {
        if (startDate == null) return false;
        Date now = new Date();
        return now.after(startDate);
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getShortName() {
        if(shortName.isEmpty()){
            String[] match = shortName.split("(MAR |PNW )?(FIRST Robotics|FRC)?(.*)(FIRST Robotics|FRC)?(District|Regional|Region|State|Tournament|FRC|Field)( Competition| Event| Championship)?");
            if(match.length > 0){
                String s = match[3];
                match = s.split("(.*)(FIRST Robotics|FRC)");
                if(match.length > 0){
                    shortName = match[1].trim();
                }else{
                    shortName = s.trim();
                }
            }
        }

        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTeams(JsonArray teams) {
        this.teams = teams;
    }

    public JsonArray getTeams() {
        return teams;
    }

    public long getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(long last_updated) {
        this.last_updated = last_updated;
    }

    public String getDateString() {
        if (startDate == null || endDate == null) return "";
        if (startDate.equals(endDate)) {
            return renderDateFormat.format(startDate);
        }
        return shortRenderDateFormat.format(startDate) + " to " + renderDateFormat.format(endDate);
    }

    @Override
    public EventListElement render() {
        return new EventListElement(eventKey, eventName, getDateString(), location);
    }

    @Override
    public ContentValues getParams() {
        ContentValues values = new ContentValues();
        values.put(Database.Events.KEY, eventKey);
        values.put(Database.Events.NAME, eventName);
        values.put(Database.Events.LOCATION, location);
        values.put(Database.Events.TYPE, eventType.ordinal());
        values.put(Database.Events.DISTRICT, eventDistrict.ordinal());
        values.put(Database.Events.START, eventDateFormat.format(startDate));
        values.put(Database.Events.END, eventDateFormat.format(endDate));
        values.put(Database.Events.OFFICIAL, official ? 1 : 0);
        values.put(Database.Events.WEEK, getCompetitionWeek());

        return values;
    }

    public static int getEventOrder(TYPE eventType) {
        switch (eventType) {
            default:
            case NONE:
                return 99;
            case REGIONAL:
            case DISTRICT:
                return 2;
            case DISTRICT_CMP:
                return 3;
            case CMP_DIVISION:
                return 4;
            case CMP_FINALS:
                return 5;
            case OFFSEASON:
                return 6;
            case PRESEASON:
                return 1;
        }
    }

    public static String generateLabelForEvent(Event e) {
        switch (e.getEventType()) {
            case CMP_DIVISION:
            case CMP_FINALS:
                return CHAMPIONSHIP_LABEL;
            case REGIONAL:
            case DISTRICT:
            case DISTRICT_CMP:
                return String.format(REGIONAL_LABEL, e.getCompetitionWeek());
            case OFFSEASON:
                return OFFSEASON_LABEL;
            case PRESEASON:
                return PRESEASON_LABEL;
            default:
                return WEEKLESS_LABEL;
        }
    }

    public static String weekLabelFromNum(int year, int weekNum) {

        if (weekNum <= 0) {
            return PRESEASON_LABEL;
        }

        //let's find the week of CMP and base everything else off that
        //there should always be something in the CMP set for every year
        int cmpWeek = Utilities.getCmpWeek(year);

        if (weekNum > 0 && weekNum < cmpWeek) {
            return String.format(REGIONAL_LABEL, weekNum);
        }
        if (weekNum == cmpWeek) {
            return CHAMPIONSHIP_LABEL;
        }
        if (weekNum > cmpWeek) {
            return OFFSEASON_LABEL;
        }
        return WEEKLESS_LABEL;
    }

    public static int weekNumFromLabel(HashMap<String, ArrayList<SimpleEvent>> groupedEvents, String label) {
        if (groupedEvents.containsKey(label)) {
            SimpleEvent e = groupedEvents.get(label).get(0);
            return e.getCompetitionWeek();
        } else {
            return -1;
        }
    }
}
