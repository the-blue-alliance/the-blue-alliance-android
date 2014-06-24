package com.thebluealliance.androidclient.models;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Event extends BasicModel<Event> {

    private String shortName;

    public Event() {
        super(Database.TABLE_EVENTS);
        shortName = "";
    }

    public JsonArray getAlliances() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.ALLIANCES) && fields.get(Database.Events.ALLIANCES) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Events.ALLIANCES));
        }
        throw new FieldNotDefinedException("Field Database.Events.ALLIANCES is not defined");
    }

    public void setAlliances(JsonArray alliances) {
        fields.put(Database.Events.ALLIANCES, alliances.toString());
    }

    public String getWebsite() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.WEBSITE) && fields.get(Database.Events.WEBSITE) instanceof String) {
            return (String) fields.get(Database.Events.ALLIANCES);
        }
        throw new FieldNotDefinedException("Field Database.Events.WEBSITE is not defined");
    }

    public int getEventYear() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.YEAR) && fields.get(Database.Events.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Events.YEAR);
        }
        throw new FieldNotDefinedException("Field Database.Events.YEAR is not defined");
    }

    public void setWebsite(String website) {
        fields.put(Database.Events.WEBSITE, website);
    }

    public JsonArray getRankings() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.RANKINGS) && fields.get(Database.Events.RANKINGS) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Events.RANKINGS));
        }
        throw new FieldNotDefinedException("Field Database.Events.RANKINGS is not defined");
    }

    public JsonArray getMatches() {
        //TODO implement
        Database.getInstance().getMatchesForEventQuery();
    }

    public void setRankings(JsonArray rankings) {
        fields.put(Database.Events.RANKINGS, rankings.toString());
    }

    public JsonArray getWebcasts() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.WEBCASTS) && fields.get(Database.Events.WEBCASTS) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Events.WEBCASTS));
        }
        throw new FieldNotDefinedException("Field Database.Events.WEBCASTS is not defined");
    }

    public void setWebcasts(JsonArray webcasts) {
        fields.put(Database.Events.WEBCASTS, webcasts.toString());
    }

    public JsonObject getStats() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.STATS) && fields.get(Database.Events.STATS) instanceof String) {
            return JSONManager.getasJsonObject((String) fields.get(Database.Events.STATS));
        }
        throw new FieldNotDefinedException("Field Database.Events.STATS is not defined");
    }

    public void setStats(JsonObject stats) {
        fields.put(Database.Events.STATS, stats.toString());
    }


    public String getEventKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.KEY) && fields.get(Database.Events.KEY) instanceof String) {
            return (String) fields.get(Database.Events.KEY);
        }
        throw new FieldNotDefinedException("Field Database.Events.KEY is not defined");
    }

    /**
     * Gets the event key with the year stripped out.
     *
     * @return Event key without the year
     */
    public String getYearAgnosticEventKey() throws FieldNotDefinedException {
        return getEventKey().replaceAll("[0-9]", "");
    }

    public void setEventKey(String eventKey) {
        if (!EventHelper.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid event key: " + eventKey + " Should be format <year><event>, like 2014cthar");
        fields.put(Database.Events.KEY, eventKey);
        fields.put(Database.Events.YEAR, Integer.parseInt(eventKey.substring(0, 4)));
    }

    public String getEventName() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.NAME) && fields.get(Database.Events.NAME) instanceof String) {
            return (String) fields.get(Database.Events.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Events.NAME is not defined");
    }

    public void setEventName(String eventName) {
        fields.put(Database.Events.NAME, eventName);
    }

    public String getLocation() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.LOCATION) && fields.get(Database.Events.LOCATION) instanceof String) {
            return (String) fields.get(Database.Events.LOCATION);
        }
        throw new FieldNotDefinedException("Field Database.Events.LOCATION is not defined");
    }

    public void setLocation(String location) {
        fields.put(Database.Events.LOCATION, location);
    }

    public String getVenue() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.VENUE) && fields.get(Database.Events.VENUE) instanceof String) {
            return (String) fields.get(Database.Events.VENUE);
        }
        throw new FieldNotDefinedException("Field Database.Events.VENUE is not defined");
    }

    public void setVenue(String venue) {
        fields.put(Database.Events.VENUE, venue);
    }

    public EventHelper.TYPE getEventType() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.TYPE) && fields.get(Database.Events.TYPE) instanceof Integer) {
            return EventHelper.TYPE.fromInt((Integer) fields.get(Database.Events.TYPE));
        }
        throw new FieldNotDefinedException("Field Database.Events.TYPE is not defined");
    }

    public void setEventType(EventHelper.TYPE eventType) {
        fields.put(Database.Events.TYPE, eventType.ordinal());
    }

    public void setEventType(String typeString) {
        fields.put(Database.Events.TYPE, EventHelper.TYPE.fromString(typeString).ordinal());
    }

    public void setEventType(int num) {
       fields.put(Database.Events.TYPE, num);
    }

    public int getDistrictEnum() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.DISTRICT) && fields.get(Database.Events.DISTRICT) instanceof String) {
            return (Integer) fields.get(Database.Events.DISTRICT);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT is not defined");
    }

    public void setDistrictEnum(int districtEnum) {
        fields.put(Database.Events.DISTRICT, districtEnum);
    }

    public String getDistrictTitle() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.DISTRICT_STRING) && fields.get(Database.Events.DISTRICT_STRING) instanceof String) {
            return (String) fields.get(Database.Events.DISTRICT_STRING);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT_STRING is not defined");
    }

    public void setDistrictTitle(String districtTitle) {
        fields.put(Database.Events.DISTRICT_STRING, districtTitle);
    }

    public Date getStartDate() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.START) && fields.get(Database.Events.START) instanceof Long) {
            return new Date((Long) fields.get(Database.Events.START));
        }
        throw new FieldNotDefinedException("Field Database.Events.START is not defined");
    }

    public void setStartDate(Date startDate) {
        fields.put(Database.Events.START, startDate.getTime());
    }

    public void setStartDate(String startString) {
        if (startString == null || startString.isEmpty()) {
            return;
        }
        try {
            fields.put(Database.Events.START, EventHelper.eventDateFormat.parse(startString).getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public Date getEndDate() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.END) && fields.get(Database.Events.END) instanceof Long) {
            return new Date((Long) fields.get(Database.Events.END));
        }
        throw new FieldNotDefinedException("Field Database.Events.END is not defined");
    }

    public void setEndDate(Date endDate) {
        fields.put(Database.Events.END, endDate.getTime());
    }

    public void setEndDate(String endString) {
        if (endString == null || endString.isEmpty()) {
            return;
        }
        try {
            fields.put(Database.Events.END, EventHelper.eventDateFormat.parse(endString).getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.WEEK) && fields.get(Database.Events.WEEK) instanceof Integer) {
            return (Integer) fields.get(Database.Events.WEEK);
        }
        throw new FieldNotDefinedException("Field Database.Events.WEEK is not defined");
    }

    public void setCompetitionWeek(int week){
        fields.put(Database.Events.WEEK, week);
    }

    public boolean isHappeningNow() {
        try {
            Date startDate = getStartDate(),
                    endDate = getEndDate();
            if (startDate == null || endDate == null) return false;
            Date now = new Date();
            return now.after(startDate) && now.before(endDate);
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields to determine if event is happening now.\n" +
                    "Required fields: Database.Events.START and Database.Events.END");
            return false;
        }
    }

    public boolean hasStarted() {
        try {
            Date startDate = getStartDate();
            if (startDate == null) return false;
            Date now = new Date();
            return now.after(startDate);
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields to determine if event has started.\n" +
                    "Required fields: Database.Events.START");
            return false;
        }
    }

    public boolean isOfficial() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.OFFICIAL) && fields.get(Database.Events.OFFICIAL) instanceof Integer) {
            return (Integer) fields.get(Database.Events.OFFICIAL) == 1;
        }
        throw new FieldNotDefinedException("Field Database.Events.OFFICIAL is not defined");
    }

    public void setOfficial(boolean official) {
        fields.put(Database.Events.OFFICIAL, official?1:0);
    }

    public String getShortName() {
        try {
            EventHelper.TYPE eventType = getEventType();
            String eventName = getEventName();

            // Preseason and offseason events will probably fail our regex matcher
            if (eventType == EventHelper.TYPE.PRESEASON || eventType == EventHelper.TYPE.OFFSEASON) {
                return eventName;
            }
            if (shortName.isEmpty()) {
                Pattern regexPattern = Pattern.compile("(MAR |PNW )?(FIRST Robotics|FRC)?(.*)( FIRST Robotics| FRC)?( District| Regional| Region| State| Tournament| FRC| Field| Division)( Competition| Event| Championship)?( sponsored by.*)?");
                Matcher m = regexPattern.matcher(eventName);
                if (m.matches()) {
                    String s = m.group(3);
                    regexPattern = Pattern.compile("(.*)(FIRST Robotics|FRC)");
                    m = regexPattern.matcher(s);
                    if (m.matches()) {
                        shortName = m.group(1).trim();
                    } else {
                        shortName = s.trim();
                    }
                }
            }
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for determining short name:\n" +
                    "Database.Events.NAME, Database.Events.NAME");
            return null;
        }
        return shortName;

    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTeams(JsonArray teams) {
        fields.put(Database.Events.TEAMS, teams.toString());
    }

    public JsonArray getTeams() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Events.TEAMS) && fields.get(Database.Events.TEAMS) instanceof String) {
            return JSONManager.getasJsonArray((String) fields.get(Database.Events.TEAMS));
        }
        throw new FieldNotDefinedException("Field Database.Events.TEAMS is not defined");
    }

    public String getDateString() {
        try {
            Date startDate = getStartDate(),
                    endDate = getEndDate();
            if (startDate == null || endDate == null) return "";
            if (startDate.equals(endDate)) {
                return EventHelper.renderDateFormat.format(startDate);
            }
            return EventHelper.shortRenderDateFormat.format(startDate) + " to " + EventHelper.renderDateFormat.format(endDate);
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for getting date string. \n" +
                    "Required fields: Database.Events.START, Database.Events.END");
            return null;
        }
    }

    @Override
    public void addFields(String... fields) {

    }

    @Override
    public EventListElement render() {
        try {
            String eventKey = getEventKey(),
                    eventName = getEventName(),
                    location = getLocation();
            if (getShortName() == null || shortName.isEmpty()) {
                return new EventListElement(eventKey, eventName, getDateString(), location);
            } else {
                return new EventListElement(eventKey, getShortName(), getDateString(), location);
            }
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for rendering event\n" +
                    "Required fields: Database.Events.KEY, Database.Events.NAME, Database.Events.LOCATION");
            return null;
        }
    }

    public ArrayList<AllianceListElement> renderAlliances() {
        try {
            JsonArray alliances = getAlliances();
            ArrayList<AllianceListElement> output = new ArrayList<>();
            int counter = 1;
            for (JsonElement alliance : alliances) {
                JsonArray teams = alliance.getAsJsonObject().get("picks").getAsJsonArray();
                output.add(new AllianceListElement(counter, teams));
                counter++;
            }
            return output;
        }catch (FieldNotDefinedException e){
            Log.w(Constants.LOG_TAG, "Missing fields for rendering alliances.\n" +
                    "Required field: Database.Events.ALLIANCES");
            return null;
        }
    }

    public String getSearchTitles() throws FieldNotDefinedException {
        return getEventKey() + "," + getEventYear() + " " + getEventName() + "," + getEventYear() + " " + getShortName() + "," + getYearAgnosticEventKey() + " " + getEventYear();
    }

    @Override
    public ContentValues getParams() {
        return fields;
    }

}
