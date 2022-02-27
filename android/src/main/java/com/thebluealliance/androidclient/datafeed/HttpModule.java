package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Dagger module that handles OkHttp and Gson
 */
@InstallIn(SingletonComponent.class)
@Module(includes = {TBAAndroidModule.class})
public class HttpModule {

    public static int CACHE_SIZE = 10 * 1024 * 1024;

    public HttpModule() {}

    @Provides @Singleton
    public APIv3RequestInterceptor provideApiRequestInterceptor(SharedPreferences prefs) {
        return new APIv3RequestInterceptor(prefs);
    }

    @Provides @Singleton
    public OkHttpClient getOkHttp(Cache responseCache, APIv3RequestInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        if (Utilities.isDebuggable()) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        builder.cache(responseCache);
        return builder.build();
    }

    @Provides @Singleton
    public Cache provideOkCache(@ApplicationContext Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }
}
