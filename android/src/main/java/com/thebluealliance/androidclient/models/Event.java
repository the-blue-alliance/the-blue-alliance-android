package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class Event extends BasicModel<Event> {

    private String shortName;
    private JsonArray matches, alliances, rankings, webcasts, teams;
    private JsonObject stats;

    public Event() {
        super(Database.TABLE_EVENTS);
        shortName = "";
        alliances = null;
        rankings = null;
        webcasts = null;
        stats = null;
    }

    public JsonArray getAlliances() throws FieldNotDefinedException{
        if(alliances != null){
            return alliances;
        }
        if(fields.containsKey(Database.Events.ALLIANCES) && fields.get(Database.Events.ALLIANCES) instanceof String) {
            alliances = JSONManager.getasJsonArray((String) fields.get(Database.Events.ALLIANCES));
            return alliances;
        }
        throw new FieldNotDefinedException("Field Database.Events.ALLIANCES is not defined");
    }

    public void setAlliances(JsonArray alliances) {
        fields.put(Database.Events.ALLIANCES, alliances.toString());
        this.alliances = alliances;
    }

    public void setAlliances(String allianceJson){
        fields.put(Database.Events.ALLIANCES, allianceJson);
    }

    public String getWebsite(){
        if(fields.containsKey(Database.Events.WEBSITE) && fields.get(Database.Events.WEBSITE) instanceof String) {
            return (String) fields.get(Database.Events.ALLIANCES);
        }
        return "";
    }

    public int getEventYear() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Events.YEAR) && fields.get(Database.Events.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Events.YEAR);
        }else{
            try{
                return Integer.parseInt(getEventKey().substring(0,4));
            }catch(FieldNotDefinedException e){
                throw new FieldNotDefinedException("Field Database.Events.YEAR is not defined");
            }
        }
    }

    public void setWebsite(String website) {
        fields.put(Database.Events.WEBSITE, website);
    }

    public JsonArray getRankings() throws FieldNotDefinedException{
        if(rankings != null){
            return rankings;
        }
        if(fields.containsKey(Database.Events.RANKINGS) && fields.get(Database.Events.RANKINGS) instanceof String) {
            rankings = JSONManager.getasJsonArray((String) fields.get(Database.Events.RANKINGS));
            return rankings;
        }
        throw new FieldNotDefinedException("Field Database.Events.RANKINGS is not defined");
    }

    public void setRankings(JsonArray rankings) {
        fields.put(Database.Events.RANKINGS, rankings.toString());
        this.rankings = rankings;
    }

    public void setRankings(String rankingsJson){
        fields.put(Database.Events.RANKINGS, rankingsJson);
    }

    public JsonArray getWebcasts() throws FieldNotDefinedException{
        if(webcasts != null){
            return webcasts;
        }
        if(fields.containsKey(Database.Events.WEBCASTS) && fields.get(Database.Events.WEBCASTS) instanceof String) {
            webcasts = JSONManager.getasJsonArray((String) fields.get(Database.Events.WEBCASTS));
            return webcasts;
        }
        throw new FieldNotDefinedException("Field Database.Events.WEBCASTS is not defined");
    }

    public void setWebcasts(JsonArray webcasts) {
        fields.put(Database.Events.WEBCASTS, webcasts.toString());
        this.webcasts = webcasts;
    }

    public void setWebcasts(String webcastJson){
        fields.put(Database.Events.WEBCASTS, webcastJson);
    }

    public JsonObject getStats() throws FieldNotDefinedException{
        if(stats != null){
            return stats;
        }
        if(fields.containsKey(Database.Events.STATS) && fields.get(Database.Events.STATS) instanceof String) {
            stats = JSONManager.getasJsonObject((String) fields.get(Database.Events.STATS));
            return stats;
        }
        throw new FieldNotDefinedException("Field Database.Events.STATS is not defined");
    }

    public void setStats(JsonObject stats) {
        fields.put(Database.Events.STATS, stats.toString());
        this.stats = stats;
    }

    public void setStats(String statsJson){
        fields.put(Database.Events.STATS, statsJson);
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
        if(fields.containsKey(Database.Events.DISTRICT) && fields.get(Database.Events.DISTRICT) instanceof Integer) {
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
            Date start = EventHelper.eventDateFormat.parse(startString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            fields.put(Database.Events.START, start.getTime());
            int week = Integer.parseInt(EventHelper.weekFormat.format(start)) - Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
            setCompetitionWeek(week);
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
        //all preseason events are week 0
        fields.put(Database.Events.WEEK, Math.max(0, week));
    }

    public JsonArray getMatches() {
        return matches;
    }

    /**
     * Sets matches associated with this event model.
     * Be careful! Matches aren't an official property, this method just exists as a continence.
     * This model <b>WILL NOT</b> store the matches you set here.
     * @param matches Match models to be temporarily associated with this event model.
     */
    public void setMatches(JsonArray matches) {
        this.matches = matches;
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
            if(shortName == null || shortName.isEmpty()) {
                setShortName(EventHelper.getShortNameForEvent(getEventName(), getEventType()));
            }
            return shortName;
        } catch (FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Missing fields for short name.");
            return "";
        }
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTeams(JsonArray teams) {
        fields.put(Database.Events.TEAMS, teams.toString());
        this.teams = teams;
    }

    public void setTeams(String teamsJson){
        fields.put(Database.Events.TEAMS, teamsJson);
    }

    public JsonArray getTeams() throws FieldNotDefinedException {
        if(teams != null){
            return teams;
        }
        if(fields.containsKey(Database.Events.TEAMS) && fields.get(Database.Events.TEAMS) instanceof String) {
            teams = JSONManager.getasJsonArray((String) fields.get(Database.Events.TEAMS));
            return teams;
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
    public EventListElement render() {
        try {
            String eventKey = getEventKey(),
                    eventName = getShortName(),
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

    public static synchronized APIResponse<Event> query(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying events table: "+whereClause+ Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_EVENTS, fields, whereClause, whereArgs, null, null, null, null);
        Event event;
        if(cursor != null && cursor.moveToFirst()){
            event = ModelInflater.inflateEvent(cursor);
        }else{
            event = new Event();
        }

        APIResponse.CODE code = forceFromCache?APIResponse.CODE.LOCAL: APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Event updatedEvent;
                if(StringUtils.countMatches(url, "/") == 6) {
                    /* event info request - inflate the event
                     * any other request will have an additional '/' in the URL
                     * anybody got a better way to determine this?
                     */
                    updatedEvent = JSONManager.getGson().fromJson(response.getData(), Event.class);
                }else{
                    /* We're getting one of the other endpoints which don't contain event data.
                     * Add them to the model based on which URL we hit
                     */
                    updatedEvent = new Event();
                    EventHelper.addFieldByAPIUrl(updatedEvent, url, response.getData());
                }
                event.merge(updatedEvent);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            event.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? "+changed);
        return new APIResponse<>(event, code);
    }

    public static synchronized APIResponse<ArrayList<Event>> queryList(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying events table: "+whereClause+ Arrays.toString(whereArgs));
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_EVENTS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Event> events = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()){
            do{
                events.add(ModelInflater.inflateEvent(cursor));
            }while(cursor.moveToNext());
        }

        APIResponse.CODE code = forceFromCache?APIResponse.CODE.LOCAL: APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray matchList = JSONManager.getasJsonArray(response.getData());
                events = new ArrayList<>();
                for(JsonElement m: matchList){
                    events.add(JSONManager.getGson().fromJson(m, Event.class));
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            Database.getInstance(c).getEventsTable().storeEvents(events);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found "+events.size()+" events, updated in db? "+changed);
        return new APIResponse<>(events, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getEventsTable().add(this);
    }
}
