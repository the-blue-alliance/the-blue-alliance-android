package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit.http.Header;
import retrofit.http.Path;
import rx.Observable;

public class APICache implements APIv2 {

    @Override public Observable<List<Team>> fetchTeamPage(
            @Path("pageNum") int pageNum,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<Team> fetchTeam(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Event>> fetchTeamEvents(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Award>> fetchTeamAtEventAwards(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Match>> fetchTeamAtEventMatches(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Integer>> fetchTeamYearsParticipated(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Media>> fetchTeamMediaInYear(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Event>> fetchTeamEventHistory(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Award>> fetchTeamEventAwards(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Event>> fetchEventsInYear(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<Event> fetchEvent(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Team>> fetchEventTeams(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<JsonArray> fetchEventRankings(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Match>> fetchEventMatches(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<JsonObject> fetchEventStats(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Award>> fetchEventAwards(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<JsonObject> fetchEventDistrictPoints(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<District>> fetchDistrictList(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<List<Event>> fetchDistrictEvents(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<JsonArray> fetchDistrictRankings(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }

    @Override public Observable<Match> fetchMatch(
            @Path("matchKey") String matchKey,
            @Header("If-Modified-Since") String ifModifiedSince) {
        return null;
    }
}
