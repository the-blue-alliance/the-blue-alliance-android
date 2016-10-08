package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.types.DistrictType;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;
import com.thebluealliance.api.model.IEvent;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;


public class Event implements IEvent, TbaDatabaseModel, ViewModelRenderer<EventViewModel, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_MYTBA_BUTTON})
    public @interface RenderType {
    }

    public static final int RENDER_BASIC = 0;
    public static final int RENDER_MYTBA_BUTTON = 1;

    private String alliances = null;
    private Integer competitionWeek = null;
    private Long endTimestamp = null;
    private String eventCode = null;
    private Integer eventDistrict = null;
    private String eventDistrictString = null;
    private Integer eventType = null;
    private String eventTypeString = null;
    private String key = null;
    private Long lastModified = null;
    private String location = null;
    private String name = null;
    private Boolean official = null;
    private String shortName = null;
    private Long startTimestamp = null;
    private String timezone = null;
    private String venueAddress = null;
    private String webcasts = null;
    private String website = null;
    private Integer year = null;


    private Date startDate;
    private Date endDate;
    private JsonArray alliancesJson;

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.LEVEL_STARTING,
            NotificationTypes.ALLIANCE_SELECTION,
            NotificationTypes.AWARDS,
            NotificationTypes.SCHEDULE_UPDATED,
            //NotificationTypes.FINAL_RESULTS
    };

    public Event() {
        startDate = null;
        endDate = null;
    }

    @Nullable @Override public String getAlliances() {
        return alliances;
    }

    public JsonArray getAlliancesJson() {
        return alliancesJson;
    }

    public void setAlliancesJson(JsonArray alliancesJson) {
        this.alliancesJson = alliancesJson;
    }

    @Override public void setAlliances(String alliances) {
        this.alliances = alliances;
        setAlliancesJson(JSONHelper.getasJsonArray(alliances));
    }

    @Nullable @Override public Integer getCompetitionWeek() {
        return competitionWeek;
    }

    @Override public void setCompetitionWeek(Integer competitionWeek) {
        this.competitionWeek = competitionWeek;
    }

    public Long getEndDate() {
        return endTimestamp;
    }

    public void setEndDate(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override public String getEventCode() {
        return eventCode;
    }

    @Override public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    @Nullable @Override public Integer getEventDistrict() {
        return eventDistrict;
    }

    @Override public void setEventDistrict(Integer eventDistrict) {
        this.eventDistrict = eventDistrict;
    }

    @Nullable @Override public String getEventDistrictString() {
        return eventDistrictString;
    }

    @Override public void setEventDistrictString(String eventDistrictString) {
        this.eventDistrictString = eventDistrictString;
    }

    @Nullable @Override public Integer getEventType() {
        return eventType;
    }

    @Override public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    @Nullable @Override public String getEventTypeString() {
        return eventTypeString;
    }

    @Override public void setEventTypeString(String eventTypeString) {
        this.eventTypeString = eventTypeString;
    }

    @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Nullable @Override public String getLocation() {
        return location;
    }

    @Override public void setLocation(String location) {
        this.location = location;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Nullable @Override public Boolean getOfficial() {
        return official;
    }

    @Override public void setOfficial(Boolean official) {
        this.official = official;
    }

    @Nullable @Override public String getShortName() {
        if (shortName == null || shortName.isEmpty()) {
            return getName();
        }
        return shortName;
    }

    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getStartDate() {
        return startTimestamp;
    }

    public void setStartDate(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Nullable @Override public String getTimezone() {
        return timezone;
    }

    @Override public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Nullable @Override public String getVenueAddress() {
        return venueAddress;
    }

    @Override public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    @Nullable @Override public String getWebcasts() {
        return webcasts;
    }

    @Override public void setWebcasts(String webcasts) {
        this.webcasts = webcasts;
    }

    @Nullable @Override public String getWebsite() {
        return website;
    }

    @Override public void setWebsite(String website) {
        this.website = website;
    }

    @Override public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public Integer getYear() {
        if (year == null) {
            int year = EventHelper.getYear(getKey());
            setYear(year);
            return year;
        }
        return year;
    }

    public void setStartDate(String startString) {
        if (startString.isEmpty()) {
            return;
        }
        try {
            startDate = ThreadSafeFormatters.parseEventDate(startString);
            setStartDate(startDate.getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException(
                    "Invalid date format: " + startString + ". Should be like yyyy-MM-dd");
        }
    }

    public Date getFormattedStartDate() {
        if (startDate == null) {
            startDate = new Date(getStartDate() != null ? getStartDate() : 0);
        }
        return startDate;
    }

    public void setEndDate(String endString) {
        if (endString.isEmpty()) {
            return;
        }
        try {
            endDate = ThreadSafeFormatters.parseEventDate(endString);
            setEndDate(endDate.getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public Date getFormattedEndDate() {
        if (endDate == null) {
            endDate = new Date(getEndDate() != null ? getEndDate() : 0);
        }
        return endDate;
    }
    /**
     * Gets the event key with the year stripped out.
     *
     * @return Event key without the year
     */
    public String getYearAgnosticEventKey() {
        return getKey().replaceAll("[0-9]", "");
    }

    public void setCompetitionWeekFromStartDate() {
        Date start;
        start = getFormattedStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start.getTime());
        int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int firstWeek = Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
        int week = eventWeek - firstWeek;
        week = Math.max(week, 0); // Ensure that week is never <0
        setCompetitionWeek(week);
    }

    public boolean isHappeningNow() {
        Date startDate = getFormattedStartDate(),
                endDate = getFormattedEndDate();

        //since the Dates are at time 0:00, we need to add one day to the end date so that times
        // during that day count as part of the event
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DATE, 1);
        endDate = c.getTime();

        if (startDate == null) {
            return false;
        }
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }

    public boolean isChampsEvent() {
        Integer typeInt = getEventType();
        if (typeInt == null) {
            return false;
        }
        EventType type = EventType.fromInt(typeInt);
        return (type == EventType.CMP_DIVISION || type == EventType.CMP_FINALS);
    }

    public String getDateString() {
        Date startDate = getFormattedStartDate(),
                endDate = getFormattedEndDate();
        if (startDate == null || endDate == null) {
            return "";
        }
        if (startDate.equals(endDate)) {
            return ThreadSafeFormatters.renderEventDate(startDate);
        }
        return ThreadSafeFormatters.renderEventShortFormat(startDate) + " to "
               + ThreadSafeFormatters.renderEventDate(endDate);
    }

    public EventType getEventTypeEnum() {
        @Nullable Integer eventType = getEventType();
        return eventType != null
               ? EventType.fromInt(eventType)
               : EventType.NONE;
    }

    public DistrictType getEventDistrictEnum() {
        @Nullable Integer districtEnum = getEventDistrict();
        return districtEnum != null
               ? DistrictType.fromEnum(districtEnum)
               : DistrictType.NO_DISTRICT;
    }

    public String getSearchTitles() {
        Integer year = getYear();
        return String.format("%1$s,%2$s %3$s,%4$s %5$s,%6$s %7$s",
                             getKey(), year, getName(), year, getShortName(), getEventCode(), year);
    }

    @Nullable @Override
    public EventViewModel renderToViewModel(Context context, @Nullable @RenderType Integer renderType) {
        if (renderType == null) {
            return null;
        }
        EventViewModel model;
        model = new EventViewModel(getKey(),
                                   getYear(),
                                   getShortName(),
                                   getDateString(),
                                   getLocation(),
                                   getEventDistrictString());
        switch (renderType) {
            case RENDER_MYTBA_BUTTON:
                model.setShowMyTbaSettings(true);
        }
        return model;
    }

    @Override
    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(EventsTable.KEY, getKey());
        params.put(EventsTable.YEAR, getYear());
        params.put(EventsTable.NAME, getName());
        params.put(EventsTable.SHORTNAME, getShortName());
        params.put(EventsTable.LOCATION, getLocation());
        params.put(EventsTable.VENUE, getVenueAddress());
        params.put(EventsTable.TYPE, getEventType());
        params.put(EventsTable.DISTRICT, getEventDistrict());
        params.put(EventsTable.DISTRICT_STRING, getEventDistrictString());
        params.put(EventsTable.START, getStartDate());
        params.put(EventsTable.END, getEndDate());
        params.put(EventsTable.OFFICIAL, getOfficial());
        params.put(EventsTable.WEEK, getCompetitionWeek());
        params.put(EventsTable.WEBCASTS, getWebcasts());
        params.put(EventsTable.WEBSITE, getWebsite());
        return params;
    }
}