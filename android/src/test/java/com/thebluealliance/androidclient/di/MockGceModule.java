package com.thebluealliance.androidclient.di;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Model;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.Tbamobile;
import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.gce.GceAuthController;
import com.thebluealliance.androidclient.datafeed.gce.TbaSuggestionController;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module()
public class MockGceModule {

    @Provides
    @Singleton
    TbaSuggestionController provideTbaSuggestionController() {
        return Mockito.mock(TbaSuggestionController.class);
    }

    @Provides
    GceAuthController provideGceAuthController() {
        return Mockito.mock(GceAuthController.class);
    }

    @Provides @Singleton
    Tbamobile provideTbaMobileApi() {
        return Mockito.mock(Tbamobile.class);
    }

    @Provides @Singleton
    Favorites provideTbaMobileFavoritesApi() {
        return Mockito.mock(Favorites.class);
    }

    @Provides @Singleton
    Subscriptions provideTbaMobileSubscriptionsApi() {
        return Mockito.mock(Subscriptions.class);
    }

    @Provides @Singleton
    Model provideTbaMobileModelPrefsApi() {
        return Mockito.mock(Model.class);
    }

    @Provides @Singleton TeamMedia provideTeamMediaApi() {
        return Mockito.mock(TeamMedia.class);
    }

    @Provides @Singleton
    MyTbaDatafeed provideMyTbaDatafeed() {
        return Mockito.mock(MyTbaDatafeed.class);
    }
}
