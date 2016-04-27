package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class Event extends BasicModel<Event> implements ViewModelRenderer<EventViewModel, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_MYTBA_BUTTON})
    public @interface RenderType {
    }

    public static final int RENDER_BASIC = 0;
    public static final int RENDER_MYTBA_BUTTON = 1;

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
        super(Database.TABLE_EVENTS, ModelType.EVENT);
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
        if (fields.containsKey(EventsTable.ALLIANCES) && fields.get(EventsTable.ALLIANCES) instanceof String) {
            alliances = JSONHelper.getasJsonArray((String) fields.get(EventsTable.ALLIANCES));
            return alliances;
        }
        throw new FieldNotDefinedException("Field Database.Events.ALLIANCES is not defined");
    }

    public void setAlliances(JsonArray alliances) {
        fields.put(EventsTable.ALLIANCES, alliances.toString());
        this.alliances = alliances;
    }

    public void setAlliances(String allianceJson) {
        fields.put(EventsTable.ALLIANCES, allianceJson);
    }

    public String getWebsite() {
        if (fields.containsKey(EventsTable.WEBSITE) && fields.get(EventsTable.WEBSITE) instanceof String) {
            return (String) fields.get(EventsTable.WEBSITE);
        }
        return "";
    }

    public int getEventYear() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.YEAR) && fields.get(EventsTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(EventsTable.YEAR);
        } else {
            return Integer.parseInt(getKey().substring(0, 4));
        }
    }

    public void setWebsite(String website) {
        fields.put(EventsTable.WEBSITE, website);
    }

    public JsonArray getRankings() throws FieldNotDefinedException {
        if (rankings != null) {
            return rankings;
        }
        if (fields.containsKey(EventsTable.RANKINGS) && fields.get(EventsTable.RANKINGS) instanceof String) {
            rankings = JSONHelper.getasJsonArray((String) fields.get(EventsTable.RANKINGS));
            return rankings;
        }
        throw new FieldNotDefinedException("Field Database.Events.RANKINGS is not defined");
    }

    public void setRankings(JsonArray rankings) {
        fields.put(EventsTable.RANKINGS, rankings.toString());
        this.rankings = rankings;
    }

    public void setRankings(String rankingsJson) {
        fields.put(EventsTable.RANKINGS, rankingsJson);
    }

    public JsonArray getWebcasts() throws FieldNotDefinedException {
        if (webcasts != null) {
            return webcasts;
        }
        if (fields.containsKey(EventsTable.WEBCASTS) && fields.get(EventsTable.WEBCASTS) instanceof String) {
            webcasts = JSONHelper.getasJsonArray((String) fields.get(EventsTable.WEBCASTS));
            return webcasts;
        }
        throw new FieldNotDefinedException("Field Database.Events.WEBCASTS is not defined");
    }

    public void setWebcasts(JsonArray webcasts) {
        fields.put(EventsTable.WEBCASTS, webcasts.toString());
        this.webcasts = webcasts;
    }

    public void setWebcasts(String webcastJson) {
        fields.put(EventsTable.WEBCASTS, webcastJson);
    }

    public JsonObject getStats() throws FieldNotDefinedException {
        if (stats != null) {
            return stats;
        }
        if (fields.containsKey(EventsTable.STATS) && fields.get(EventsTable.STATS) instanceof String) {
            stats = JSONHelper.getasJsonObject((String) fields.get(EventsTable.STATS));
            return stats;
        }
        throw new FieldNotDefinedException("Field Database.Events.STATS is not defined");
    }

    public void setStats(JsonObject stats) {
        fields.put(EventsTable.STATS, stats.toString());
        this.stats = stats;
    }

    public void setStats(String statsJson) {
        fields.put(EventsTable.STATS, statsJson);
    }


    @Override
    public String getKey() {
        if (fields.containsKey(EventsTable.KEY) && fields.get(EventsTable.KEY) instanceof String) {
            return (String) fields.get(EventsTable.KEY);
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
        fields.put(EventsTable.KEY, eventKey);
        fields.put(EventsTable.YEAR, Integer.parseInt(eventKey.substring(0, 4)));
    }

    public String getEventName() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.NAME) && fields.get(EventsTable.NAME) instanceof String) {
            return (String) fields.get(EventsTable.NAME);
        }
        throw new FieldNotDefinedException("Field Database.Events.NAME is not defined");
    }

    public void setEventName(String eventName) {
        fields.put(EventsTable.NAME, eventName);
    }

    public String getEventShortName() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.SHORTNAME) && fields.get(EventsTable.SHORTNAME) instanceof String) {
            String shortName = (String) fields.get(EventsTable.SHORTNAME);
            if (shortName != null && !shortName.isEmpty()) {
                return shortName;
            }
        }
        return getEventName();
    }

    public void setEventShortName(String eventShortName) {
        fields.put(EventsTable.SHORTNAME, eventShortName);
    }

    public String getLocation() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.LOCATION) && fields.get(EventsTable.LOCATION) instanceof String) {
            return (String) fields.get(EventsTable.LOCATION);
        }
        throw new FieldNotDefinedException("Field Database.Events.LOCATION is not defined");
    }

    public void setLocation(String location) {
        fields.put(EventsTable.LOCATION, location);
    }

    public String getVenue() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.VENUE) && fields.get(EventsTable.VENUE) instanceof String) {
            return (String) fields.get(EventsTable.VENUE);
        }
        throw new FieldNotDefinedException("Field Database.Events.VENUE is not defined");
    }

    public void setVenue(String venue) {
        fields.put(EventsTable.VENUE, venue);
    }

    public EventType getEventType() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.TYPE) && fields.get(EventsTable.TYPE) instanceof Integer) {
            return EventType.fromInt((Integer) fields.get(EventsTable.TYPE));
        }
        throw new FieldNotDefinedException("Field Database.Events.TYPE is not defined");
    }

    public void setEventType(EventType eventType) {
        fields.put(EventsTable.TYPE, eventType.ordinal());
    }

    public void setEventType(String typeString) {
        fields.put(EventsTable.TYPE, EventType.fromString(typeString).ordinal());
    }

    public void setEventType(int num) {
        fields.put(EventsTable.TYPE, num);
    }

    public int getDistrictEnum() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.DISTRICT) && fields.get(EventsTable.DISTRICT) instanceof Integer) {
            return (Integer) fields.get(EventsTable.DISTRICT);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT is not defined");
    }

    public void setDistrictEnum(int districtEnum) {
        fields.put(EventsTable.DISTRICT, districtEnum);
    }

    public String getDistrictTitle() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.DISTRICT_STRING) && fields.get(EventsTable.DISTRICT_STRING) instanceof String) {
            return (String) fields.get(EventsTable.DISTRICT_STRING);
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT_STRING is not defined");
    }

    public void setDistrictTitle(String districtTitle) {
        fields.put(EventsTable.DISTRICT_STRING, districtTitle);
    }

    public JsonObject getDistrictPoints() throws FieldNotDefinedException {
        if (districtPoints != null) {
            return districtPoints;
        }
        if (fields.containsKey(EventsTable.DISTRICT_POINTS) && fields.get(EventsTable.DISTRICT_POINTS) instanceof String) {
            districtPoints = JSONHelper.getasJsonObject((String) fields.get(EventsTable.DISTRICT_POINTS));
            return districtPoints;
        }
        throw new FieldNotDefinedException("Field Database.Events.DISTRICT_POINTS is not defined");
    }

    public void setDistrictPoints(String districtPoints) {
        fields.put(EventsTable.DISTRICT_POINTS, districtPoints);
    }

    public Date getStartDate() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.START) && fields.get(EventsTable.START) instanceof Long) {
            return new Date((Long) fields.get(EventsTable.START));
        }
        throw new FieldNotDefinedException("Field Database.Events.START is not defined");
    }

    public void setStartDate(Date startDate) {
        fields.put(EventsTable.START, startDate.getTime());
    }

    public void setStartDate(String startString) {
        if (startString == null || startString.isEmpty()) {
            return;
        }
        try {
            Date start = ThreadSafeFormatters.parseEventDate(startString);
            fields.put(EventsTable.START, start.getTime());
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
        if (fields.containsKey(EventsTable.END) && fields.get(EventsTable.END) instanceof Long) {
            return new Date((Long) fields.get(EventsTable.END));
        }
        throw new FieldNotDefinedException("Field Database.Events.END is not defined");
    }

    public void setEndDate(Date endDate) {
        fields.put(EventsTable.END, endDate.getTime());
    }

    public void setEndDate(String endString) {
        if (endString == null || endString.isEmpty()) {
            return;
        }
        try {
            fields.put(
                    EventsTable.END,
                    ThreadSafeFormatters.parseEventDate(endString).getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public int getCompetitionWeek() throws FieldNotDefinedException {
        if (fields.containsKey(EventsTable.WEEK) && fields.get(EventsTable.WEEK) instanceof Integer) {
            return (Integer) fields.get(EventsTable.WEEK);
        }
        throw new FieldNotDefinedException("Field Database.Events.WEEK is not defined");
    }

    public void setCompetitionWeek(int week) {
        //all preseason events are week 0
        fields.put(EventsTable.WEEK, Math.max(0, week));
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
        if (fields.containsKey(EventsTable.OFFICIAL) && fields.get(EventsTable.OFFICIAL) instanceof Integer) {
            return (Integer) fields.get(EventsTable.OFFICIAL) == 1;
        }
        throw new FieldNotDefinedException("Field Database.Events.OFFICIAL is not defined");
    }

    public boolean isChampsEvent() throws FieldNotDefinedException {
        EventType type = getEventType();
        return (type == EventType.CMP_DIVISION || type == EventType.CMP_FINALS);
    }

    public void setOfficial(boolean official) {
        fields.put(EventsTable.OFFICIAL, official ? 1 : 0);
    }

    public void setTeams(JsonArray teams) {
        fields.put(EventsTable.TEAMS, teams.toString());
        this.teams = teams;
    }

    public void setTeams(String teamsJson) {
        fields.put(EventsTable.TEAMS, teamsJson);
    }

    public JsonArray getTeams() throws FieldNotDefinedException {
        if (teams != null) {
            return teams;
        }
        if (fields.containsKey(EventsTable.TEAMS) && fields.get(EventsTable.TEAMS) instanceof String) {
            teams = JSONHelper.getasJsonArray((String) fields.get(EventsTable.TEAMS));
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
            return "";
        }
    }

    public String getSearchTitles() throws FieldNotDefinedException {
        return getKey() + "," + getEventYear() + " " + getEventName() + "," + getEventYear() + " " + getEventShortName() + "," + getYearAgnosticEventKey() + " " + getEventYear();
    }

    @Nullable @Override public EventViewModel renderToViewModel(Context context, @Nullable @RenderType Integer renderType) {
        EventViewModel model;
        try {
            model = new EventViewModel(getKey(), getEventYear(), getEventShortName(), getDateString(), getLocation());


            switch (renderType) {
                case RENDER_MYTBA_BUTTON:
                    model.setShowMyTbaSettings(true);
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
            Log.w(Constants.LOG_TAG, "Missing fields for rendering event\n" +
                    "Required fields: Database.Events.KEY, Database.Events.NAME, Database.Events.LOCATION");
            return null;
        }

        return model;
    }
}