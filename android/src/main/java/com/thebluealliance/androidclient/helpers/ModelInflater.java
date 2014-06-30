package com.thebluealliance.androidclient.helpers;

import android.database.Cursor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.Date;

/**
 * File created by phil on 6/21/14.
 */
public class ModelInflater {

    public static Award inflateAward(Cursor data){
        Award award = new Award();
        for(int i=0; i<data.getColumnCount(); i++){
            switch(data.getColumnName(i)){
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
                    JsonArray winners = JSONManager.getasJsonArray(data.getString(i));
                    award.setWinners(winners);
                    break;
                default:
            }
        }
        return award;
    }

    public static Event inflateEvent(Cursor data){
        Event event = new Event();
        for(int i=0; i<data.getColumnCount(); i++){
            switch(data.getColumnName(i)){
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
                case Database.Events.TYPE:
                    event.setEventType(data.getString(i));
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
                    JsonArray rankings = JSONManager.getasJsonArray(data.getString(i));
                    event.setRankings(rankings);
                    break;
                case Database.Events.ALLIANCES:
                    JsonArray alliances = JSONManager.getasJsonArray(data.getString(i));
                    event.setAlliances(alliances);
                    break;
                case Database.Events.STATS:
                    JsonObject stats = JSONManager.getasJsonObject(data.getString(i));
                    event.setStats(stats);
                    break;
                default:
            }
        }
        return event;
    }

    public static Match inflateMatch(Cursor data){
        Match match = new Match();
        for(int i=0; i<data.getColumnCount(); i++){
            switch(data.getColumnName(i)){
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
                    JsonObject alliances = JSONManager.getasJsonObject(data.getString(i));
                    match.setAlliances(alliances);
                    break;
                case Database.Matches.VIDEOS:
                    JsonArray videos = JSONManager.getasJsonArray(data.getString(i));
                    match.setVideos(videos);
                    break;
                default:
            }
        }
        return match;
    }

    public static Media inflateMedia(Cursor data){
        Media media = new Media();
        for(int i=0; i<data.getColumnCount(); i++){
            switch(data.getColumnName(i)){
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
                    JsonObject details = JSONManager.getasJsonObject(data.getString(i));
                    media.setDetails(details);
                    break;
                default:
            }
        }
        return media;
    }

    public static Team inflateTeam(Cursor data){
        Team team = new Team();
        for(int i=0; i<data.getColumnCount(); i++) {
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
                case Database.Teams.LOCATION:
                    team.setLocation(data.getString(i));
                    break;
                case Database.Teams.WEBSITE:
                    team.setWebsite(data.getString(i));
                    break;
                case Database.Teams.EVENTS:
                    JsonArray events = JSONManager.getasJsonArray(data.getString(i));
                    team.setEvents(events);
                    break;
                case Database.Teams.YEARS_PARTICIPATED:
                    JsonArray years = JSONManager.getasJsonArray(data.getString(i));
                    team.setYearsParticipated(years);
                    break;
                default:
            }
        }
        return team;
    }

}
