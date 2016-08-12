package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;


public class Event extends com.thebluealliance.api.model.Event implements RenderableModel,
                                                                          TbaDatabaseModel,
                                                           ViewModelRenderer<EventViewModel, Integer> {

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

    private @Nullable JsonArray matches, alliances, rankings, webcasts, teams;
    private @Nullable JsonObject stats, districtPoints;
    private @Nullable Integer competitionWeek;

    public Event() {
        alliances = null;
        rankings = null;
        webcasts = null;
        stats = null;
        districtPoints = null;
    }

    public JsonArray getAlliances()  {

    }

    public void setAlliances(JsonArray alliances) {

    }

    public void setAlliances(String allianceJson) {
        fields.put(EventsTable.ALLIANCES, allianceJson);
    }

    public JsonArray getRankings() {

    }

    public void setRankings(JsonArray rankings) {
    }

    public void setRankings(String rankingsJson) {
    }

    public JsonArray getWebcasts() {

    }

    public void setWebcasts(JsonArray webcasts) {
    }

    public void setWebcasts(String webcastJson) {
    }

    public JsonObject getStats() {

    }

    public void setStats(JsonObject stats) {

    }

    public void setStats(String statsJson) {
    }

    /**
     * Gets the event key with the year stripped out.
     *
     * @return Event key without the year
     */
    public String getYearAgnosticEventKey() {
        return getKey().replaceAll("[0-9]", "");
    }

    public JsonObject getDistrictPoints() {

    }

    public void setDistrictPoints(String districtPoints) {
    }

    public Date getStartDate() {

    }

    public void setStartDate(Date startDate) {
    }

    public void setCompetitionWeekFromStartDate() {
        Date start;
        start = getStartDate();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start.getTime());
        int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int firstWeek = Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
        int week = eventWeek - firstWeek;
        setCompetitionWeek(week);
    }

    public Date getEndDate() {

    }

    public void setEndDate(Date endDate) {
    }

    public void setEndDate(String endString) {
    }

    @Nullable
    public Integer getCompetitionWeek() {
        return competitionWeek;
    }

    public void setCompetitionWeek(int week) {
        competitionWeek = week;
    }

    @Nullable
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
    public void setMatches(@Nullable JsonArray matches) {
        this.matches = matches;
    }

    public boolean isHappeningNow() {
        Date startDate = getStartDate(),
                endDate = getEndDate();

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

    public void setTeams(@Nullable JsonArray teams) {
        this.teams = teams;
    }

    public void setTeams(String teamsJson) {
        teams = JSONHelper.getasJsonArray(teamsJson);
    }

    @Nullable
    public JsonArray getTeams() {
        return teams;
    }

    public String getDateString() {
        Date startDate = getStartDate(),
                endDate = getEndDate();
        if (startDate == null || endDate == null) {
            return "";
        }
        if (startDate.equals(endDate)) {
            return ThreadSafeFormatters.renderEventDate(startDate);
        }
        return ThreadSafeFormatters.renderEventShortFormat(startDate) + " to "
               + ThreadSafeFormatters.renderEventDate(endDate);
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
}