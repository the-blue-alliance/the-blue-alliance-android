package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thebluealliance.androidclient.TBAAndroidModule;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module(
    injects = {
        CacheableDatafeed.class,
        DatafeedModule.class,
        RetrofitConverter.class
    },
    includes = {
        TBAAndroidModule.class
    }
)
public class DatafeedModule {

    @Inject OkClient mOkClient;
    @Inject RestAdapter mRestAdapter;

    public DatafeedModule() {
        ObjectGraph.create(this).inject(this);
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
        return new OkClient();
    }

    @Provides @Singleton
    public RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
            .setEndpoint(APIv2.TBA_APIv2_URL)
            .setConverter(new RetrofitConverter())
            .setRequestInterceptor(new APIv2RequestInterceptor())
            .setErrorHandler(new APIv2ErrorHandler())
            .setClient(mOkClient)
            .build();
    }

    @Provides @Singleton
    public Gson provideGson() {
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

    @Provides @Singleton
    public CacheableDatafeed provideDatafeed() {
        return new CacheableDatafeed();
    }

    //  @Provides
    //  public RefreshManager provideRefreshManager(){
    //      return new RefreshManager();
    //  }
}
