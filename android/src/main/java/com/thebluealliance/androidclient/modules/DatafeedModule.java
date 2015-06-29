package com.thebluealliance.androidclient.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.APIv2ErrorHandler;
import com.thebluealliance.androidclient.datafeed.APIv2RequestInterceptor;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.RetrofitConverter;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDistrictPointsDeserializer;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.modules.components.DaggerDatafeedComponent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module(includes = {TBAAndroidModule.class})
public class DatafeedModule {

    @Inject OkHttpClient mOkHttpClient;
    @Inject OkClient mOkClient;
    @Inject RestAdapter mRestAdapter;

    public DatafeedModule() {
        DaggerDatafeedComponent.builder()
                .datafeedModule(this)
                .build()
                .inject(this);
    }

    @Provides @Singleton @Named("retrofit")
    public APIv2 provideRetrofitAPI() {
        return mRestAdapter.create(APIv2.class);
    }

    @Provides @Singleton @Named("cache")
    public APICache provideAPICache() {
        return new APICache();
    }

    @Provides @Singleton
    public OkClient provideOkClient() {
        return new OkClient(mOkHttpClient);
    }

    @Provides @Singleton
    public RestAdapter provideRestAdapter() {
        return getRestAdapter(mOkClient);
    }

    @Provides @Singleton
    public Gson provideGson() {
        return getGson();
    }

    @Provides @Singleton
    public CacheableDatafeed provideDatafeed() {
        return new CacheableDatafeed();
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new APIv2RequestInterceptor());
        return client;
    }

    //  @Provides
    //  public RefreshManager provideRefreshManager(){
    //      return new RefreshManager();
    //  }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Award.class, new AwardDeserializer());
        builder.registerTypeAdapter(Event.class, new EventDeserializer());
        builder.registerTypeAdapter(Match.class, new MatchDeserializer());
        builder.registerTypeAdapter(Team.class, new TeamDeserializer());
        builder.registerTypeAdapter(Media.class, new MediaDeserializer());
        builder.registerTypeAdapter(DistrictTeam.class, new DistrictTeamDeserializer());
        builder.registerTypeAdapter(DistrictPointBreakdown.class, new TeamDistrictPointsDeserializer());
        return builder.create();
    }

    public static RestAdapter getRestAdapter(OkClient okClient) {
        return new RestAdapter.Builder()
            .setEndpoint(APIv2.TBA_APIv2_URL)
            .setConverter(new RetrofitConverter(getGson()))
            .setErrorHandler(new APIv2ErrorHandler())
            .setClient(okClient)
            .build();
    }
}
