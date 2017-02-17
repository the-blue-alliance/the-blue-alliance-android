package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.deserializers.APIStatusDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AllianceDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingItemDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.RankingsResponseDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDistrictPointsDeserializer;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.api.model.IApiStatus;
import com.thebluealliance.api.model.IAward;
import com.thebluealliance.api.model.IEvent;
import com.thebluealliance.api.model.IEventAlliance;
import com.thebluealliance.api.model.IMatch;
import com.thebluealliance.api.model.IMedia;
import com.thebluealliance.api.model.IRankingResponseObject;
import com.thebluealliance.api.model.ITeam;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

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
    public APIv3RequestInterceptor provideApiRequestInterceptor(SharedPreferences prefs) {
        return new APIv3RequestInterceptor(prefs);
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp(Cache responseCache, APIv3RequestInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        if (Utilities.isDebuggable()) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        builder.cache(responseCache);
        return builder.build();
    }

    @Provides @Singleton
    public Cache provideOkCache(Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }

    @VisibleForTesting
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        AwardDeserializer awardDeserializer = new AwardDeserializer();
        EventDeserializer eventDeserializer = new EventDeserializer();
        MatchDeserializer matchDeserializer = new MatchDeserializer();
        TeamDeserializer teamDeserializer = new TeamDeserializer();
        MediaDeserializer mediaDeserializer = new MediaDeserializer();
        APIStatusDeserializer apiStatusDeserializer = new APIStatusDeserializer();
        RankingsResponseDeserializer rankingsResponseDeserializer = new RankingsResponseDeserializer();
        AllianceDeserializer allianceDeserializer = new AllianceDeserializer();

        builder.registerTypeAdapter(IAward.class, awardDeserializer);
        builder.registerTypeAdapter(Award.class, awardDeserializer);

        builder.registerTypeAdapter(IEvent.class, eventDeserializer);
        builder.registerTypeAdapter(Event.class, eventDeserializer);

        builder.registerTypeAdapter(IMatch.class, matchDeserializer);
        builder.registerTypeAdapter(Match.class, matchDeserializer);

        builder.registerTypeAdapter(ITeam.class, teamDeserializer);
        builder.registerTypeAdapter(Team.class, teamDeserializer);

        builder.registerTypeAdapter(IMedia.class, mediaDeserializer);
        builder.registerTypeAdapter(Media.class, mediaDeserializer);

        builder.registerTypeAdapter(IApiStatus.class, apiStatusDeserializer);
        builder.registerTypeAdapter(ApiStatus.class, apiStatusDeserializer);

        builder.registerTypeAdapter(IRankingResponseObject.class, rankingsResponseDeserializer);
        builder.registerTypeAdapter(RankingResponseObject.class, rankingsResponseDeserializer);
        builder.registerTypeAdapter(RankingItem.class, new RankingItemDeserializer());

        builder.registerTypeAdapter(IEventAlliance.class, allianceDeserializer);
        builder.registerTypeAdapter(EventAlliance.class, allianceDeserializer);

        builder.registerTypeAdapter(District.class, new DistrictDeserializer());
        builder.registerTypeAdapter(DistrictTeam.class, new DistrictTeamDeserializer());
        builder.registerTypeAdapter(DistrictPointBreakdown.class, new TeamDistrictPointsDeserializer());
        return builder.create();
    }

}
