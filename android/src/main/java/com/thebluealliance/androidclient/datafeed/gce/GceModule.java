package com.thebluealliance.androidclient.datafeed.gce;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger Module for Google Cloud Endpoints and their related things
 */
@Module
public class GceModule {

    @Provides @Singleton
    public TbaSuggestionController provideTbaSuggestionController() {
        return new TbaSuggestionController();
    }
}
