package com.thebluealliance.androidclient.datafeed;

import android.content.SharedPreferences;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.api.ApiConstants;
import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.retrofit.FirebaseAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.GitHubAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

@InstallIn(SingletonComponent.class)
@Module
public class DatafeedModule {


    public DatafeedModule() {}

    @Provides @Singleton @Named("tba_retrofit")
    public Retrofit provideTBARetrofit(
            Gson gson,
            OkHttpClient okHttpClient,
            SharedPreferences prefs) {
        return getRetrofit(gson, okHttpClient, prefs);
    }

    @Provides @Singleton @Named("github_retrofit")
    public Retrofit provideGithubRetrofit(
            Gson gson,
            OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton @Named("firebase_retrofit")
    public Retrofit provideFirebaseRetrofit() {
        //FIXME Read from FirebaseRemoteConfig
        /*String firebaseUrl = Utilities.readLocalProperty(context, "firebase.url", FirebaseTickerFragment.FIREBASE_URL_DEFAULT);
        return new Retrofit.Builder()
                .baseUrl(firebaseUrl)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
         */
        return null;
    }

    @Provides @Singleton @Named("tba_apiv3_rx")
    public com.thebluealliance.androidclient.api.rx.TbaApiV3 provideRxApiv3(@Named("tba_retrofit") Retrofit retrofit) {
        return retrofit.create(TbaApiV3.class);
    }

    @Provides @Singleton @Named("tba_apiv3_call")
    public com.thebluealliance.androidclient.api.call.TbaApiV3 provideCallApiv3(@Named("tba_retrofit") Retrofit retrofit) {
        return retrofit.create(com.thebluealliance.androidclient.api.call.TbaApiV3.class);
    }

    @Provides @Singleton @Named("github_api")
    public GitHubAPI provideGitHubAPI(@Named("github_retrofit") Retrofit retrofit) {
        return retrofit.create(GitHubAPI.class);
    }

    @Provides @Singleton @Named("firebase_api")
    public FirebaseAPI provideFirebaseAPI(@Named("firebase_retrofit") Retrofit retrofit) {
        return retrofit.create(FirebaseAPI.class);
    }


    @Provides @Singleton
    public APICache provideApiCache(Database db, Gson gson) {
        return new APICache(db, gson);
    }

    @Provides @Singleton
    public CacheableDatafeed provideDatafeed(
      @Named("tba_apiv3_rx") TbaApiV3 retrofit,
      APICache cache,
      DatabaseWriter writer,
      Gson gson,
      RetrofitResponseMap responseMap) {
        return new CacheableDatafeed(retrofit, cache, writer, gson, responseMap);
    }

    @Provides @Singleton
    public RefreshController provideRefreshController() {
        return new RefreshController();
    }

    @Provides @Singleton
    public TBAStatusController provideTbaStatusController(SharedPreferences prefs,
                                                          Gson gson,
                                                          Cache cache,
                                                          AccountController accountController) {
        return new TBAStatusController(prefs, gson, cache, accountController);
    }

    @VisibleForTesting
    public static Retrofit getRetrofit(Gson gson, OkHttpClient okHttpClient, SharedPreferences prefs) {
        String baseUrl = Utilities.isDebuggable()
          ? prefs.getString(ApiConstants.DEV_TBA_PREF_KEY, ApiConstants.TBA_URL)
          : ApiConstants.TBA_URL;
        baseUrl = baseUrl.isEmpty() ? ApiConstants.TBA_URL : baseUrl;
        TbaLogger.d("Using TBA Host: " + baseUrl);
        return new Retrofit.Builder()
          .baseUrl(baseUrl)
          .client(okHttpClient)
          .addConverterFactory(LenientGsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build();
    }
}
