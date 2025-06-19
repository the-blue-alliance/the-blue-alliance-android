package com.thebluealliance.androidclient.models;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;
import com.thebluealliance.api.model.IEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import androidx.annotation.IntDef;
import thebluealliance.api.model.District;


public class Event implements IEvent, TbaDatabaseModel, ViewModelRenderer<EventViewModel, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_MYTBA_BUTTON})
    public @interface RenderType {
    }

    public static final int RENDER_BASIC = 0;
    public static final int RENDER_MYTBA_BUTTON = 1;

    private String key;
    private String eventCode;
    private String name;
    private Integer year;

    private @Nullable Integer week;
    private @Nullable Integer eventType;
    private @Nullable String eventTypeString;
    private @Nullable String shortName;
    private @Nullable String address;
    private @Nullable District district;
    private @Nullable String districtKey;
    private @Nullable String gmapsUrl;
    private @Nullable String locationName;
    private @Nullable String location;
    private @Nullable String city;
    private @Nullable String webcasts;
    private @Nullable String website;
    private @Nullable Date endDate;
    private @Nullable Date startDate;
    private @Nullable String timezone;
    private @Nullable Long lastModified;

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.LEVEL_STARTING,
            NotificationTypes.ALLIANCE_SELECTION,
            NotificationTypes.AWARDS,
            NotificationTypes.SCHEDULE_UPDATED,
            NotificationTypes.MATCH_VIDEO,
            //NotificationTypes.FINAL_RESULTS
    };

    public Event() {
        startDate = null;
        endDate = null;
    }

    @Nullable @Override
    public Integer getWeek() {
        return week;
    }

    @Override
    public void setWeek(@Nullable Integer competitionWeek) {
        if (competitionWeek == null) {
            /* Fall back and calculate the week, mainly for offseason events */
            Date start;
            start = getFormattedStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(start.getTime());
            int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
            int firstWeek = Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
            int week = eventWeek - firstWeek;
            this.week = Math.max(week, 0); // Ensure that week is never <0
        } else {
            /* TBA Server regards 'week 0' as the first competition week */
            this.week = competitionWeek;
        }
    }

    @Override
    public @Nullable Date getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getEventCode() {
        if (eventCode == null) {
            return EventHelper.getEventCode(key);
        }
        return eventCode;
    }

    @Override
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    @Override @Nullable
    public District getDistrict() {
        return district;
    }

    @Override
    public void setDistrict(@Nullable District district) {
        this.district = district;
    }

    @Nullable public String getDistrictKey() {
        return districtKey;
    }

    public void setDistrictKey(@Nullable String districtKey) {
        this.districtKey = districtKey;
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

    @Override public void setEventTypeString(@Nullable String eventTypeString) {
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

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override @Nullable
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Nullable @Override
    public String getShortName() {
        if (shortName == null || shortName.isEmpty()) {
            return getName();
        }
        return shortName;
    }

    @Override public void setShortName(@Nullable String shortName) {
        this.shortName = shortName;
    }

    @Override
    public void setStartDate(@Nullable Date startDate) {
        this.startDate = startDate;
    }

    @Override @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable @Override public String getTimezone() {
        return timezone;
    }

    @Override public void setTimezone(@Nullable String timezone) {
        this.timezone = timezone;
    }

    @Nullable @Override public String getWebcasts() {
        return webcasts;
    }

    @Override public void setWebcasts(@Nullable String webcasts) {
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

    @Override @Nullable
    public String getGmapsUrl() {
        return gmapsUrl;
    }

    @Override
    public void setGmapsUrl(@Nullable String gmapsUrl) {
        this.gmapsUrl = gmapsUrl;
    }

    @Override @Nullable
    public String getLocationName() {
        return locationName;
    }

    @Override public void
    setLocationName(@Nullable String locationName) {
        this.locationName = locationName;
    }

    @Nullable public String getLocation() {
        return location;
    }

    @Nullable public String getCity() {
        return city;
    }

    public void setCity(@Nullable String city) {
        this.city = city;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    public void setStartDate(String startString) {
        if (startString.isEmpty()) {
            return;
        }
        try {
            startDate = ThreadSafeFormatters.parseEventDate(startString);
            setStartDate(startDate);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException(
                    "Invalid date format: " + startString + ". Should be like yyyy-MM-dd");
        }
    }

    public void setStartDate(long timestamp) {
        startDate = new Date(timestamp);
    }

    public Date getFormattedStartDate() {
        if (startDate == null) {
            startDate = new Date(0);
        }
        return startDate;
    }

    public void setEndDate(String endString) {
        if (endString.isEmpty()) {
            return;
        }
        try {
            endDate = ThreadSafeFormatters.parseEventDate(endString);
            setEndDate(endDate);
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public void setEndDate(long timestamp) {
        endDate = new Date(timestamp);
    }

    public Date getFormattedEndDate() {
        if (endDate == null) {
            endDate = new Date(0);
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
        setWeek(week);
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
        if (getStartDate() == null || getEndDate() == null) {
            return "";
        }
       Date startDate = getFormattedStartDate(),
                endDate = getFormattedEndDate();
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

    public String getEventDistrictString() {
        @Nullable District district = getDistrict();
        return district != null
                ? district.getDisplayName()
                : "";
    }

    public String getSearchTitles() {
        Integer year = getYear();
        return String.format("%1$s,%2$s %3$s,%4$s %5$s,%6$s",
                             getKey(), year, getName(), year, getShortName(), getEventCode());
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
    public ContentValues getParams(Gson gson) {
        @Nullable Date startDate = getStartDate();
        @Nullable Date endDate = getEndDate();
        @Nullable District district = getDistrict();
        ContentValues params = new ContentValues();
        params.put(EventsTable.KEY, getKey());
        params.put(EventsTable.YEAR, getYear());
        params.put(EventsTable.NAME, getName());
        params.put(EventsTable.SHORTNAME, getShortName());
        params.put(EventsTable.LOCATION, getLocation());
        params.put(EventsTable.CITY, getCity());
        params.put(EventsTable.VENUE, getLocationName());
        params.put(EventsTable.ADDRESS, getAddress());
        params.put(EventsTable.TYPE, getEventType());
        params.put(EventsTable.DISTRICT_KEY, district != null ? district.getKey() : "");
        params.put(EventsTable.START, startDate != null ? startDate.getTime() : 0);
        params.put(EventsTable.END, endDate != null ? endDate.getTime() : 0);
        params.put(EventsTable.WEEK, getWeek());
        params.put(EventsTable.WEBCASTS, getWebcasts());
        params.put(EventsTable.WEBSITE, getWebsite());
        params.put(EventsTable.LAST_MODIFIED, getLastModified());
        return params;
    }
}