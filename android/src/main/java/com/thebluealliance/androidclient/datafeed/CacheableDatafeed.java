package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.combiners.JsonAndKeyCombiner;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAndEventTeamCombiner;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.datafeed.maps.DistrictTeamExtractor;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.maps.TeamRankExtractor;
import com.thebluealliance.androidclient.datafeed.maps.TeamStatsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.WeekEventsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.YearsParticipatedInfoMap;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class CacheableDatafeed {

    private TbaApiV3 mApiv3;
    private APICache mAPICache;
    private DatabaseWriter mWriter;
    private RetrofitResponseMap mResponseMap;

    @Inject
    public CacheableDatafeed(
      @Named("tba_apiv3") TbaApiV3 apiv3,
      @Named("cache") APICache apiCache,
      DatabaseWriter writer,
      RetrofitResponseMap responseMap) {
        mApiv3 = apiv3;
        mAPICache = apiCache;
        mWriter = writer;
        mResponseMap = responseMap;
    }

    public APICache getCache() {
        return mAPICache;
    }

    public Observable<List<Team>> fetchTeamPage(int pageNum, String cacheHeader) {
        Observable<List<Team>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeamPage(pageNum, cacheHeader),
          mWriter.getTeamListWriter().get());
        return mAPICache.fetchTeamPage(pageNum).concatWith(apiData);
    }

    public Observable<Team> fetchTeam(String teamKey, String cacheHeader) {
        Observable<Team> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeam(teamKey, cacheHeader),
          mWriter.getTeamWriter().get());
        return mAPICache.fetchTeam(teamKey).concatWith(apiData);
    }

    public Observable<List<Event>> fetchTeamEvents(String teamKey, int year, String cacheHeader) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeamEvents(teamKey, year, cacheHeader),
          mWriter.getEventListWriter().get());
        return mAPICache.fetchTeamEvents(teamKey, year).concatWith(apiData);
    }

    public Observable<List<Event>> fetchEventsInWeek(int year, int week, String cacheHeader) {
        WeekEventsExtractor extractor = new WeekEventsExtractor(week);
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchEventsInYear(year, cacheHeader),
          mWriter.getEventListWriter().get(),
          Database.TABLE_EVENTS, EventsTable.WEEK + " = ?", new String[]{Integer.toString(week)});
        return mAPICache.fetchEventsInWeek(year, week).concatWith(apiData.map(extractor));
    }

    public Observable<List<Award>> fetchTeamAtEventAwards(
      String teamKey,
      String eventKey,
      String cacheHeader) {
        Observable<List<Award>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeamAtEventAwards(teamKey, eventKey, cacheHeader),
          mWriter.getAwardListWriter().get());
        return mAPICache.fetchTeamAtEventAwards(teamKey, eventKey).concatWith(apiData);
    }

    public Observable<List<Match>> fetchTeamAtEventMatches(
      String teamKey,
      String eventKey,
      String cacheHeader) {
        Observable<List<Match>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeamAtEventMatches(teamKey, eventKey, cacheHeader),
          mWriter.getMatchListWriter().get());
        return mAPICache.fetchTeamAtEventMatches(teamKey, eventKey).concatWith(apiData);
    }

    public Observable<JsonArray> fetchTeamAtEventRank(
      String teamKey,
      String eventKey,
      String cacheHeader) {
        TeamRankExtractor extractor = new TeamRankExtractor(teamKey);
        return fetchEventRankings(eventKey, cacheHeader).map(extractor);
    }

    public Observable<List<Integer>> fetchTeamYearsParticipated(String teamKey, String cacheHeader) {
        Observable<List<Integer>> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchTeamYearsParticipated(teamKey, cacheHeader),
          new YearsParticipatedInfoMap(teamKey),
          mWriter.getYearsParticipatedWriter().get());
        return mAPICache.fetchTeamYearsParticipated(teamKey).concatWith(apiData);
    }

    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year, String cacheHeader) {
        Observable<List<Media>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchTeamMediaInYear(teamKey, year, cacheHeader),
          mWriter.getMediaListWriter().get());
        return mAPICache.fetchTeamMediaInYear(teamKey, year).concatWith(apiData);
    }

    public Observable<List<Event>> fetchEventsInYear(int year, String cacheHeader) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchEventsInYear(year, cacheHeader),
          mWriter.getEventListWriter().get(),
          Database.TABLE_EVENTS, EventsTable.YEAR + " = ?", new String[]{Integer.toString(year)});
        return mAPICache.fetchEventsInYear(year).concatWith(apiData);
    }

    public Observable<Event> fetchEvent(String eventKey, String cacheHeader) {
        Observable<Event> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchEvent(eventKey, cacheHeader),
          mWriter.getEventWriter().get());
        return mAPICache.fetchEvent(eventKey).concatWith(apiData);
    }

    public Observable<List<Team>> fetchEventTeams(String eventKey, String cacheHeader) {
        Observable<List<Team>> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchEventTeams(eventKey, cacheHeader),
          new TeamAndEventTeamCombiner(eventKey),
          mWriter.getEventTeamAndTeamListWriter().get());
        return mAPICache.fetchEventTeams(eventKey).concatWith(apiData);
    }

    public Observable<? extends JsonElement> fetchEventRankings(String eventKey, String cacheHeader) {
        //TODO EventDetail
        /*Observable<JsonElement> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchEventRankings(eventKey, cacheHeader),
          new JsonAndKeyCombiner(eventKey),
          mWriter.getEventRankingsWriter().get());
        return mAPICache.fetchEventRankings(eventKey).concatWith(apiData);*/
        return Observable.just(JsonNull.INSTANCE);
    }

    public Observable<List<Match>> fetchEventMatches(String eventKey, String cacheHeader) {
        Observable<List<Match>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchEventMatches(eventKey, cacheHeader),
          mWriter.getMatchListWriter().get(),
          Database.TABLE_MATCHES, MatchesTable.EVENT + " = ?", new String[]{eventKey});
        return mAPICache.fetchEventMatches(eventKey).concatWith(apiData);
    }

    public Observable<? extends JsonElement> fetchEventStats(String eventKey, String cacheHeader) {
        Observable<JsonElement> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchEventOPR(eventKey, cacheHeader),
          new JsonAndKeyCombiner(eventKey),
          mWriter.getEventStatsWriter().get());
        return mAPICache.fetchEventStats(eventKey).concatWith(apiData);
    }

    public Observable<? extends JsonElement> fetchTeamAtEventStats(
      String eventKey,
      String teamKey,
      String cacheHeader) {
        TeamStatsExtractor extractor = new TeamStatsExtractor(teamKey);
        return fetchEventStats(eventKey, cacheHeader).map(extractor);
    }

    public Observable<List<Award>> fetchEventAwards(String eventKey, String cacheHeader) {
        Observable<List<Award>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchEventAwards(eventKey, cacheHeader),
          mWriter.getAwardListWriter().get());
        return mAPICache.fetchEventAwards(eventKey).concatWith(apiData);
    }

    public Observable<? extends JsonElement> fetchEventDistrictPoints(String eventKey, String cacheHeader) {
        Observable<JsonElement> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchEventDistrictPoints(eventKey, cacheHeader),
          new JsonAndKeyCombiner(eventKey),
          mWriter.getEventDistrictPointsWriter().get());
        return mAPICache.fetchEventDistrictPoints(eventKey).concatWith(apiData);
    }

    public Observable<List<District>> fetchDistrictList(int year, String cacheHeader) {
        Observable<List<District>> apiData = mResponseMap.mapAndWriteResponseBody(
          mApiv3.fetchDistrictList(year, cacheHeader),
          new AddDistrictKeys(year),
          mWriter.getDistrictListWriter().get());
        return mAPICache.fetchDistrictList(year).concatWith(apiData);
    }

    public Observable<List<Event>> fetchDistrictEvents(
      String districtShort,
      int year,
      String cacheHeader) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchDistrictEvents(DistrictHelper.generateKey(districtShort, year), cacheHeader),
          mWriter.getEventListWriter().get());
        return mAPICache.fetchDistrictEvents(districtShort, year).concatWith(apiData);
    }

    public Observable<List<DistrictTeam>> fetchDistrictRankings(String districtShort, int year, String cacheHeader) {
        /* TODO
        Observable<List<DistrictTeam>> apiData = mResponseMap.mapAndWriteResponseBody(
          mApiv3.fetchDistrictRankings(DistrictHelper.generateKey(districtShort, year), cacheHeader),
          new AddDistrictTeamKey(districtShort, year),
          mWriter.getDistrictTeamListWriter().get());
        return mAPICache.fetchDistrictRankings(districtShort, year).concatWith(apiData);
        */
        return Observable.just(null);
    }

    public Observable<DistrictTeam> fetchTeamAtDistrictRankings(
      String teamKey,
      String districtShort,
      int year, String cacheHeader) {
        return fetchDistrictRankings(districtShort, year, cacheHeader)
          .map(new DistrictTeamExtractor(teamKey));
    }

    public Observable<Match> fetchMatch(String matchKey, String cacheHeader) {
        Observable<Match> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchMatch(matchKey, cacheHeader),
          mWriter.getMatchWriter().get());
        return mAPICache.fetchMatch(matchKey).concatWith(apiData);
    }
}
