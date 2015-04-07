package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.District;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class APIHelper {

    public static final String TBA_APIv2_SUFFIX= "/api/v2";

    private static final String TBA_HOST_PREF = "tba_host";
    private static final String tbaHostDefault = "http://www.thebluealliance.com";

    private static APIv2 tbaAPI;
    private static OkHttpClient okHttpClient;
    private static Cache apiCache;
    private static final long CACHE_SIZE = 5 * 1024 * 1024; // 5 MB

    public static enum QUERY {
        CSV_TEAMS,
        TEAM_LIST,
        TEAM,
        TEAM_YEAR,
        TEAM_EVENTS,
        TEAM_EVENT_AWARDS,
        TEAM_EVENT_MATCHES,
        TEAM_YEARS_PARTICIPATED,
        TEAM_MEDIA,
        EVENT_LIST,
        EVENT_INFO,
        EVENT_TEAMS,
        EVENT_MATCHES,
        EVENT_MATCHES_FOR_TEAM,
        EVENT_STATS,
        EVENT_RANKS,
        EVENT_AWARDS,
        EVENT_DISTRICT_POINTS,
        DISTRICT_LIST,
        DISTRICT_EVENTS,
        DISTRICT_RANKINGS
    }

    private static final HashMap<QUERY, String> API_URL;

    static {
        API_URL = new HashMap<>();
        API_URL.put(QUERY.CSV_TEAMS, "/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader());
        API_URL.put(QUERY.TEAM_LIST, "/api/v2/teams/%s");

        API_URL.put(QUERY.TEAM, "/api/v2/team/%s");
        API_URL.put(QUERY.TEAM_YEAR, "/api/v2/team/%s/%d");
        API_URL.put(QUERY.TEAM_EVENTS, "/api/v2/team/%s/%d/events");
        API_URL.put(QUERY.TEAM_EVENT_AWARDS, "/api/v2/team/%s/event/%s/awards");
        API_URL.put(QUERY.TEAM_EVENT_MATCHES, "/api/v2/team/%s/event/%s/matches");
        API_URL.put(QUERY.TEAM_YEARS_PARTICIPATED, "/api/v2/team/%s/years_participated");
        API_URL.put(QUERY.TEAM_MEDIA, "/api/v2/team/%s/%d/media");

        API_URL.put(QUERY.EVENT_INFO, "/api/v2/event/%s");
        API_URL.put(QUERY.EVENT_TEAMS, "/api/v2/event/%s/teams");
        API_URL.put(QUERY.EVENT_RANKS, "/api/v2/event/%s/rankings");
        API_URL.put(QUERY.EVENT_MATCHES, "/api/v2/event/%s/matches");
        API_URL.put(QUERY.EVENT_STATS, "/api/v2/event/%s/stats");
        API_URL.put(QUERY.EVENT_AWARDS, "/api/v2/event/%s/awards");
        API_URL.put(QUERY.EVENT_LIST, "/api/v2/events/%d");
        API_URL.put(QUERY.EVENT_DISTRICT_POINTS, "/api/v2/event/%s/district_points");

        API_URL.put(QUERY.DISTRICT_LIST, "/api/v2/districts/%d");
        API_URL.put(QUERY.DISTRICT_EVENTS, "/api/v2/district/%s/%d/events");
        API_URL.put(QUERY.DISTRICT_RANKINGS, "/api/v2/district/%s/%d/rankings");
    }

    public static APIv2 getAPI(){
        /* When context is null, we default to www.tba.com */
        return getAPI(null);
    }

    public static APIv2 getAPI(Context context){
        if(tbaAPI == null){

            if(okHttpClient == null){
                okHttpClient = new OkHttpClient();
            }

            if(context != null && apiCache == null){
                File cacheDir = context.getCacheDir();
                apiCache = new Cache(cacheDir, CACHE_SIZE);
                okHttpClient.setCache(apiCache);
            }

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getTBAAPIHost(context))
                    .setRequestInterceptor(new APIv2.APIv2RequestInterceptor())
                    .setErrorHandler(new APIv2.APIv2ErrorHandler())
                    .setClient(new OkClient(okHttpClient))
                    .build();
            tbaAPI = restAdapter.create(APIv2.class);
        }
        return tbaAPI;
    }

    public static String getTBAApiUrl(Context c, QUERY query) {
        String host = tbaHostDefault;
        if (c != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            if (prefs != null) {
                host = prefs.getString(TBA_HOST_PREF, tbaHostDefault);
                if (!Utilities.isDebuggable() || host.isEmpty()) {
                    host = tbaHostDefault;
                }
            }
        }
        return host + API_URL.get(query);
    }

    public static String getTBAAPIHost(Context c){
        String host = tbaHostDefault;
        if (c != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            if (prefs != null) {
                host = prefs.getString(TBA_HOST_PREF, tbaHostDefault);
                if (!Utilities.isDebuggable() || host.isEmpty()) {
                    host = tbaHostDefault;
                }
            }
        }
        return host;
    }

    public static ArrayList<District> getDistrictList(String json, String url, int version) {
        JsonArray data = JSONManager.getasJsonArray(json);
        return DistrictHelper.buildVersionedDistrictList(data, url, version);
    }
}
