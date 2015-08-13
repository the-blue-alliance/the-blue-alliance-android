package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.datafeed.maps.DistrictTeamExtractor;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.maps.TeamRankExtractor;
import com.thebluealliance.androidclient.datafeed.maps.TeamStatsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.WeekEventsExtractor;
import com.thebluealliance.androidclient.datafeed.maps.YearsParticipatedInfoMap;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
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
public class CacheableDatafeed implements Datafeed {

    private APIv2 mRetrofitAPI;
    private APICache mAPICache;
    private DatabaseWriter mWriter;
    private RetrofitResponseMap mResponseMap;

    @Inject
    public CacheableDatafeed(
            @Named("retrofit") APIv2 retrofitAPI,
            @Named("cache") APICache apiCache,
            DatabaseWriter writer,
            RetrofitResponseMap responseMap) {
        mRetrofitAPI = retrofitAPI;
        mAPICache = apiCache;
        mWriter = writer;
        mResponseMap = responseMap;
    }

    public APICache getCache() {
        return mAPICache;
    }

    @Override
    public Observable<List<Team>> fetchTeamPage(int pageNum) {
        Observable<List<Team>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeamPage(pageNum),
          mWriter.teamListWriter.get());
        return mAPICache.fetchTeamPage(pageNum).concatWith(apiData);
    }

    @Override
    public Observable<Team> fetchTeam(String teamKey) {
        Observable<Team> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeam(teamKey),
          mWriter.teamWriter.get());
        return mAPICache.fetchTeam(teamKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Event>> fetchTeamEvents(String teamKey, int year) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeamEvents(teamKey, year),
          mWriter.eventListWriter.get());
        return mAPICache.fetchTeamEvents(teamKey, year).concatWith(apiData);
    }

    public Observable<List<Event>> fetchEventsInWeek(int year, int week) {
        WeekEventsExtractor extractor = new WeekEventsExtractor(week);
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEventsInYear(year),
          mWriter.eventListWriter.get());
        return mAPICache.fetchEventsInWeek(year, week).concatWith(apiData.map(extractor));
    }

    @Override
    public Observable<List<Award>> fetchTeamAtEventAwards(String teamKey, String eventKey) {
        Observable<List<Award>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeamAtEventAwards(teamKey, eventKey),
          mWriter.awardListWriter.get());
        return mAPICache.fetchTeamAtEventAwards(teamKey, eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey) {
        Observable<List<Match>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeamAtEventMatches(teamKey, eventKey),
          mWriter.matchListWriter.get());
        return mAPICache.fetchTeamAtEventMatches(teamKey, eventKey).concatWith(apiData);
    }

    public Observable<JsonArray> fetchTeamAtEventRank(String teamKey, String eventKey) {
        TeamRankExtractor extractor = new TeamRankExtractor(teamKey);
        return fetchEventRankings(eventKey).map(extractor);
    }

    @Override
    public Observable<JsonArray> fetchTeamYearsParticipated(String teamKey) {
        Observable<JsonArray> apiData = mResponseMap.getAndWriteMappedResponseBody(
          mRetrofitAPI.fetchTeamYearsParticipated(teamKey),
          new YearsParticipatedInfoMap(teamKey),
          mWriter.yearsParticipatedWriter.get());
        return mAPICache.fetchTeamYearsParticipated(teamKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year) {
        Observable<List<Media>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchTeamMediaInYear(teamKey, year),
          mWriter.mediaListWriter.get());
        return mAPICache.fetchTeamMediaInYear(teamKey, year).concatWith(apiData);
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
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEventsInYear(year),
          mWriter.eventListWriter.get());
        return mAPICache.fetchEventsInYear(year).concatWith(apiData);
    }

    @Override
    public Observable<Event> fetchEvent(String eventKey) {
        Observable<Event> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEvent(eventKey),
          mWriter.eventWriter.get());
        return mAPICache.fetchEvent(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Team>> fetchEventTeams(String eventKey) {
        Observable<List<Team>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEventTeams(eventKey),
          mWriter.teamListWriter.get());
        return mAPICache.fetchEventTeams(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonArray> fetchEventRankings(String eventKey) {
        //TODO write the response to the db
        Observable<JsonArray> apiData = mResponseMap.getResponseBody(
          mRetrofitAPI.fetchEventRankings(eventKey));
        return mAPICache.fetchEventRankings(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        Observable<List<Match>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEventMatches(eventKey),
          mWriter.matchListWriter.get());
        return mAPICache.fetchEventMatches(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        //TODO write the response to the db
        Observable<JsonObject> apiData = mResponseMap.getResponseBody(
          mRetrofitAPI.fetchEventStats(eventKey));
        return mAPICache.fetchEventStats(eventKey).concatWith(apiData);
    }

    public Observable<JsonObject> fetchTeamAtEventStats(String eventKey, String teamKey) {
        TeamStatsExtractor extractor = new TeamStatsExtractor(teamKey);
        return fetchEventStats(eventKey).map(extractor);
    }

    @Override
    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        Observable<List<Award>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchEventAwards(eventKey),
          mWriter.awardListWriter.get());
        return mAPICache.fetchEventAwards(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonObject> fetchEventDistrictPoints(String eventKey) {
        //TODO write response to db
        Observable<JsonObject> apiData = mResponseMap.getResponseBody(
          mRetrofitAPI.fetchEventDistrictPoints(eventKey));
        return mAPICache.fetchEventDistrictPoints(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<District>> fetchDistrictList(int year) {
        Observable<List<District>> apiData = mResponseMap.mapAndWriteResponseBody(
          mRetrofitAPI.fetchDistrictList(year),
          new AddDistrictKeys(year),
          mWriter.districtListWriter.get());
        return mAPICache.fetchDistrictList(year).concatWith(apiData);
    }

    @Override
    public Observable<List<Event>> fetchDistrictEvents(String districtShort, int year) {
        Observable<List<Event>> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchDistrictEvents(districtShort, year),
          mWriter.eventListWriter.get());
        return mAPICache.fetchDistrictEvents(districtShort, year).concatWith(apiData);
    }

    @Override
    public Observable<List<DistrictTeam>> fetchDistrictRankings(String districtShort, int year) {
        Observable<List<DistrictTeam>> apiData = mResponseMap.mapAndWriteResponseBody(
          mRetrofitAPI.fetchDistrictRankings(districtShort, year),
          new AddDistrictTeamKey(districtShort, year),
          mWriter.districtTeamListWriter.get());
        return mAPICache.fetchDistrictRankings(districtShort, year).concatWith(apiData);
    }

    public Observable<DistrictTeam> fetchTeamAtDistrictRankings(
            String teamKey,
            String districtShort,
            int year) {
        return fetchDistrictRankings(districtShort, year).map(new DistrictTeamExtractor(teamKey));
    }

    @Override
    public Observable<Match> fetchMatch(String matchKey) {
        Observable<Match> apiData = mResponseMap.getAndWriteResponseBody(
          mRetrofitAPI.fetchMatch(matchKey),
          mWriter.matchWriter.get());
        return mAPICache.fetchMatch(matchKey).concatWith(apiData);
    }
}
