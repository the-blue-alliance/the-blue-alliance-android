package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.datafeed.retrofit.GitHubAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module(includes = {TBAAndroidModule.class, HttpModule.class})
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

    @Provides @Singleton @Named("tba_api")
    public APIv2 provideTBAAPI(@Named("tba_retrofit") Retrofit retrofit) {
        return retrofit.create(APIv2.class);
    }

    @Provides @Singleton @Named("github_api")
    public GitHubAPI provideGitHubAPI(@Named("github_retrofit") Retrofit retrofit) {
        return retrofit.create(GitHubAPI.class);
    }


    @Provides @Singleton
    public APICache provideApiCache(Database db) {
        return new APICache(db);
    }

    @Provides @Singleton
    public CacheableDatafeed provideDatafeed(
      @Named("tba_api") APIv2 retrofit,
      APICache cache,
      DatabaseWriter writer,
      RetrofitResponseMap responseMap) {
        return new CacheableDatafeed(retrofit, cache, writer, responseMap);
    }

    @Provides @Singleton
    public RefreshController provideRefreshController() {
        return new RefreshController();
    }

    @Provides @Singleton
    public MyTbaDatafeed provideMyTbaDatafeed(Context context, SharedPreferences prefs, Database db) {
        return new MyTbaDatafeed(context, context.getResources(), prefs, db);
    }

    @Provides @Singleton
    public TBAStatusController provideTbaStatusController(SharedPreferences prefs, Gson gson,
                                                          Cache cache, Context context) {
        return new TBAStatusController(prefs, gson, cache, context);
    }

    @VisibleForTesting
    public static Retrofit getRetrofit(Gson gson, OkHttpClient okHttpClient, SharedPreferences prefs) {
        String baseUrl = Utilities.isDebuggable()
          ? prefs.getString(APIv2.DEV_TBA_PREF_KEY, APIv2.TBA_URL)
          : APIv2.TBA_URL;
        baseUrl = baseUrl.isEmpty() ? APIv2.TBA_URL : baseUrl;
        Log.d(Constants.LOG_TAG, "Using TBA Host: " + baseUrl);
        return new Retrofit.Builder()
          .baseUrl(baseUrl)
          .client(okHttpClient)
          .addConverterFactory(LenientGsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build();
    }
}
