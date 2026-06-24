package com.thebluealliance.android.wear.data

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.core.network.ApiKeyProvider
import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.wear.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WearNetworkModule {
    // The watch keeps a deliberately small HTTP cache; the size stays a per-module choice.
    private const val HTTP_CACHE_SIZE_BYTES = 5L * 1024 * 1024

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun provideJson(): Json = TbaClientFactory.json

    @Provides
    @Singleton
    fun provideApiKeyProvider(
        @ApplicationContext context: Context,
        remoteConfig: FirebaseRemoteConfig,
    ): ApiKeyProvider =
        ApiKeyProvider(
            isDebug = BuildConfig.DEBUG,
            bakedApiKey = BuildConfig.TBA_API_KEY,
            // Same "api_config" prefs the wear copy always used, so the cached key survives.
            prefs = context.getSharedPreferences("api_config", Context.MODE_PRIVATE),
            remoteConfig = remoteConfig,
        )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        apiKeyProvider: ApiKeyProvider,
    ): OkHttpClient =
        TbaClientFactory.okHttpClient(
            apiKey = { apiKeyProvider.apiKey },
            cacheDir = context.cacheDir.resolve("http_cache"),
            cacheSizeBytes = HTTP_CACHE_SIZE_BYTES,
            isDebug = BuildConfig.DEBUG,
        )

    @Provides
    @Singleton
    fun provideWearTbaApi(client: OkHttpClient): WearTbaApi =
        TbaClientFactory
            .retrofit(BuildConfig.TBA_BASE_URL, client)
            .create(WearTbaApi::class.java)
}
