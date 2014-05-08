package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datatypes.EventListElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        public String toString(){
            switch(ordinal()){
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

    public static final DateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.ENGLISH);
    public static final SimpleDateFormat renderDateFormat = new SimpleDateFormat("MMM d, yyyy"),
                                         shortRenderDateFormat = new SimpleDateFormat("MMM d"),
                                         weekFormat = new SimpleDateFormat("w");

    String 		eventKey,
                eventName,
                location,
                shortName,
                abbreviation,
                website;
    TYPE		eventType;
    DISTRICT	eventDistrict;
    Date		startDate,
                endDate;
    boolean		official;
    long		last_updated;
	JsonArray 	rankings,
                webcasts,
                teams,
                matches;
    JsonObject	stats;

    public Event() {
        this.eventKey = "";
        this.eventName = "";
        this.shortName = "";
        this.abbreviation = "";
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
        if(!Event.validateEventKey(eventKey)) throw new IllegalArgumentException("Invalid event key: "+eventKey+" Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
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

    public ArrayList<Match> getMatchList(){
        ArrayList<Match> matches = new ArrayList<>();
        if(matches == null) return matches;
        Iterator iterator = matches.iterator();
        while(iterator!= null){
            matches.add(JSONManager.getGson().fromJson((JsonObject)(iterator.next()),Match.class));
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


    public static boolean validateEventKey(String key){
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+$");
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        if(!Event.validateEventKey(eventKey)) throw new IllegalArgumentException("Invalid event key: "+eventKey+" Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
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
        try {
            this.endDate = eventDateFormat.parse(endString);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek(){
        if(startDate == null) return -1;
        int week = Integer.parseInt(weekFormat.format(startDate))-8;
        return week<0?0:week;
    }

    public boolean isHappeningNow(){
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
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

    public String getDateString(){
        return shortRenderDateFormat.format(startDate) + " to " + renderDateFormat.format(endDate);
    }

    public int getWeek(){
        if(startDate == null) return -1;
        int week = Integer.parseInt(weekFormat.format(startDate))-8;
        return week<0?0:week;
    }

    @Override
    public EventListElement render() {
        return new EventListElement(eventKey, eventName, getDateString() , location);
    }

    @Override
    public ContentValues getParams() {
        ContentValues values = new ContentValues();
        values.put(Database.Events.KEY,eventKey);
        values.put(Database.Events.NAME,eventName);
        values.put(Database.Events.LOCATION,location);
        values.put(Database.Events.TYPE,eventType.ordinal());
        values.put(Database.Events.DISTRICT,eventDistrict.ordinal());
        values.put(Database.Events.START,eventDateFormat.format(startDate));
        values.put(Database.Events.END,eventDateFormat.format(endDate));
        values.put(Database.Events.OFFICIAL,official?1:0);
        values.put(Database.Events.WEEK,getWeek());

        return values;
    }
}
