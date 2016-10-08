package com.thebluealliance.androidclient.di;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.api.ApiV2Constants;
import com.thebluealliance.androidclient.api.rx.TbaApiV2;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
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

import static org.mockito.Mockito.when;

@Module(includes = MockTbaAndroidModule.class)
public class MockDatafeedModule {
    @Provides @Singleton @Named("tba_retrofit")
    public Retrofit provideTBARetrofit(
            Gson gson,
            OkHttpClient okHttpClient,
            SharedPreferences prefs) {
        return new Retrofit.Builder()
                .baseUrl(ApiV2Constants.TBA_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton @Named("tba_api")
    public TbaApiV2 provideRxTBAAPI(@Named("tba_retrofit") Retrofit retrofit) {
        return Mockito.mock(TbaApiV2.class);
    }

    @Provides @Singleton
    public com.thebluealliance.androidclient.api.call.TbaApiV2 provideTBAAPI(@Named("tba_retrofit") Retrofit retrofit) {
        return Mockito.mock(com.thebluealliance.androidclient.api.call.TbaApiV2.class);
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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
    public CacheableDatafeed provideDatafeed(APICache cache) {
        CacheableDatafeed df = Mockito.mock(CacheableDatafeed.class);
        when(df.getCache()).thenReturn(cache);
        return df;
    }

    @Provides @Singleton
    public RefreshController provideRefreshController() {
        return Mockito.mock(RefreshController.class);
    }

    @Provides @Singleton
    public TBAStatusController provideTbaStatusController(SharedPreferences prefs, Gson gson,
                                                          Cache cache, Context context) {
        return Mockito.mock(TBAStatusController.class);
    }
}
