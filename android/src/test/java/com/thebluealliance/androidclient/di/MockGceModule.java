package com.thebluealliance.androidclient.di;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Model;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.Tbamobile;
import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.gce.GceAuthController;
import com.thebluealliance.androidclient.datafeed.gce.TbaSuggestionController;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.gcm.GcmController;
import com.thebluealliance.androidclient.models.Favorite;

import org.mockito.Mockito;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
