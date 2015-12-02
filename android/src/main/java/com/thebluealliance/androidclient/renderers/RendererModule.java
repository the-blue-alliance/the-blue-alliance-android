package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DatafeedModule.class)
public class RendererModule {

    private static EventRenderer sEventRenderer;

    @Provides @Singleton
    public MyTbaModelRenderer provideMyTbaModelRenderer(
      APICache cache,
      EventRenderer eventRenderer,
      TeamRenderer teamRenderer,
      MatchRenderer matchRenderer,
      DistrictRenderer districtRenderer) {
        return new MyTbaModelRenderer(cache, eventRenderer, teamRenderer, matchRenderer, districtRenderer);
    }

    @Provides @Singleton
    public EventRenderer provideEventRenderer(APICache cache) {
        return new EventRenderer(cache);
    }

    @Provides @Singleton
    public AwardRenderer provideAwardRenderer(APICache cache) {
        return new AwardRenderer(cache);
    }

    @Provides @Singleton
    public TeamRenderer provideTeamRenderer(APICache cache) {
        return new TeamRenderer(cache);
    }

    @Provides @Singleton
    public MatchRenderer provideMatchRenderer(APICache cache) {
        return new MatchRenderer(cache);
    }

    @Provides @Singleton
    public DistrictRenderer provideDistrictRenderer(APICache cache) {
        return new DistrictRenderer(cache);
    }
}
