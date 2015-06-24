package com.thebluealliance.androidclient.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.datafeed.*;
import com.thebluealliance.androidclient.datafeed.deserializers.*;
import com.thebluealliance.androidclient.models.*;
import com.thebluealliance.androidclient.modules.components.DaggerDatafeedComponent;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
  includes = {
    TBAAndroidModule.class
  }
)
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
