package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Event extends BasicModel<Event> {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.LEVEL_STARTING,
            NotificationTypes.ALLIANCE_SELECTION,
            NotificationTypes.AWARDS,
            NotificationTypes.SCHEDULE_UPDATED,
            //NotificationTypes.FINAL_RESULTS
    };

    private JsonArray matches, alliances, rankings, webcasts, teams;
    private JsonObject stats, districtPoints;

    public Event() {
        super(Database.TABLE_EVENTS);
        alliances = null;
        rankings = null;
        webcasts = null;
        stats = null;
        districtPoints = null;
    }

    public JsonArray getAlliances() throws FieldNotDefinedException {
        if (alliances != null) {
            return alliances;
        }
        if (fields.containsKey(Database.Events.ALLIANCES) && fields.get(Database.Events.ALLIANCES) instanceof String) {
            alliances = JSONHelper.getasJsonArray((String) fields.get(Database.Events.ALLIANCES));
            return alliances;
        }
        throw new FieldNotDefinedException("Field Database.Events.ALLIANCES is not defined");
    }

    public void setAlliances(JsonArray alliances) {
        fields.put(Database.Events.ALLIANCES, alliances.toString());
        this.alliances = alliances;
    }

    public void setAlliances(String allianceJson) {
        fields.put(Database.Events.ALLIANCES, allianceJson);
    }

    public String getWebsite() {
        if (fields.containsKey(Database.Events.WEBSITE) && fields.get(Database.Events.WEBSITE) instanceof String) {
            return (String) fields.get(Database.Events.WEBSITE);
        }
        return "";
    }

    public int getEventYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.YEAR) && fields.get(Database.Events.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Events.YEAR);
        } else {
            return Integer.parseInt(getKey().substring(0, 4));
        }
    }

    public void setWebsite(String website) {
        fields.put(Database.Events.WEBSITE, website);
    }

    public JsonArray getRankings() throws FieldNotDefinedException {
        if (rankings != null) {
            return rankings;
        }
        if (fields.containsKey(Database.Events.RANKINGS) && fields.get(Database.Events.RANKINGS) instanceof String) {
            rankings = JSONHelper.getasJsonArray((String) fields.get(Database.Events.RANKINGS));
            return rankings;
        }
        throw new FieldNotDefinedException("Field Database.Events.RANKINGS is not defined");
    }

    public void setRankings(JsonArray rankings) {
        fields.put(Database.Events.RANKINGS, rankings.toString());
        this.rankings = rankings;
    }

    public void setRankings(String rankingsJson) {
        fields.put(Database.Events.RANKINGS, rankingsJson);
    }

    public JsonArray getWebcasts() throws FieldNotDefinedException {
        if (webcasts != null) {
            return webcasts;
        }
        if (fields.containsKey(Database.Events.WEBCASTS) && fields.get(Database.Events.WEBCASTS) instanceof String) {
            webcasts = JSONHelper.getasJsonArray((String) fields.get(Database.Events.WEBCASTS));
            return webcasts;
        }
        throw new FieldNotDefinedException("Field Database.Events.WEBCASTS is not defined");
    }

    public void setWebcasts(JsonArray webcasts) {
        fields.put(Database.Events.WEBCASTS, webcasts.toString());
        this.webcasts = webcasts;
    }

    public void setWebcasts(String webcastJson) {
        fields.put(Database.Events.WEBCASTS, webcastJson);
    }

    public JsonObject getStats() throws FieldNotDefinedException {
        if (stats != null) {
            return stats;
        }
        if (fields.containsKey(Database.Events.STATS) && fields.get(Database.Events.STATS) instanceof String) {
            stats = JSONHelper.getasJsonObject((String) fields.get(Database.Events.STATS));
            return stats;
        }
        throw new FieldNotDefinedException("Field Database.Events.STATS is not defined");
    }

    public void setStats(JsonObject stats) {
        fields.put(Database.Events.STATS, stats.toString());
        this.stats = stats;
    }

    public void setStats(String statsJson) {
        fields.put(Database.Events.STATS, statsJson);
    }


    @Override
    public String getKey() {
        if (fields.containsKey(Database.Events.KEY) && fields.get(Database.Events.KEY) instanceof String) {
            return (String) fields.get(Database.Events.KEY);
        }
        return "";
    }

    /**
     * Gets the event key with the year stripped out.
     *
     * @return Event key without the year
     */
    public String getYearAgnosticEventKey() throws FieldNotDefinedException {
        return getKey().replaceAll("[0-9]", "");
    }

    public void setEventKey(String eventKey) {
        if (!EventHelper.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid event key: " + eventKey + " Should be format <year><event>, like 2014cthar");
        fields.put(Database.Events.KEY, eventKey);
        fields.put(Database.Events.YEAR, Integer.parseInt(eventKey.substring(0, 4)));
    }

    public String getEventName() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.NAME) && fields.get(Database.Events.NAME) instanceof String) {
            return (String) fields.get(Database.Events.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Events.NAME is not defined");
    }

    public void setEventName(String eventName) {
        fields.put(Database.Events.NAME, eventName);
    }

    public String getEventShortName() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.SHORTNAME) && fields.get(Database.Events.SHORTNAME) instanceof String) {
            String shortName = (String) fields.get(Database.Events.SHORTNAME);
            if (shortName != null && !shortName.isEmpty()) {
                return shortName;
            }
        }
        return getEventName();
    }

    public void setEventShortName(String eventShortName) {
        fields.put(Database.Events.SHORTNAME, eventShortName);
    }

    public String getLocation() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.LOCATION) && fields.get(Database.Events.LOCATION) instanceof String) {
            return (String) fields.get(Database.Events.LOCATION);
        }
        throw new FieldNotDefinedException("Field Database.Events.LOCATION is not defined");
    }

    public void setLocation(String location) {
        fields.put(Database.Events.LOCATION, location);
    }

    public String getVenue() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.VENUE) && fields.get(Database.Events.VENUE) instanceof String) {
            return (String) fields.get(Database.Events.VENUE);
        }
        throw new FieldNotDefinedException("Field Database.Events.VENUE is not defined");
    }

    public void setVenue(String venue) {
        fields.put(Database.Events.VENUE, venue);
    }

    public EventHelper.TYPE getEventType() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.TYPE) && fields.get(Database.Events.TYPE) instanceof Integer) {
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
        if (fields.containsKey(Database.Events.DISTRICT) && fields.get(Database.Events.DISTRICT) instanceof Integer) {
            return (Integer) fields.get(Database.Events.DISTRICT);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT is not defined");
    }

    public void setDistrictEnum(int districtEnum) {
        fields.put(Database.Events.DISTRICT, districtEnum);
    }

    public String getDistrictTitle() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.DISTRICT_STRING) && fields.get(Database.Events.DISTRICT_STRING) instanceof String) {
            return (String) fields.get(Database.Events.DISTRICT_STRING);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT_STRING is not defined");
    }

    public void setDistrictTitle(String districtTitle) {
        fields.put(Database.Events.DISTRICT_STRING, districtTitle);
    }

    public JsonObject getDistrictPoints() throws FieldNotDefinedException {
        if (districtPoints != null) {
            return districtPoints;
        }
        if (fields.containsKey(Database.Events.DISTRICT_POINTS) && fields.get(Database.Events.DISTRICT_POINTS) instanceof String) {
            districtPoints = JSONHelper.getasJsonObject((String) fields.get(Database.Events.DISTRICT_POINTS));
            return districtPoints;
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT_POINTS is not defined");
    }

    public void setDistrictPoints(String districtPoints) {
        fields.put(Database.Events.DISTRICT_POINTS, districtPoints);
    }

    public Date getStartDate() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.START) && fields.get(Database.Events.START) instanceof Long) {
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
            Date start = ThreadSafeFormatters.parseEventDate(startString);
            fields.put(Database.Events.START, start.getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public void setCompetitionWeekFromStartDate() {
        Date start;
        try {
            start = getStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(start.getTime());
            int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
            int firstWeek = Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
            int week = eventWeek - firstWeek;
            setCompetitionWeek(week);
        } catch (FieldNotDefinedException e) {
            e.printStackTrace();
            Log.w(Constants.LOG_TAG, "Can't set week, no start date");
        }
    }

    public Date getEndDate() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.END) && fields.get(Database.Events.END) instanceof Long) {
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
            fields.put(
              Database.Events.END,
              ThreadSafeFormatters.parseEventDate(endString).getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.WEEK) && fields.get(Database.Events.WEEK) instanceof Integer) {
            return (Integer) fields.get(Database.Events.WEEK);
        }
        throw new FieldNotDefinedException("Field Database.Events.WEEK is not defined");
    }

    public void setCompetitionWeek(int week) {
        //all preseason events are week 0
        fields.put(Database.Events.WEEK, Math.max(0, week));
    }

    public JsonArray getMatches() {
        return matches;
    }

    /**
     * Sets matches associated with this event model. Be careful! Matches aren't an official
     * property, this method just exists as a continence. This model <b>WILL NOT</b> store the
     * matches you set here.
     *
     * @param matches Match models to be temporarily associated with this event model.
     */
    public void setMatches(JsonArray matches) {
        this.matches = matches;
    }

    public boolean isHappeningNow() {
        try {
            Date startDate = getStartDate(),
                    endDate = getEndDate();

            //since the Dates are at time 0:00, we need to add one day to the end date so that times during that day count as part of the event
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, 1);
            endDate = c.getTime();

            if (startDate == null) return false;
            Date now = new Date();
            return now.after(startDate) && now.before(endDate);
        } catch (FieldNotDefinedException e) {
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
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields to determine if event has started.\n" +
                    "Required fields: Database.Events.START");
            return false;
        }
    }

    public boolean isOfficial() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Events.OFFICIAL) && fields.get(Database.Events.OFFICIAL) instanceof Integer) {
            return (Integer) fields.get(Database.Events.OFFICIAL) == 1;
        }
        throw new FieldNotDefinedException("Field Database.Events.OFFICIAL is not defined");
    }

    public void setOfficial(boolean official) {
        fields.put(Database.Events.OFFICIAL, official ? 1 : 0);
    }

    public void setTeams(JsonArray teams) {
        fields.put(Database.Events.TEAMS, teams.toString());
        this.teams = teams;
    }

    public void setTeams(String teamsJson) {
        fields.put(Database.Events.TEAMS, teamsJson);
    }

    public JsonArray getTeams() throws FieldNotDefinedException {
        if (teams != null) {
            return teams;
        }
        if (fields.containsKey(Database.Events.TEAMS) && fields.get(Database.Events.TEAMS) instanceof String) {
            teams = JSONHelper.getasJsonArray((String) fields.get(Database.Events.TEAMS));
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
                return ThreadSafeFormatters.renderEventDate(startDate);
            }
            return ThreadSafeFormatters.renderEventShortFormat(startDate) + " to " +
              ThreadSafeFormatters.renderEventDate(endDate);
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for getting date string. \n" +
                    "Required fields: Database.Events.START, Database.Events.END");
            return null;
        }
    }

    @Override
    public EventListElement render() {
        try {
            return new EventListElement(getKey(), getEventShortName(), getDateString(), getLocation());
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering event\n" +
                    "Required fields: Database.Events.KEY, Database.Events.NAME, Database.Events.LOCATION");
            return null;
        }
    }

    public ArrayList<WebcastListElement> renderWebcasts() {
        ArrayList<WebcastListElement> webcasts = new ArrayList<>();
        try {
            int i = 1;
            for (JsonElement webcast : getWebcasts()) {
                try {
                    webcasts.add(new WebcastListElement(getKey(), getEventShortName(), webcast.getAsJsonObject(), i));
                    i++;
                } catch (FieldNotDefinedException e) {
                    Log.w(Constants.LOG_TAG, "Missing fields for rendering event webcasts: KEY, SHORTNAME");
                }
            }
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields to get event webcasts");
        }
        return webcasts;
    }

    public ArrayList<ListItem> renderAlliances() {
        ArrayList<ListItem> output = new ArrayList<>();
        renderAlliances(output);
        return output;
    }

    public void renderAlliances(List<ListItem> destList) {
        try {
            JsonArray alliances = getAlliances();
            int counter = 1;
            for (JsonElement alliance : alliances) {
                JsonArray teams = alliance.getAsJsonObject().get("picks").getAsJsonArray();
                destList.add(new AllianceListElement(getKey(), counter, teams));
                counter++;
            }
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering alliances.\n" +
              "Required field: Database.Events.ALLIANCES");
        } catch (IllegalArgumentException e) {
            Log.w(Constants.LOG_TAG, "Invalid alliance size. Can't render");
        }
    }

    public String getSearchTitles() throws FieldNotDefinedException {
        return getKey() + "," + getEventYear() + " " + getEventName() + "," + getEventYear() + " " + getEventShortName() + "," + getYearAgnosticEventKey() + " " + getEventYear();
    }

    public static APIResponse<Event> query(Context c, String eventKey, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying events table: " + whereClause + Arrays.toString(whereArgs));
        Database.Events table = Database.getInstance(c).getEventsTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        Event event;
        if (cursor != null && cursor.moveToFirst()) {
            event = table.inflate(cursor);
            cursor.close();
        } else {
            event = new Event();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Event updatedEvent;
                if (url.substring(url.lastIndexOf("/")).equals(eventKey)) {
                    /* event info request - inflate the event
                     * Here, the last url parameter is the event key
                     * All other endpoints have something else after that
                     */
                    updatedEvent = JSONHelper.getGson().fromJson(response.getData(), Event.class);
                    if (updatedEvent == null) {
                        // Error parsing the json
                        code = APIResponse.CODE.NODATA;
                        continue;
                    }
                } else {
                    /* We're getting one of the other endpoints which don't contain event data.
                     * Add them to the model based on which URL we hit
                     */
                    updatedEvent = new Event();
                    updatedEvent.setEventKey(eventKey);
                    EventHelper.addFieldByAPIUrl(updatedEvent, url, response.getData());
                }
                event.merge(updatedEvent);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            event.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(event, code);
    }

    public static APIResponse<ArrayList<Event>> queryList(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying events table: " + whereClause + Arrays.toString(whereArgs));
        ArrayList<Event> events = new ArrayList<>();

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray matchList = JSONHelper.getasJsonArray(response.getData());
                events = new ArrayList<>();
                for (JsonElement m : matchList) {
                    events.add(JSONHelper.getGson().fromJson(m, Event.class));
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        Database.Events eventsTable = Database.getInstance(c).getEventsTable();
        if (changed) {
            eventsTable.delete(whereClause, whereArgs);
            eventsTable.add(events);
        }

        // Fetch from db after web, see #372
        // Allows us to do whereClause filtering again
        Cursor cursor = eventsTable.query(fields, whereClause, whereArgs, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            events.clear();
            do {
                events.add(eventsTable.inflate(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + events.size() + " events, updated in db? " + changed);
        return new APIResponse<>(events, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getEventsTable().add(this);
    }
}