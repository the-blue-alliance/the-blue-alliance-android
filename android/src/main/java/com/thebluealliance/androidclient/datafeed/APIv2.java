package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.mime.TypedByteArray;
import rx.Observable;

/**
 * Created by phil on 3/28/15.
 */
public interface APIv2 {

    /* Team List */
    @GET("/teams/{pageNum}")
    public Observable<List<Team>> fetchTeamPageObservable(
            @Path("pageNum") int pageNum,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/teams/{pageNum}")
    public List<Team> fetchTeamPage(
            @Path("pageNum") int pageNum,
            @Header("If-Modified-Since") String ifModifiedSince
    );


    /* Fetch Team */
    @GET("/team/{teamKey}")
    public Observable<Team> fetchTeamObservable(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}")
    public Team fetchTeam(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Team Events In Year */
    @GET("/team/{teamKey}/{year}/events")
    public Observable<List<Event>> fetchTeamEventsObservable(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/{year}/events")
    public List<Event> fetchTeamEvents(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Awards for Team at Event */
    @GET("/team/{teamKey}/event/{eventKey}/awards")
    public Observable<List<Award>> fetchTeamAtEventAwardsObservable(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/event/{eventKey}/awards")
    public List<Award> fetchTeamAtEventAwards(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Matches for Team at Event */
    @GET("/team/{teamKey}/event/{eventKey}/matches")
    public Observable<List<Match>> fetchTeamAtEventMatchesObservable(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/event/{eventKey}/matches")
    public List<Match> fetchTeamAtEventMatches(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch years participated for a team */
    @GET("/team/{teamKey}/years_participated")
    public Observable<List<Integer>> fetchTeamYearsParticipatedObservable(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/years_participated")
    public List<Integer> fetchTeamYearsParticipated(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch team media in a year */
    @GET("/team/{teamKey}/{year}/media")
    public Observable<List<Media>> fetchTeamMediaInYearObservable(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/{year}/media")
    public List<Media> fetchTeamMediaInYear(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Team History */
    @GET("/team/{teamKey}/history/events")
    public Observable<List<Event>> fetchTeamEventHistoryObservable(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/history/events")
    public List<Event> fetchTeamEventHistory(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/history/awards")
    public Observable<List<Award>> fetchTeamEventAwardsObservable(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/team/{teamKey}/history/awards")
    public List<Award> fetchTeamAwardHistory(
            @Path("teamKey") String teamKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event List */
    @GET("/events/{year}")
    public Observable<List<Event>> fetchEventsInYearObservable(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/events/{year}")
    public List<Event> fetchEventsInYear(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event */
    @GET("/event/{eventKey}")
    public Observable<Event> fetchEventObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}")
    public Event fetchEvent(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );


    /* Fetch Event Teams */
    @GET("/event/{eventKey}/teams")
    public Observable<List<Team>> fetchEventTeamsObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/teams")
    public List<Team> fetchEventTeams(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event Rankings */
    @GET("/event/{eventKey}/rankings")
    public Observable<JsonArray> fetchEventRankingsObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/rankings")
    public JsonArray fetchEventRankings(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event Matches */
    @GET("/event/{eventKey}/matches")
    public Observable<List<Match>> fetchEventMatchesObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/matches")
    public List<Match> fetchEventMatches(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event Stats */
    @GET("/event/{eventKey}/stats")
    public Observable<JsonObject> fetchEventStatsObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/stats")
    public JsonObject fetchEventStats(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event Awards */
    @GET("/event/{eventKey}/awards")
    public Observable<List<Award>> fetchEventAwardsObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/awards")
    public List<Award> fetchEventAwards(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Event District Points */
    @GET("/event/{eventKey}/district_points")
    public Observable<JsonObject> fetchEventDistrictPointsObservable(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/event/{eventKey}/district_points")
    public JsonObject fetchEventDistrictPoints(
            @Path("eventKey") String eventKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Districts In Year */
    @GET("/districts/{year}")
    public Observable<List<District>> fetchDistrictListObservable(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/districts/{year}")
    public List<District> fetchDistrictList(
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Events in District */
    @GET("/district/{districtShort}/{year}/events")
    public Observable<List<Event>> fetchDistrictEventsObservable(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/district/{districtShort}/{year}/events")
    public List<Event> fetchDistrictEvents(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* District Rankings */
    @GET("/district/{districtShort}/{year}/rankings")
    public Observable<JsonArray> fetchDistrictRankingsObservable(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/district/{districtShort}/{year}/rankings")
    public JsonArray fetchDistrictRankings(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    /* Fetch Match */
    @GET("/match/{matchKey}")
    public Observable<Match> fetchMatchObservable(
            @Path("matchKey") String matchKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );
    @GET("/match/{matchKey}")
    public Match fetchMatch(
            @Path("matchKey") String matchKey,
            @Header("If-Modified-Since") String ifModifiedSince
    );

    static class APIv2RequestInterceptor implements RequestInterceptor {

        /**
         * ONLY SET THIS VARIABLE FROM WITHIN TESTS
         * It's so we can send a different App Id
         */
        public static boolean isFromJunit = false;

        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-TBA-App-Id", Constants.getApiHeader()+(isFromJunit?"-junit":""));
        }
    }

    static class APIv2ErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            System.out.println(cause);
            Response response = cause.getResponse();
            if(response != null) {
                TypedByteArray data = (TypedByteArray) (response.getBody());
                byte[] bytes = data.getBytes();
                System.out.println(new String(bytes));
            }
            return cause;
        }
    }
}
