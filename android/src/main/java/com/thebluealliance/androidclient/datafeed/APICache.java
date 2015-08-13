package com.thebluealliance.androidclient.datafeed;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.models.Team;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class APICache implements Datafeed {

    private Database mDb;

    @Inject
    public APICache(Database db) {
        mDb = db;
    }

    @Override
    public Observable<List<Team>> fetchTeamPage(int pageNum) {
        String where = String.format("%1$s >= ? AND %1$s <= ?", TeamsTable.NUMBER);
        int startNum = 500 * pageNum;
        return Observable.create((observer) -> {
            try {
                List<Team> teams = mDb.getTeamsTable().getForQuery(null, where, new String[]{Integer.toString(startNum), Integer.toString(startNum + 499)});
                if (!observer.isUnsubscribed()) {
                    observer.onNext(teams);
                    observer.onCompleted();
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
        //return Observable.just(mDb.getTeamsTable().getForQuery(null, where, new String[]{Integer.toString(startNum), Integer.toString(startNum + 499)}));
    }

    @Override
    public Observable<Team> fetchTeam(String teamKey) {
        Team team = mDb.getTeamsTable().get(teamKey);
        return Observable.just(team);
    }

    @Override
    public Observable<List<Event>> fetchTeamEvents(String teamKey, int year) {
        List<Event> events = mDb.getEventTeamsTable().getEvents(teamKey, year);
        return Observable.just(events);
    }

    @Override
    public Observable<List<Award>> fetchTeamAtEventAwards(String teamKey, String eventKey) {
        return Observable.just(mDb.getAwardsTable().getTeamAtEventAwards(teamKey, eventKey));
    }

    @Override
    public Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey) {
        return Observable.just(mDb.getMatchesTable().getTeamAtEventMatches(teamKey, eventKey));
    }

    @Override
    public Observable<JsonArray> fetchTeamYearsParticipated(String teamKey) {
        Team team = mDb.getTeamsTable().get(teamKey);
        JsonArray years;
        try {
            years = team == null ? new JsonArray() : team.getYearsParticipated();
        } catch (BasicModel.FieldNotDefinedException e) {
            years = new JsonArray();
        }
        return Observable.just(years);
    }

    @Override
    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year) {
        String where = MediasTable.TEAMKEY + " = ? AND " + MediasTable.YEAR + " = ?";
        return Observable.just(mDb.getMediasTable().getForQuery(null, where, new String[]{teamKey,
          Integer.toString(year)}));
    }

    @Override
    public Observable<List<Event>> fetchTeamEventHistory(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Award>> fetchTeamEventAwards(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Event>> fetchEventsInYear(int year) {
        String where = String.format("%1$s = ?", EventsTable.YEAR);
        return Observable.create((observer) -> {
            try {
                List<Event> events = mDb.getEventsTable().getForQuery(null, where, new String[]{Integer.toString(year)});
                if (!observer.isUnsubscribed()) {
                    observer.onNext(events);
                    observer.onCompleted();
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
        //return Observable.just(mDb.getEventsTable().getForQuery(null, where, new String[]{Integer.toString(year)}));
    }

    public Observable<List<Event>> fetchEventsInWeek(int year, int week) {
        String where =
                String.format("%1$s = ? AND %2$s = ?", EventsTable.YEAR, EventsTable.WEEK);
        return Observable.just(mDb.getEventsTable()
          .getForQuery(null, where, new String[]{Integer.toString(year), Integer.toString(week)}));
    }

    public Observable<List<Event>> fetchEventsInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        String start = Long.toString(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String end = Long.toString(cal.getTimeInMillis());

        String where = String.format(
                "%1$s >= ? AND %2$s < ?",
                EventsTable.START,
                EventsTable.END);
        return Observable.just(mDb.getEventsTable()
          .getForQuery(null, where, new String[]{start, end}));
    }

    @Override
    public Observable<Event> fetchEvent(String eventKey) {
        Event event = mDb.getEventsTable().get(eventKey);
        return Observable.just(event);
    }

    @Override
    public Observable<List<Team>> fetchEventTeams(String eventKey) {
        List<Team> teams = mDb.getEventTeamsTable().getTeams(eventKey);
        return Observable.just(teams);
    }

    @Override
    public Observable<JsonArray> fetchEventRankings(String eventKey) {
        try {
            Event event = mDb.getEventsTable()
                    .get(eventKey, new String[]{EventsTable.RANKINGS});
            if (event != null) {
                return Observable.just(event.getRankings());
            }
        } catch (BasicModel.FieldNotDefinedException e) {

        }
        return Observable.just(null);
    }

    @Override
    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        String where = String.format("%1$s = ?", MatchesTable.EVENT);
        return Observable.just(
          mDb.getMatchesTable().getForQuery(null, where, new String[]{eventKey}));
    }

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        try {
            Event event = mDb.getEventsTable()
                    .get(eventKey, new String[]{EventsTable.STATS});
            if (event != null) {
                return Observable.just(event.getStats());
            }
        } catch (BasicModel.FieldNotDefinedException e) {

        }
        return Observable.just(null);
    }

    @Override
    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        String where = String.format("%1$s = ?", AwardsTable.EVENTKEY);
        return Observable.just(
          mDb.getAwardsTable().getForQuery(null, where, new String[]{eventKey}));
    }

    @Override
    public Observable<JsonObject> fetchEventDistrictPoints(String eventKey) {
        Event event = mDb.getEventsTable().get(eventKey);
        try {
            return Observable.just(event.getDistrictPoints());
        } catch (BasicModel.FieldNotDefinedException e) {
            return Observable.just(null);
        }
    }

    @Override
    public Observable<List<District>> fetchDistrictList(int year) {
        String where = String.format("%1$s = ?", DistrictsTable.YEAR);
        return Observable.just(
          mDb.getDistrictsTable().getForQuery(null, where, new String[]{Integer.toString(year)}));
    }

    @Override
    public Observable<List<Event>> fetchDistrictEvents(String districtShort, int year) {
        String where =
                String.format("$1%s = ? AND %2$s = ?", EventsTable.YEAR, EventsTable.DISTRICT);
        int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtShort).ordinal();
        return Observable.just(mDb.getEventsTable().getForQuery(
          null,
          where,
          new String[]{Integer.toString(year), Integer.toString(districtEnum)}));
    }

    @Override
    public Observable<List<DistrictTeam>> fetchDistrictRankings(String districtShort, int year) {
        String where = String.format(
                "%1$s = ? AND %2$s = ?",
                DistrictTeamsTable.YEAR,
                DistrictTeamsTable.DISTRICT_ENUM);
        int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtShort).ordinal();
        return Observable.just(mDb.getDistrictTeamsTable().getForQuery(
          null,
          where,
          new String[]{Integer.toString(year), Integer.toString(districtEnum)}));
    }

    @Override
    public Observable<Match> fetchMatch(String matchKey) {
        return Observable.just(mDb.getMatchesTable().get(matchKey));
    }

    public Observable<List<Subscription>> fetchUserSubscriptions(Context context) {
        String account = AccountHelper.getSelectedAccount(context);
        return Observable.just(mDb.getSubscriptionsTable().getForUser(account));
    }

    public Observable<List<Favorite>> fetchUserFavorites(Context context) {
        String account = AccountHelper.getSelectedAccount(context);
        return Observable.just(mDb.getFavoritesTable().getForUser(account));
    }
}
