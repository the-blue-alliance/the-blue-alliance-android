package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


public class Event implements BasicModel {

    String eventKey,
            eventName,
            location,
            venue,
            shortName,
            abbreviation,
            website;
    EventHelper.TYPE eventType;
    int districtEnum;
    String districtTitle;
    Date startDate,
            endDate;
    boolean official;
    long last_updated;
    JsonArray rankings,
            webcasts,
            teams,
            matches,
            alliances;
    JsonObject stats;
    int eventYear;

    public Event() {
        this.eventKey = "";
        this.eventName = "";
        this.shortName = "";
        this.abbreviation = "";
        this.eventYear = -1;
        this.location = "";
        this.venue = "";
        this.eventType = EventHelper.TYPE.NONE;
        this.districtEnum = 0;
        this.districtTitle = "";
        this.startDate = new Date(0);
        this.endDate = new Date(0);
        this.official = false;
        this.last_updated = -1;
        website = "";
        rankings = new JsonArray();
        webcasts = new JsonArray();
        teams = new JsonArray();
        stats = new JsonObject();
        alliances = new JsonArray();
    }

    public Event(String eventKey, String eventName, String shortName, String abbreviation, String location, String venue, boolean official, EventHelper.TYPE eventType, int districtEnum, String districtTitle, Date startDate, Date endDate,
                 String website, JsonArray teams, JsonArray rankings, JsonArray webcasts, JsonObject stats, JsonArray alliances, long last_updated) {
        if (!EventHelper.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid event key: " + eventKey + " Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
        this.eventYear = Integer.parseInt(eventKey.substring(0, 4));
        this.eventName = eventName;
        this.shortName = shortName;
        this.abbreviation = abbreviation;
        this.location = location;
        this.venue = venue;
        this.eventType = eventType;
        this.districtTitle = districtTitle;
        this.districtEnum = districtEnum;
        this.startDate = startDate;
        this.endDate = endDate;
        this.official = official;
        this.last_updated = last_updated;
        this.website = website;
        this.rankings = rankings;
        this.webcasts = webcasts;
        this.stats = stats;
        this.teams = teams;
        this.alliances = alliances;
    }

    public JsonArray getAlliances() {
        return alliances;
    }

    public void setAlliances(JsonArray alliances) {
        this.alliances = alliances;
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
        if (this.matches == null) return matches;
        for (JsonElement element : this.matches.getAsJsonArray()) {
            matches.add(JSONManager.getGson().fromJson(element, Match.class));
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


    public String getEventKey() {
        return eventKey;
    }

    /**
     * Gets the event key with the year stripped out.
     *
     * @return Event key without the year
     */
    public String getYearAgnosticEventKey() {
        return eventKey.replaceAll("[0-9]", "");
    }

    public void setEventKey(String eventKey) {
        if (!EventHelper.validateEventKey(eventKey))
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public EventHelper.TYPE getEventType() {
        return eventType;
    }

    public void setEventType(EventHelper.TYPE eventType) {
        this.eventType = eventType;
    }

    public void setEventType(String typeString) {
        this.eventType = EventHelper.TYPE.fromString(typeString);
    }

    public void setEventType(int num) {
        this.eventType = EventHelper.TYPE.fromInt(num);
    }

    public int getDistrictEnum() {
        return districtEnum;
    }

    public void setDistrictEnum(int districtEnum) {
        this.districtEnum = districtEnum;
    }

    public String getDistrictTitle() {
        return districtTitle;
    }

    public void setDistrictTitle(String districtTitle) {
        this.districtTitle = districtTitle;
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
            this.startDate = EventHelper.eventDateFormat.parse(startString);
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
            this.endDate = EventHelper.eventDateFormat.parse(endString);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek() {
        if (startDate == null) return -1;
        int week = Integer.parseInt(EventHelper.weekFormat.format(startDate)) - Utilities.getFirstCompWeek(eventYear);
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
        return EventHelper.getShortNameForEvent(eventName, eventType);
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
            return EventHelper.renderDateFormat.format(startDate);
        }
        return EventHelper.shortRenderDateFormat.format(startDate) + " to " + EventHelper.renderDateFormat.format(endDate);
    }

    @Override
    public EventListElement render() {
        return new EventListElement(eventKey, getShortName(), getDateString(), location);
    }

    public ArrayList<AllianceListElement> renderAlliances() {
        ArrayList<AllianceListElement> output = new ArrayList<>();
        int counter = 1;
        for (JsonElement alliance : alliances) {
            JsonArray teams = alliance.getAsJsonObject().get("picks").getAsJsonArray();
            output.add(new AllianceListElement(counter, teams));
            counter++;
        }
        return output;
    }

    public String getSearchTitles() {
        return eventKey + "," + eventYear + " " + eventName + "," + eventYear + " " + getShortName() + "," + getYearAgnosticEventKey() + " " + eventYear;
    }

    @Override
    public ContentValues getParams() {
        ContentValues values = new ContentValues();
        values.put(Database.Events.KEY, eventKey);
        values.put(Database.Events.NAME, eventName);
        values.put(Database.Events.LOCATION, location);
        values.put(Database.Events.TYPE, eventType.ordinal());
        values.put(Database.Events.DISTRICT, districtEnum);
        values.put(Database.Events.DISTRICT_STRING, districtTitle);
        values.put(Database.Events.START, EventHelper.eventDateFormat.format(startDate));
        values.put(Database.Events.END, EventHelper.eventDateFormat.format(endDate));
        values.put(Database.Events.OFFICIAL, official ? 1 : 0);
        values.put(Database.Events.WEEK, getCompetitionWeek());

        return values;
    }

}
