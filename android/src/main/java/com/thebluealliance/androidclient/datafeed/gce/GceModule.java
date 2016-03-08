package com.thebluealliance.androidclient.datafeed.gce;

import com.google.gson.Gson;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Model;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.Tbamobile;
import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;

/**
 * Dagger Module for Google Cloud Endpoints and their related things
 */
@Module(includes = HttpModule.class)
public class GceModule {

    // Format with app engine project ID
    public static final String GCE_URL_FORMAT = "https://%1$s.appspot.com";

    public GceModule() {
    }

    @Provides @Singleton
    public TbaSuggestionController provideTbaSuggestionController(
            TeamMedia teamMedia,
            GceAuthController gceAuthController) {
        return new TbaSuggestionController(teamMedia, gceAuthController);
    }

    @Provides
    public GceAuthController provideGceAuthController(
            Context context,
            SharedPreferences sharedPreferences,
            AccountManager accountManager) {
        return new GceAuthController(context, sharedPreferences, accountManager);
    }

    @Provides @Singleton @Named("gce_retrofit")
    public Retrofit provideGceRetrofit(Context context, Gson gson, OkHttpClient okHttpClient) {
        String appspotId = Utilities.readLocalProperty(context, "appspot.projectId", "tbatv-prod-hrd");
        return new Retrofit.Builder()
                .baseUrl(String.format(GCE_URL_FORMAT, appspotId))
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .build();
    }

    @Provides @Singleton
    public Tbamobile provideTbaMobileApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Tbamobile.class);
    }

    @Provides @Singleton
    public Favorites provideTbaMobileFavoritesApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Favorites.class);
    }

    @Provides @Singleton
    public Subscriptions provideTbaMobileSubscriptionsApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Subscriptions.class);
    }

    @Provides @Singleton
    public Model provideTbaMobileModelPrefsApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Model.class);
    }

    @Provides @Singleton
    public TeamMedia provideTeamMediaApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(TeamMedia.class);
    }
}
