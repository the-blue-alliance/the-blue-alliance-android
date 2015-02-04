package com.thebluealliance.androidclient.helpers;

import android.database.Cursor;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.models.Subscription;
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
                case Database.Events.SHORTNAME:
                    event.setEventShortName(data.getString(i));
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
                case Database.Events.DISTRICT_POINTS:
                    event.setDistrictPoints(data.getString(i));
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

    public static District inflateDistrict(Cursor data) {
        District district = new District();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Districts.KEY:
                    district.setKey(data.getString(i));
                    break;
                case Database.Districts.ABBREV:
                    district.setAbbreviation(data.getString(i));
                    break;
                case Database.Districts.ENUM:
                    district.setEnum(data.getInt(i));
                    break;
                case Database.Districts.YEAR:
                    district.setYear(data.getInt(i));
                    break;
                case Database.Districts.NAME:
                    district.setName(data.getString(i));
                    break;
                default:
            }
        }
        return district;
    }

    public static DistrictTeam inflateDistrictTeam(Cursor data) {
        DistrictTeam districtTeam = new DistrictTeam();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.DistrictTeams.KEY:
                    districtTeam.setKey(data.getString(i));
                    break;
                case Database.DistrictTeams.TEAM_KEY:
                    districtTeam.setTeamKey(data.getString(i));
                    break;
                case Database.DistrictTeams.DISTRICT_KEY:
                    districtTeam.setDistrictKey(data.getString(i));
                    break;
                case Database.DistrictTeams.DISTRICT_ENUM:
                    districtTeam.setDistrictEnum(data.getInt(i));
                    break;
                case Database.DistrictTeams.YEAR:
                    districtTeam.setYear(data.getInt(i));
                    break;
                case Database.DistrictTeams.RANK:
                    districtTeam.setRank(data.getInt(i));
                    break;
                case Database.DistrictTeams.EVENT1_KEY:
                    districtTeam.setEvent1Key(data.getString(i));
                    break;
                case Database.DistrictTeams.EVENT1_POINTS:
                    districtTeam.setEvent1Points(data.getInt(i));
                    break;
                case Database.DistrictTeams.EVENT2_KEY:
                    districtTeam.setEvent2Key(data.getString(i));
                    break;
                case Database.DistrictTeams.EVENT2_POINTS:
                    districtTeam.setEvent2Points(data.getInt(i));
                    break;
                case Database.DistrictTeams.CMP_KEY:
                    districtTeam.setCmpKey(data.getString(i));
                    break;
                case Database.DistrictTeams.CMP_POINTS:
                    districtTeam.setCmpPoints(data.getInt(i));
                    break;
                case Database.DistrictTeams.ROOKIE_POINTS:
                    districtTeam.setRookiePoints(data.getInt(i));
                    break;
                case Database.DistrictTeams.TOTAL_POINTS:
                    districtTeam.setTotalPoints(data.getInt(i));
                    break;
                case Database.DistrictTeams.JSON:
                    districtTeam.setJson(data.getString(i));
                    break;
                default:
            }
        }
        return districtTeam;
    }

    public static Favorite inflateFavorite(Cursor data){
        Favorite favorite = new Favorite();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Favorites.MODEL_KEY:
                    favorite.setModelKey(data.getString(i));
                    break;
                case Database.Favorites.USER_NAME:
                    favorite.setUserName(data.getString(i));
                    break;
                case Database.Favorites.MODEL_ENUM:
                    favorite.setModelEnum(data.getInt(i));
                    break;
                default:
            }
        }
        return favorite;
    }

    public static Subscription inflateSubscription(Cursor data){
        Subscription subscription = new Subscription();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case Database.Subscriptions.MODEL_KEY:
                    subscription.setModelKey(data.getString(i));
                    break;
                case Database.Subscriptions.USER_NAME:
                    subscription.setUserName(data.getString(i));
                    break;
                case Database.Subscriptions.MODEL_ENUM:
                    subscription.setModelEnum(data.getInt(i));
                    break;
                case Database.Subscriptions.NOTIFICATION_SETTINGS:
                    Log.d(Constants.LOG_TAG, "Settings: "+data.getString(i));
                    subscription.setNotificationSettings(data.getString(i));
                    break;
                default:
            }
        }
        return subscription;
    }
    
    public static StoredNotification inflateStoredNotification(Cursor data){
        StoredNotification storedNotification = new StoredNotification();
        for(int i=0; i<data.getColumnCount(); i++){
            switch(data.getColumnName(i)){
                case Database.Notifications.ID:
                    storedNotification.setId(data.getInt(i));
                    break;
                case Database.Notifications.TYPE:
                    storedNotification.setType(data.getString(i));
                    break;
                case Database.Notifications.TITLE:
                    storedNotification.setTitle(data.getString(i));
                    break;
                case Database.Notifications.BODY:
                    storedNotification.setBody(data.getString(i));
                    break;
                case Database.Notifications.INTENT:
                    storedNotification.setIntent(data.getString(i));
                    break;
                case Database.Notifications.TIME:
                    storedNotification.setTime(new Date(data.getLong(i)));
            }
        }
        return storedNotification;
    }
}
