package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.types.DistrictType;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class Event extends com.thebluealliance.api.model.Event implements TbaDatabaseModel,
                                                           ViewModelRenderer<EventViewModel, Integer> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RENDER_BASIC, RENDER_MYTBA_BUTTON})
    public @interface RenderType {
    }

    public static final int RENDER_BASIC = 0;
    public static final int RENDER_MYTBA_BUTTON = 1;

    private Date startDate;
    private Date endDate;

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

    public void setStartDate(String startString) {
        try {
            startDate = ThreadSafeFormatters.parseEventDate(startString);
            setStartDate(startDate.getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public Date getFormattedStartDate() {
        return startDate;
    }

    public void setEndDate(String endString) {
        try {
            endDate = ThreadSafeFormatters.parseEventDate(endString);
            setEndDate(endDate.getTime());
        } catch (ParseException ex) {
            //can't parse the date
            throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
        }
    }

    public Date getFormattedEndDate() {
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
        @javax.annotation.Nullable Integer eventType = getEventType();
        return eventType != null
               ? EventType.fromInt(eventType)
               : EventType.NONE;
    }

    public DistrictType getEventDistrictEnum() {
        @javax.annotation.Nullable Integer districtEnum = getEventDistrict();
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
                                   getLocation());
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