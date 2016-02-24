package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.deserializers.APIStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDistrictPointsDeserializer;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.APIStatus;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that handles OkHttp and Gson
 */
@Module(includes = {TBAAndroidModule.class})
public class HttpModule {

    public static int CACHE_SIZE = 10 * 1024 * 1024;

    public HttpModule() {}

    @Provides @Singleton
    public Gson provideGson() {
        return getGson();
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp(Cache responseCache) {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new APIv2RequestInterceptor());
        if (Utilities.isDebuggable()) {
            client.networkInterceptors().add(new StethoInterceptor());
        }
        client.setCache(responseCache);
        return client;
    }

    @Provides @Singleton
    public Cache provideOkCache(Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }

    @VisibleForTesting
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Award.class, new AwardDeserializer());
        builder.registerTypeAdapter(Event.class, new EventDeserializer());
        builder.registerTypeAdapter(Match.class, new MatchDeserializer());
        builder.registerTypeAdapter(Team.class, new TeamDeserializer());
        builder.registerTypeAdapter(Media.class, new MediaDeserializer());
        builder.registerTypeAdapter(District.class, new DistrictDeserializer());
        builder.registerTypeAdapter(DistrictTeam.class, new DistrictTeamDeserializer());
        builder.registerTypeAdapter(DistrictPointBreakdown.class, new TeamDistrictPointsDeserializer());
        builder.registerTypeAdapter(APIStatus.class, new APIStatusDeserializer());
        return builder.create();
    }

}
