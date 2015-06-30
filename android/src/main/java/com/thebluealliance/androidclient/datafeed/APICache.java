package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class APICache implements APIv2 {

    private Database mDb;

    @Inject
    public APICache(Database db) {
       mDb = db;
    }

    @Override
    public Observable<List<Team>> fetchTeamPage(
      int pageNum) {
        return null;
    }

    @Override
    public Observable<Team> fetchTeam(
      String teamKey) {
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
        return null;
    }

    @Override
    public Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey) {
        return null;
    }

    @Override
    public Observable<List<Integer>> fetchTeamYearsParticipated(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year) {
        String where = Database.Medias.TEAMKEY + " = ? AND " + Database.Medias.YEAR + " = ?";
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
        return null;
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
              .get(eventKey, new String[]{Database.Events.RANKINGS});
            if (event != null) {
                return Observable.just(event.getRankings());
            }
        } catch (BasicModel.FieldNotDefinedException e) {

        }
        return Observable.just(null);
    }

    @Override
    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        String where = String.format("%1$s = ?", Database.Matches.EVENT);
        return Observable.just(
          mDb.getMatchesTable().getForQuery(null, where, new String[]{eventKey}));
    }

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        try {
            Event event = mDb.getEventsTable()
              .get(eventKey, new String[]{Database.Events.STATS});
            if (event != null) {
                return Observable.just(event.getStats());
            }
        } catch (BasicModel.FieldNotDefinedException e) {

        }
        return Observable.just(null);
    }

    @Override
    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        String where = String.format("%1$s = ?", Database.Awards.EVENTKEY);
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
        return null;
    }

    @Override
    public Observable<List<Event>> fetchDistrictEvents(String districtShort, int year) {
        return null;
    }

    @Override
    public Observable<JsonArray> fetchDistrictRankings(String districtShort, int year) {
        return null;
    }

    @Override
    public Observable<Match> fetchMatch(String matchKey) {
        return null;
    }
}
