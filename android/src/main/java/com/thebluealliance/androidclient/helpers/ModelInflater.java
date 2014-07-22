package com.thebluealliance.androidclient.helpers;

import android.database.Cursor;

import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.Date;

/**
 * File created by phil on 6/21/14.
 */
public class ModelInflater {

    /**
     * Inflate an award model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Award model containing the fields as defined in the cursor
     */
    public static Award inflateAward(Cursor data) {
        Award award = new Award();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Awards.EVENTKEY:
                    award.setEventKey(data.getString(i));
                    break;
                case Database.Awards.NAME:
                    award.setName(data.getString(i));
                    break;
                case Database.Awards.YEAR:
                    award.setYear(data.getInt(i));
                    break;
                case Database.Awards.WINNERS:
                    award.setWinners(data.getString(i));
                    break;
                case Database.Awards.KEY:
                    award.setKey(data.getString(i));
                    break;
                case Database.Awards.ENUM:
                    award.setEnum(data.getInt(i));
                    break;
                default:
            }
        }
        return award;
    }

    /**
     * Inflate an event model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Event model containing the fields as defined in the cursor
     */
    public static Event inflateEvent(Cursor data) {
        Event event = new Event();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Events.KEY:
                    event.setEventKey(data.getString(i));
                    break;
                case Database.Events.NAME:
                    event.setEventName(data.getString(i));
                    break;
                case Database.Events.LOCATION:
                    event.setLocation(data.getString(i));
                    break;
                case Database.Events.VENUE:
                    event.setVenue(data.getString(i));
                    break;
                case Database.Events.WEBSITE:
                    event.setWebsite(data.getString(i));
                    break;
                case Database.Events.TYPE:
                    event.setEventType(data.getInt(i));
                    break;
                case Database.Events.DISTRICT:
                    event.setDistrictEnum(data.getInt(i));
                    break;
                case Database.Events.DISTRICT_STRING:
                    event.setDistrictTitle(data.getString(i));
                    break;
                case Database.Events.START:
                    event.setStartDate(new Date(data.getLong(i)));
                    break;
                case Database.Events.END:
                    event.setEndDate(new Date(data.getLong(i)));
                    break;
                case Database.Events.OFFICIAL:
                    event.setOfficial(data.getInt(i) == 1);
                    break;
                case Database.Events.WEEK:
                    event.setCompetitionWeek(data.getInt(i));
                    break;
                case Database.Events.RANKINGS:
                    event.setRankings(data.getString(i));
                    break;
                case Database.Events.ALLIANCES:
                    event.setAlliances(data.getString(i));
                    break;
                case Database.Events.STATS:
                    event.setStats(data.getString(i));
                    break;
                case Database.Events.TEAMS:
                    event.setTeams(data.getString(i));
                    break;
                default:
            }
        }
        return event;
    }

    /**
     * Inflate a match model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Match model containing the fields as defined in the cursor
     */
    public static Match inflateMatch(Cursor data) {
        Match match = new Match();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Matches.KEY:
                    match.setKey(data.getString(i));
                    break;
                case Database.Matches.TIMESTRING:
                    match.setTimeString(data.getString(i));
                    break;
                case Database.Matches.TIME:
                    match.setTime(data.getLong(i));
                    break;
                case Database.Matches.ALLIANCES:
                    match.setAlliances(data.getString(i));
                    break;
                case Database.Matches.VIDEOS:
                    match.setVideos(data.getString(i));
                    break;
                case Database.Matches.MATCHNUM:
                    match.setMatchNumber(data.getInt(i));
                    break;
                case Database.Matches.SETNUM:
                    match.setSetNumber(data.getInt(i));
                    break;
                default:
            }
        }
        return match;
    }

    /**
     * Inflate a media model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Media model containing the fields as defined in the cursor
     */
    public static Media inflateMedia(Cursor data) {
        Media media = new Media();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Medias.TYPE:
                    media.setMediaType(data.getString(i));
                    break;
                case Database.Medias.FOREIGNKEY:
                    media.setForeignKey(data.getString(i));
                    break;
                case Database.Medias.YEAR:
                    media.setYear(data.getInt(i));
                    break;
                case Database.Medias.DETAILS:
                    media.setDetails(data.getString(i));
                    break;
                default:
            }
        }
        return media;
    }

    /**
     * Inflate a team model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Team model containing the fields as defined in the cursor
     */
    public static Team inflateTeam(Cursor data) {
        Team team = new Team();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Teams.KEY:
                    team.setTeamKey(data.getString(i));
                    break;
                case Database.Teams.NUMBER:
                    team.setTeamNumber(data.getInt(i));
                    break;
                case Database.Teams.SHORTNAME:
                    team.setNickname(data.getString(i));
                    break;
                case Database.Teams.NAME:
                    team.setFullName(data.getString(i));
                    break;
                case Database.Teams.LOCATION:
                    team.setLocation(data.getString(i));
                    break;
                case Database.Teams.WEBSITE:
                    team.setWebsite(data.getString(i));
                    break;
                case Database.Teams.YEARS_PARTICIPATED:
                    team.setYearsParticipated(data.getString(i));
                    break;
                default:
            }
        }
        return team;
    }

    /**
     * Inflate an eventTeam model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return EventTeam model containing the fields as defined in the cursor
     */
    public static EventTeam inflateEventTeam(Cursor data) {
        EventTeam eventTeam = new EventTeam();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.EventTeams.TEAMKEY:
                    eventTeam.setTeamKey(data.getString(i));
                    break;
                case Database.EventTeams.EVENTKEY:
                    eventTeam.setEventKey(data.getString(i));
                    break;
                case Database.EventTeams.YEAR:
                    eventTeam.setYear(data.getInt(i));
                    break;
                case Database.EventTeams.COMPWEEK:
                    eventTeam.setCompWeek(data.getInt(i));
                    break;
                case Database.EventTeams.KEY:
                    eventTeam.setKey(data.getString(i));
                    break;
                default:
            }
        }
        return eventTeam;
    }

}
