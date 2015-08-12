package com.thebluealliance.androidclient.database;

import android.database.Cursor;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
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
                case AwardsTable.EVENTKEY:
                    award.setEventKey(data.getString(i));
                    break;
                case AwardsTable.NAME:
                    award.setName(data.getString(i));
                    break;
                case AwardsTable.YEAR:
                    award.setYear(data.getInt(i));
                    break;
                case AwardsTable.WINNERS:
                    award.setWinners(data.getString(i));
                    break;
                case AwardsTable.KEY:
                    award.setKey(data.getString(i));
                    break;
                case AwardsTable.ENUM:
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
                case EventsTable.KEY:
                    event.setEventKey(data.getString(i));
                    break;
                case EventsTable.NAME:
                    event.setEventName(data.getString(i));
                    break;
                case EventsTable.SHORTNAME:
                    event.setEventShortName(data.getString(i));
                    break;
                case EventsTable.LOCATION:
                    event.setLocation(data.getString(i));
                    break;
                case EventsTable.VENUE:
                    event.setVenue(data.getString(i));
                    break;
                case EventsTable.WEBSITE:
                    event.setWebsite(data.getString(i));
                    break;
                case EventsTable.TYPE:
                    event.setEventType(data.getInt(i));
                    break;
                case EventsTable.DISTRICT:
                    event.setDistrictEnum(data.getInt(i));
                    break;
                case EventsTable.DISTRICT_STRING:
                    event.setDistrictTitle(data.getString(i));
                    break;
                case EventsTable.DISTRICT_POINTS:
                    event.setDistrictPoints(data.getString(i));
                    break;
                case EventsTable.START:
                    event.setStartDate(new Date(data.getLong(i)));
                    break;
                case EventsTable.END:
                    event.setEndDate(new Date(data.getLong(i)));
                    break;
                case EventsTable.OFFICIAL:
                    event.setOfficial(data.getInt(i) == 1);
                    break;
                case EventsTable.WEEK:
                    event.setCompetitionWeek(data.getInt(i));
                    break;
                case EventsTable.RANKINGS:
                    event.setRankings(data.getString(i));
                    break;
                case EventsTable.ALLIANCES:
                    event.setAlliances(data.getString(i));
                    break;
                case EventsTable.STATS:
                    event.setStats(data.getString(i));
                    break;
                case EventsTable.TEAMS:
                    event.setTeams(data.getString(i));
                    break;
                case EventsTable.WEBCASTS:
                    event.setWebcasts(data.getString(i));
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
                case MatchesTable.KEY:
                    match.setKey(data.getString(i));
                    break;
                case MatchesTable.TIMESTRING:
                    match.setTimeString(data.getString(i));
                    break;
                case MatchesTable.TIME:
                    match.setTime(data.getLong(i));
                    break;
                case MatchesTable.ALLIANCES:
                    match.setAlliances(data.getString(i));
                    break;
                case MatchesTable.VIDEOS:
                    match.setVideos(data.getString(i));
                    break;
                case MatchesTable.MATCHNUM:
                    match.setMatchNumber(data.getInt(i));
                    break;
                case MatchesTable.SETNUM:
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
                case MediasTable.TYPE:
                    media.setMediaType(data.getString(i));
                    break;
                case MediasTable.FOREIGNKEY:
                    media.setForeignKey(data.getString(i));
                    break;
                case MediasTable.YEAR:
                    media.setYear(data.getInt(i));
                    break;
                case MediasTable.DETAILS:
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
                case TeamsTable.KEY:
                    team.setTeamKey(data.getString(i));
                    break;
                case TeamsTable.NUMBER:
                    team.setTeamNumber(data.getInt(i));
                    break;
                case TeamsTable.SHORTNAME:
                    team.setNickname(data.getString(i));
                    break;
                case TeamsTable.NAME:
                    team.setFullName(data.getString(i));
                    break;
                case TeamsTable.LOCATION:
                    team.setLocation(data.getString(i));
                    break;
                case TeamsTable.WEBSITE:
                    team.setWebsite(data.getString(i));
                    break;
                case TeamsTable.YEARS_PARTICIPATED:
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
                case EventTeamsTable.TEAMKEY:
                    eventTeam.setTeamKey(data.getString(i));
                    break;
                case EventTeamsTable.EVENTKEY:
                    eventTeam.setEventKey(data.getString(i));
                    break;
                case EventTeamsTable.YEAR:
                    eventTeam.setYear(data.getInt(i));
                    break;
                case EventTeamsTable.COMPWEEK:
                    eventTeam.setCompWeek(data.getInt(i));
                    break;
                case EventTeamsTable.KEY:
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
                case DistrictsTable.KEY:
                    district.setKey(data.getString(i));
                    break;
                case DistrictsTable.ABBREV:
                    district.setAbbreviation(data.getString(i));
                    break;
                case DistrictsTable.ENUM:
                    district.setEnum(data.getInt(i));
                    break;
                case DistrictsTable.YEAR:
                    district.setYear(data.getInt(i));
                    break;
                case DistrictsTable.NAME:
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
                case DistrictTeamsTable.KEY:
                    districtTeam.setKey(data.getString(i));
                    break;
                case DistrictTeamsTable.TEAM_KEY:
                    districtTeam.setTeamKey(data.getString(i));
                    break;
                case DistrictTeamsTable.DISTRICT_KEY:
                    districtTeam.setDistrictKey(data.getString(i));
                    break;
                case DistrictTeamsTable.DISTRICT_ENUM:
                    districtTeam.setDistrictEnum(data.getInt(i));
                    break;
                case DistrictTeamsTable.YEAR:
                    districtTeam.setYear(data.getInt(i));
                    break;
                case DistrictTeamsTable.RANK:
                    districtTeam.setRank(data.getInt(i));
                    break;
                case DistrictTeamsTable.EVENT1_KEY:
                    districtTeam.setEvent1Key(data.getString(i));
                    break;
                case DistrictTeamsTable.EVENT1_POINTS:
                    districtTeam.setEvent1Points(data.getInt(i));
                    break;
                case DistrictTeamsTable.EVENT2_KEY:
                    districtTeam.setEvent2Key(data.getString(i));
                    break;
                case DistrictTeamsTable.EVENT2_POINTS:
                    districtTeam.setEvent2Points(data.getInt(i));
                    break;
                case DistrictTeamsTable.CMP_KEY:
                    districtTeam.setCmpKey(data.getString(i));
                    break;
                case DistrictTeamsTable.CMP_POINTS:
                    districtTeam.setCmpPoints(data.getInt(i));
                    break;
                case DistrictTeamsTable.ROOKIE_POINTS:
                    districtTeam.setRookiePoints(data.getInt(i));
                    break;
                case DistrictTeamsTable.TOTAL_POINTS:
                    districtTeam.setTotalPoints(data.getInt(i));
                    break;
                case DistrictTeamsTable.JSON:
                    districtTeam.setJson(data.getString(i));
                    break;
                default:
            }
        }
        return districtTeam;
    }

    public static Favorite inflateFavorite(Cursor data) {
        Favorite favorite = new Favorite();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case FavoritesTable.MODEL_KEY:
                    favorite.setModelKey(data.getString(i));
                    break;
                case FavoritesTable.USER_NAME:
                    favorite.setUserName(data.getString(i));
                    break;
                case FavoritesTable.MODEL_ENUM:
                    favorite.setModelEnum(data.getInt(i));
                    break;
                default:
            }
        }
        return favorite;
    }

    public static Subscription inflateSubscription(Cursor data) {
        Subscription subscription = new Subscription();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case SubscriptionsTable.MODEL_KEY:
                    subscription.setModelKey(data.getString(i));
                    break;
                case SubscriptionsTable.USER_NAME:
                    subscription.setUserName(data.getString(i));
                    break;
                case SubscriptionsTable.MODEL_ENUM:
                    subscription.setModelEnum(data.getInt(i));
                    break;
                case SubscriptionsTable.NOTIFICATION_SETTINGS:
                    Log.d(Constants.LOG_TAG, "Settings: " + data.getString(i));
                    subscription.setNotificationSettings(data.getString(i));
                    break;
                default:
            }
        }
        return subscription;
    }

    public static StoredNotification inflateStoredNotification(Cursor data) {
        StoredNotification storedNotification = new StoredNotification();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case NotificationsTable.ID:
                    storedNotification.setId(data.getInt(i));
                    break;
                case NotificationsTable.TYPE:
                    storedNotification.setType(data.getString(i));
                    break;
                case NotificationsTable.TITLE:
                    storedNotification.setTitle(data.getString(i));
                    break;
                case NotificationsTable.BODY:
                    storedNotification.setBody(data.getString(i));
                    break;
                case NotificationsTable.INTENT:
                    storedNotification.setIntent(data.getString(i));
                    break;
                case NotificationsTable.TIME:
                    storedNotification.setTime(new Date(data.getLong(i)));
                    break;
                case NotificationsTable.SYSTEM_ID:
                    storedNotification.setSystemId(data.getInt(i));
                    break;
                case NotificationsTable.ACTIVE:
                    storedNotification.setActive(data.getInt(i));
                    break;
            }
        }
        return storedNotification;
    }
}
