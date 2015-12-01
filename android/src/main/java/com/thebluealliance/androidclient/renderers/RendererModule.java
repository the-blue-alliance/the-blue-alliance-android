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
    public MyTbaModelRenderer provideMyTbaModelRenderer(APICache cache, EventRenderer eventRenderer) {
        return new MyTbaModelRenderer(cache, eventRenderer);
    }

    @Provides @Singleton
    public EventRenderer provideEventRenderer(APICache cache) {
        return new EventRenderer(cache);
    }

    @Provides @Singleton
    public AwardRenderer provideAwardRenderer(APICache cache) {
        return new AwardRenderer(cache);
    }
}
