package com.thebluealliance.androidclient.di;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.datafeed.retrofit.FirebaseAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.GitHubAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.fragments.FirebaseTickerFragment;

import org.mockito.Mockito;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.when;

@Module(includes = MockTbaAndroidModule.class)
public class MockDatafeedModule {
    @Provides @Singleton @Named("tba_retrofit")
    public Retrofit provideTBARetrofit(
            Gson gson,
            OkHttpClient okHttpClient,
            SharedPreferences prefs) {
        return new Retrofit.Builder()
                .baseUrl(APIv2.TBA_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides @Singleton @Named("github_retrofit")
    public Retrofit provideGithubRetrofit(
            Gson gson,
            OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides @Singleton @Named("tba_api")
    public APIv2 provideTBAAPI(@Named("tba_retrofit") Retrofit retrofit) {
        return Mockito.mock(APIv2.class);
    }

    @Provides @Singleton @Named("github_api")
    public GitHubAPI provideGitHubAPI(@Named("github_retrofit") Retrofit retrofit) {
        return Mockito.mock(GitHubAPI.class);
    }

    @Provides @Singleton @Named("firebase_retrofit")
    public Retrofit provideFirebaseRetrofit(Context context, Gson gson, OkHttpClient okHttpClient) {
        String firebaseUrl = FirebaseTickerFragment.FIREBASE_URL_DEFAULT;
        return new Retrofit.Builder()
                .baseUrl(firebaseUrl)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides @Singleton @Named("firebase_api")
    public FirebaseAPI provideFirebaseAPI(@Named("firebase_retrofit") Retrofit retrofit) {
        return Mockito.mock(FirebaseAPI.class);
    }

    @Provides @Singleton
    public Gson provideGson() {
        return HttpModule.getGson();
    }


    @Provides @Singleton
    public Cache provideOkCache(Context context) {
        return new Cache(context.getCacheDir(), HttpModule.CACHE_SIZE);
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp(Cache responseCache) {
        return Mockito.mock(OkHttpClient.class);
    }

    @Provides @Singleton
    public APICache provideApiCache(Database db) {
        return Mockito.mock(APICache.class);
    }

    @Provides @Singleton
    public CacheableDatafeed provideDatafeed(
            @Named("tba_api") APIv2 retrofit,
            APICache cache,
            DatabaseWriter writer,
            RetrofitResponseMap responseMap) {
        CacheableDatafeed df = Mockito.mock(CacheableDatafeed.class);
        when(df.getCache()).thenReturn(cache);
        return df;
    }

    @Provides @Singleton
    public RefreshController provideRefreshController() {
        return Mockito.mock(RefreshController.class);
    }

    @Provides @Singleton
    public MyTbaDatafeed provideMyTbaDatafeed(Context context, SharedPreferences prefs, Database db) {
        return Mockito.mock(MyTbaDatafeed.class);
    }

    @Provides @Singleton
    public TBAStatusController provideTbaStatusController(SharedPreferences prefs, Gson gson,
                                                          Cache cache, Context context) {
        return Mockito.mock(TBAStatusController.class);
    }
}
