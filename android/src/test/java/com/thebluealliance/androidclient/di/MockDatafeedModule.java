package com.thebluealliance.androidclient.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.api.ApiConstants;
import com.thebluealliance.androidclient.api.rx.TbaApiV2;
import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.retrofit.FirebaseAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.GitHubAPI;
import com.thebluealliance.androidclient.datafeed.retrofit.LenientGsonConverterFactory;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.fragments.FirebaseTickerFragment;

import org.mockito.Mockito;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import static org.mockito.Mockito.when;

@Module
@TestInstallIn(components = SingletonComponent.class, replaces = DatafeedModule.class)
public class MockDatafeedModule {

    @Provides @Singleton @Named("tba_retrofit")
    public Retrofit provideTBARetrofit(
            Gson gson,
            OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ApiConstants.TBA_URL)
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
    public TbaApiV2 provideRxTBAAPI() {
        return Mockito.mock(TbaApiV2.class);
    }

    @Provides @Singleton @Named("tba_apiv3_rx")
    public TbaApiV3 provideRxApiv3() {
        return Mockito.mock(TbaApiV3.class);
    }

    @Provides @Singleton @Named("tba_apiv3_call")
    public com.thebluealliance.androidclient.api.call.TbaApiV3 provideCallApiV3() {
        return Mockito.mock(com.thebluealliance.androidclient.api.call.TbaApiV3.class);
    }

    @Provides @Singleton
    public com.thebluealliance.androidclient.api.call.TbaApiV2 provideTBAAPI() {
        return Mockito.mock(com.thebluealliance.androidclient.api.call.TbaApiV2.class);
    }

    @Provides @Singleton @Named("github_api")
    public GitHubAPI provideGitHubAPI() {
        return Mockito.mock(GitHubAPI.class);
    }

    @Provides @Singleton @Named("firebase_retrofit")
    public Retrofit provideFirebaseRetrofit(Gson gson, OkHttpClient okHttpClient) {
        String firebaseUrl = FirebaseTickerFragment.FIREBASE_URL_DEFAULT;
        return new Retrofit.Builder()
                .baseUrl(firebaseUrl)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton @Named("firebase_api")
    public FirebaseAPI provideFirebaseAPI() {
        return Mockito.mock(FirebaseAPI.class);
    }

    @Provides @Singleton
    public APICache provideApiCache() {
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
    public TBAStatusController provideTbaStatusController() {
        return Mockito.mock(TBAStatusController.class);
    }
}
