package com.thebluealliance.androidclient.database;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventDetailsTable;
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
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventDetail;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.MatchAlliancesContainer;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.api.model.IDistrictEventPoints;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ModelInflater {

    private ModelInflater() {
        // unused
    }

    /**
     * Inflate an award model from a single row of a cursor returned by a database query.
     *
     * @param data Cursor of data. Ensure that it's not null and is pointing to a valid row
     * @return Award model containing the fields as defined in the cursor
     */
    public static Award inflateAward(Cursor data, Gson gson) {
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
                    award.setRecipientList(gson.fromJson(data.getString(i), new
                            TypeToken<List<Award.AwardRecipient>>(){}.getType()));
                    break;
                case AwardsTable.KEY:
                    award.setKey(data.getString(i));
                    break;
                case AwardsTable.ENUM:
                    award.setEnum(data.getInt(i));
                    break;
                case AwardsTable.LAST_MODIFIED:
                    award.setLastModified(data.getLong(i));
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
                    event.setKey(data.getString(i));
                    break;
                case EventsTable.NAME:
                    event.setName(data.getString(i));
                    break;
                case EventsTable.SHORTNAME:
                    event.setShortName(data.getString(i));
                    break;
                case EventsTable.LOCATION:
                    event.setLocation(data.getString(i));
                    break;
                case EventsTable.CITY:
                    event.setCity(data.getString(i));
                    break;
                case EventsTable.VENUE:
                    event.setLocationName(data.getString(i));
                    break;
                case EventsTable.ADDRESS:
                    event.setAddress(data.getString(i));
                    break;
                case EventsTable.WEBSITE:
                    event.setWebsite(data.getString(i));
                    break;
                case EventsTable.TYPE:
                    event.setEventType(data.getInt(i));
                    break;
                case EventsTable.DISTRICT_KEY:
                    event.setDistrictKey(data.getString(i));
                    break;
                case EventsTable.START:
                    event.setStartDate(data.getLong(i));
                    break;
                case EventsTable.END:
                    event.setEndDate(data.getLong(i));
                    break;
                case EventsTable.WEEK:
                    event.setWeek(data.getInt(i));
                    break;
                case EventsTable.WEBCASTS:
                    event.setWebcasts(data.getString(i));
                    break;
                case EventsTable.LAST_MODIFIED:
                    event.setLastModified(data.getLong(i));
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
    public static Match inflateMatch(Cursor data, Gson gson) {
        Match match = new Match();
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case MatchesTable.KEY:
                    match.setKey(data.getString(i));
                    match.setCompLevel(MatchType.fromKey(match.getKey()).getCompLevel());
                    break;
                case MatchesTable.TIME:
                    match.setTime(data.getLong(i));
                    break;
                case MatchesTable.ALLIANCES:
                    match.setAlliances(gson.fromJson(data.getString(i), MatchAlliancesContainer.class));
                    break;
                case MatchesTable.WINNER:
                    match.setWinningAlliance(data.getString(i));
                    break;
                case MatchesTable.VIDEOS:
                    match.setVideos(gson.fromJson(data.getString(i), new TypeToken<List<Match.MatchVideo>>(){}.getType()));
                    break;
                case MatchesTable.MATCHNUM:
                    match.setMatchNumber(data.getInt(i));
                    break;
                case MatchesTable.SETNUM:
                    match.setSetNumber(data.getInt(i));
                    break;
                case MatchesTable.BREAKDOWN:
                    match.setScoreBreakdown(data.getString(i));
                    break;
                case MatchesTable.LAST_MODIFIED:
                    match.setLastModified(data.getLong(i));
                    break;
                case MatchesTable.EVENT:
                    match.setEventKey(data.getString(i));
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
                    media.setType(data.getString(i));
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
                case MediasTable.LAST_MODIFIED:
                    media.setLastModified(data.getLong(i));
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
                    team.setKey(data.getString(i));
                    break;
                case TeamsTable.NUMBER:
                    team.setTeamNumber(data.getInt(i));
                    break;
                case TeamsTable.SHORTNAME:
                    team.setNickname(data.getString(i));
                    break;
                case TeamsTable.NAME:
                    team.setName(data.getString(i));
                    break;
                case TeamsTable.LOCATION:
                    team.setLocation(data.getString(i));
                    break;
                case TeamsTable.ADDRESS:
                    team.setAddress(data.getString(i));
                    break;
                case TeamsTable.LOCATION_NAME:
                    team.setLocationName(data.getString(i));
                    break;
                case TeamsTable.WEBSITE:
                    team.setWebsite(data.getString(i));
                    break;
                case TeamsTable.YEARS_PARTICIPATED:
                    team.setYearsParticipated(data.getString(i));
                    break;
                case TeamsTable.MOTTO:
                    team.setMotto(data.getString(i));
                    break;
                case TeamsTable.LAST_MODIFIED:
                    team.setLastModified(data.getLong(i));
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
    public static EventTeam inflateEventTeam(Cursor data, Gson gson) {
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
                case EventTeamsTable.KEY:
                    eventTeam.setKey(data.getString(i));
                    break;
                case EventTeamsTable.STATUS:
                    eventTeam.setStatus(gson.fromJson(data.getString(i), TeamAtEventStatus.class));
                    break;
                case EventTeamsTable.LAST_MODIFIED:
                    eventTeam.setLastModified(data.getLong(i));
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
                case DistrictsTable.YEAR:
                    district.setYear(data.getInt(i));
                    break;
                case DistrictsTable.NAME:
                    district.setDisplayName(data.getString(i));
                    break;
                case DistrictsTable.LAST_MODIFIED:
                    district.setLastModified(data.getLong(i));
                    break;
                default:
            }
        }
        return district;
    }

    public static DistrictRanking inflateDistrictTeam(Cursor data, Gson gson) {
        DistrictRanking districtTeam = new DistrictRanking();
        IDistrictEventPoints[] events = new IDistrictEventPoints[3];
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
                case DistrictTeamsTable.RANK:
                    districtTeam.setRank(data.getInt(i));
                    break;
                case DistrictTeamsTable.EVENT1_POINTS:
                    events[0] = gson.fromJson(data.getString(i), IDistrictEventPoints.class);
                    break;
                case DistrictTeamsTable.EVENT2_POINTS:
                    events[1] = gson.fromJson(data.getString(i), IDistrictEventPoints.class);
                    break;
                case DistrictTeamsTable.CMP_POINTS:
                    events[2] = gson.fromJson(data.getString(i), IDistrictEventPoints.class);
                    break;
                case DistrictTeamsTable.ROOKIE_POINTS:
                    districtTeam.setRookieBonus(data.getInt(i));
                    break;
                case DistrictTeamsTable.TOTAL_POINTS:
                    districtTeam.setPointTotal(data.getInt(i));
                    break;
                case DistrictTeamsTable.LAST_MODIFIED:
                    districtTeam.setLastModified(data.getLong(i));
                    break;
                default:
            }
        }
        List<IDistrictEventPoints> eventPoints = new ArrayList<>();
        for (IDistrictEventPoints event : events) {
            if (event == null) break;
            eventPoints.add(event);
        }
        districtTeam.setEventPoints(eventPoints);
        return districtTeam;
    }

    public static EventDetail inflateEventDetail(Cursor data) {
        int eventKeyIndex = data.getColumnIndex(EventDetailsTable.EVENT_KEY);
        int typeIndex = data.getColumnIndex(EventDetailsTable.DETAIL_TYPE);
        EventDetail detail = new EventDetail(data.getString(eventKeyIndex), data.getInt(typeIndex));
        for (int i = 0; i < data.getColumnCount(); i++) {
            switch (data.getColumnName(i)) {
                case EventDetailsTable.JSON_DATA:
                    detail.setJsonData(data.getString(i));
                    break;
                case EventDetailsTable.LAST_MODIFIED:
                    detail.setLastModified(data.getLong(i));
                    break;
            }
        }
        return detail;
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
                    TbaLogger.d("Settings: " + data.getString(i));
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
                case NotificationsTable.MSG_DATA:
                    storedNotification.setMessageData(data.getString(i));
                    break;
            }
        }
        return storedNotification;
    }
}
