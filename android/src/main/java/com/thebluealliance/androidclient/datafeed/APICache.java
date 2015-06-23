package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import rx.Observable;

public class APICache implements APIv2 {

    @Inject Database mDb;

    public APICache() {
        ObjectGraph.create(new DatafeedModule()).inject(this);
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
    public Observable<List<Event>> fetchTeamEvents(
      String teamKey,
      int year) {
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
        return null;
    }

    @Override
    public Observable<List<Team>> fetchEventTeams(String eventKey) {
        return null;
    }

    @Override
    public Observable<JsonArray> fetchEventRankings(String eventKey) {
        return null;
    }

    @Override
    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        return null;
    }

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        return null;
    }

    @Override
    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        return null;
    }

    @Override
    public Observable<JsonObject> fetchEventDistrictPoints(String eventKey) {
        return null;
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
