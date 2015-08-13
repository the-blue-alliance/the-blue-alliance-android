package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import rx.Observable;

/**
 * Interface for a Datafeed class to implement
 * Should mirror {@link APIv2}, except these methods should return the generic type of
 * its {@link retrofit.Response} return types and the annotations are unnecessary
 */
public interface Datafeed {

    Observable<List<Team>> fetchTeamPage(int pageNum);

    Observable<Team> fetchTeam(String teamKey);

    Observable<List<Event>> fetchTeamEvents(String teamKey, int year);

    Observable<List<Award>> fetchTeamAtEventAwards(String teamKey, String eventKey);

    Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey);

    Observable<JsonArray> fetchTeamYearsParticipated(String teamKey);

    Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year);

    Observable<List<Event>> fetchTeamEventHistory(String teamKey);

    Observable<List<Award>> fetchTeamEventAwards(String teamKey);

    Observable<List<Event>> fetchEventsInYear(int year);

    Observable<Event> fetchEvent(String eventKey);

    Observable<List<Team>> fetchEventTeams(String eventKey);

    Observable<JsonArray> fetchEventRankings(String eventKey);

    Observable<List<Match>> fetchEventMatches(String eventKey);

    Observable<JsonObject> fetchEventStats(String eventKey);

    Observable<List<Award>> fetchEventAwards(String eventKey);

    Observable<JsonObject> fetchEventDistrictPoints(String eventKey);

    Observable<List<District>> fetchDistrictList(int year);

    Observable<List<Event>> fetchDistrictEvents(String districtShort, int year);

    Observable<List<DistrictTeam>> fetchDistrictRankings(String districtShort, int year);

    Observable<Match> fetchMatch(String matchKey);
}