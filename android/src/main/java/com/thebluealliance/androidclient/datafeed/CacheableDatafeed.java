package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.EventDetailsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAndEventTeamCombiner;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.datafeed.maps.AddEventKeyToRankings;
import com.thebluealliance.androidclient.datafeed.maps.AllianceEventKeyAdder;
import com.thebluealliance.androidclient.datafeed.maps.DistrictTeamExtractor;
import com.thebluealliance.androidclient.datafeed.maps.EventAlliancesToEventDetail;
import com.thebluealliance.androidclient.datafeed.maps.JsonToEventDetail;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.maps.TeamAtEventStatusExtractor;
import com.thebluealliance.androidclient.datafeed.maps.TeamAtEventStatusToEventTeam;
import com.thebluealliance.androidclient.datafeed.maps.TeamMediaKeyAdder;
import com.thebluealliance.androidclient.datafeed.maps.TeamStatsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.WeekEventsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.YearsParticipatedInfoMap;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.EventDetail;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.types.EventDetailType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import thebluealliance.api.model.District;

@Singleton
public class CacheableDatafeed {

    private final TbaApiV3 mApiv3;
    private final APICache mAPICache;
    private final DatabaseWriter mWriter;
    private final Gson mGson;
    private final RetrofitResponseMap mResponseMap;

    @Inject
    public CacheableDatafeed(
      @Named("tba_apiv3") TbaApiV3 apiv3,
      @Named("cache") APICache apiCache,
      DatabaseWriter writer,
      Gson gson,
      RetrofitResponseMap responseMap) {
        mApiv3 = apiv3;
        mAPICache = apiCache;
        mWriter = writer;
        mGson = gson;
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

    public Observable<TeamAtEventStatus> fetchTeamAtEventStatus(
            String teamKey,
            String eventKey,
            String cacheHeader) {
        Observable<TeamAtEventStatus> apiData = mResponseMap.getAndWriteMappedResponseBody(
                mApiv3.fetchTeamAtEventStatus(teamKey, eventKey, cacheHeader),
                new TeamAtEventStatusToEventTeam(teamKey, eventKey),
                mWriter.getEventTeamWriter().get());
        return mAPICache.fetchEventTeam(teamKey, eventKey)
                        .map(new TeamAtEventStatusExtractor())
                        .concatWith(apiData);
    }

    public Observable<List<Integer>> fetchTeamYearsParticipated(String teamKey, String cacheHeader) {
        Observable<List<Integer>> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mApiv3.fetchTeamYearsParticipated(teamKey, cacheHeader),
          new YearsParticipatedInfoMap(teamKey),
          mWriter.getYearsParticipatedWriter().get());
        return mAPICache.fetchTeamYearsParticipated(teamKey).concatWith(apiData);
    }

    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year, String cacheHeader) {
        Observable<List<Media>> apiData = mResponseMap.mapAndWriteResponseBody(
          mApiv3.fetchTeamMediaInYear(teamKey, year, cacheHeader),
          new TeamMediaKeyAdder(teamKey, year),
          mWriter.getMediaListWriter().get());
        return mAPICache.fetchTeamMediaInYear(teamKey, year).concatWith(apiData);
    }

    public Observable<List<Media>> fetchTeamSocialMedia(String teamKey, String cacheHeader) {
        Observable<List<Media>> apiData = mResponseMap.mapAndWriteResponseBody(
                mApiv3.fetchTeamSocialMedia(teamKey, cacheHeader),
                new TeamMediaKeyAdder(teamKey, -1),  // Social media don't have a specific year
                mWriter.getMediaListWriter().get());
        return mAPICache.fetchTeamSocialMedia(teamKey).concatWith(apiData);
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

    public Observable<RankingResponseObject> fetchEventRankings(String eventKey, String cacheHeader) {
        Observable<RankingResponseObject> apiData = mResponseMap.getAndWriteMappedResponseBody(
                mApiv3.fetchEventRankings(eventKey, cacheHeader),
                new AddEventKeyToRankings(eventKey, mGson),
                mWriter.getEventDetailWriter().get(),
                Database.TABLE_EVENTDETAILS, EventDetailsTable.KEY + " = ?", new
                        String[]{EventDetail.buildKey(eventKey, EventDetailType.RANKINGS)}
        ).map(new AddEventKeyToRankings.ApiMap(eventKey));
        return mAPICache.fetchEventRankings(eventKey).concatWith(apiData);
    }

    public Observable<List<EventAlliance>> fetchEventAlliances(String eventKey, String cacheHeader) {
        Observable<List<EventAlliance>> apiData = mResponseMap.getAndWriteMappedResponseBody(
                mApiv3.fetchEventAlliances(eventKey, cacheHeader),
                new EventAlliancesToEventDetail(eventKey, mGson),
                mWriter.getEventDetailWriter().get(),
                Database.TABLE_EVENTDETAILS, EventDetailsTable.KEY + " = ?", new
                        String[]{EventDetail.buildKey(eventKey, EventDetailType.RANKINGS)}
        ).map(new AllianceEventKeyAdder(eventKey));
        return mAPICache.fetchEventAlliances(eventKey).concatWith(apiData);
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
          new JsonToEventDetail(eventKey, EventDetailType.OPRS),
          mWriter.getEventDetailWriter().get());
        return mAPICache.fetchJsonEventDetail(eventKey, EventDetailType.OPRS).concatWith(apiData);
    }

    public Observable<? extends JsonElement> fetchEventInsights(String eventKey, String cacheHeader) {
        Observable<JsonElement> apiData = mResponseMap.getAndWriteMappedResponseBody(
                mApiv3.fetchEventInsights(eventKey, cacheHeader),
                new JsonToEventDetail(eventKey, EventDetailType.INSIGHTS),
                mWriter.getEventDetailWriter().get()
        );
        return mAPICache.fetchJsonEventDetail(eventKey, EventDetailType.INSIGHTS).concatWith(apiData);
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
          new JsonToEventDetail(eventKey, EventDetailType.DISTRICT_POINTS),
          mWriter.getEventDetailWriter().get());
        return mAPICache.fetchJsonEventDetail(eventKey, EventDetailType.DISTRICT_POINTS).concatWith(apiData);
    }

    public Observable<List<District>> fetchDistrictList(int year, String cacheHeader) {
        Observable<List<District>> apiData = mResponseMap.mapAndWriteResponseBody(
          mApiv3.fetchDistrictList(year, cacheHeader),
          new AddDistrictKeys(year),
          mWriter.getDistrictListWriter().get());
        return mAPICache.fetchDistrictList(year).concatWith(apiData);
    }

    public Observable<List<Event>> fetchDistrictEvents(String districtKey, String cacheHeader) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchDistrictEvents(districtKey, cacheHeader),
          mWriter.getEventListWriter().get());
        return mAPICache.fetchDistrictEvents(districtKey).concatWith(apiData);
    }

    public Observable<List<DistrictRanking>> fetchDistrictRankings(String districtKey, String cacheHeader) {
        Observable<List<DistrictRanking>> apiData = mResponseMap.mapAndWriteResponseBody(
          mApiv3.fetchDistrictRankings(districtKey, cacheHeader),
          new AddDistrictTeamKey(districtKey),
          mWriter.getDistrictTeamListWriter().get());
        return mAPICache.fetchDistrictRankings(districtKey).concatWith(apiData);
    }

    public Observable<DistrictRanking> fetchTeamAtDistrictRankings(
      String teamKey,
      String districtKey, String cacheHeader) {
        return fetchDistrictRankings(districtKey, cacheHeader)
          .map(new DistrictTeamExtractor(teamKey));
    }

    public Observable<Match> fetchMatch(String matchKey, String cacheHeader) {
        Observable<Match> apiData = mResponseMap.getAndWriteResponseBody(
          mApiv3.fetchMatch(matchKey, cacheHeader),
          mWriter.getMatchWriter().get());
        return mAPICache.fetchMatch(matchKey).concatWith(apiData);
    }
}
