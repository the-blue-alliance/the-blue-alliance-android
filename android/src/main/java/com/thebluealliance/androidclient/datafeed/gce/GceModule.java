package com.thebluealliance.androidclient.datafeed.gce;

import android.content.Context;
import android.content.SharedPreferences;

import com.appspot.tbatv_prod_hrd.Favorites;
import com.appspot.tbatv_prod_hrd.Model;
import com.appspot.tbatv_prod_hrd.Subscriptions;
import com.appspot.tbatv_prod_hrd.Tbamobile;
import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.auth.AuthModule;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.firebase.FirebaseAuthProvider;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.gcm.GcmController;
import com.thebluealliance.androidclient.gcm.GcmModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Dagger Module for Google Cloud Endpoints and their related things
 */
@InstallIn(SingletonComponent.class)
@Module
public class GceModule {

    // Format with app engine project ID
    private static final String GCE_URL_FORMAT = "https://%1$s.appspot.com";

    public GceModule() {
    }

    @Provides @Singleton TbaSuggestionController provideTbaSuggestionController(
            TeamMedia teamMedia,
            GceAuthController gceAuthController) {
        return new TbaSuggestionController(teamMedia, gceAuthController);
    }

    @Provides GceAuthController provideGceAuthController(
            @Named("firebase_auth") AuthProvider firebaseAuthProvider) {
        return new GceAuthController((FirebaseAuthProvider)firebaseAuthProvider);
    }

    @Provides @Singleton @Named("gce_retrofit")
    Retrofit provideGceRetrofit(
            Gson gson,
            OkHttpClient okHttpClient,
            AppConfig appConfig) {
        String appspotId = appConfig.getString("appspot_projectId");
        if (appspotId == null || appspotId.isEmpty()) {
            // Fall back to prod
            appspotId = "tbatv-prod-hrd";
        }
        return new Retrofit.Builder()
                .baseUrl(String.format(GCE_URL_FORMAT, appspotId))
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .build();
    }

    @Provides @Singleton Tbamobile provideTbaMobileApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Tbamobile.class);
    }

    @Provides @Singleton
    Favorites provideTbaMobileFavoritesApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Favorites.class);
    }

    @Provides @Singleton
    Subscriptions provideTbaMobileSubscriptionsApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Subscriptions.class);
    }

    @Provides @Singleton
    Model provideTbaMobileModelPrefsApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(Model.class);
    }

    @Provides @Singleton TeamMedia provideTeamMediaApi(@Named("gce_retrofit") Retrofit retrofit) {
        return retrofit.create(TeamMedia.class);
    }

    @Provides @Singleton MyTbaDatafeed provideMyTbaDatafeed(
            @ApplicationContext Context context,
            GceAuthController authController,
            GcmController gcmController,
            Tbamobile tbamobile,
            Favorites favoriteApi,
            Subscriptions subscriptionApi,
            Model modelApi,
            SharedPreferences prefs,
            AccountController accountController,
            Database db) {
        return new MyTbaDatafeed(context, authController, gcmController, tbamobile, favoriteApi,
                                 subscriptionApi, modelApi, context.getResources(), prefs,
                                 accountController, db);
    }
}
