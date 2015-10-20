package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DatafeedModule.class)
public class RendererModule {

    @Provides @Singleton
    public MyTbaModelRenderer provideMyTbaModelRenderer(APICache cache) {
        return new MyTbaModelRenderer(cache);
    }
}
