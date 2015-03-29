package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
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
            System.out.println(new String(((TypedByteArray)cause.getResponse().getBody()).getBytes()));
            return cause;
        }
    }
}
