package com.thebluealliance.android.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.core.network.ApiKeyProvider
import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.data.remote.AuthTokenInterceptor
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.GitHubApi
import com.thebluealliance.android.data.remote.TbaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HTTP_CACHE_SIZE_BYTES = 20L * 1024 * 1024

    // Canonical JSON for every client in this module (TBA read, authenticated ClientApi,
    // GitHub) so they cannot drift — see TbaClientFactory.json for the flag rationale.
    @Provides
    @Singleton
    fun provideJson(): Json = TbaClientFactory.json

    @Provides
    @Singleton
    fun provideApiKeyProvider(
        prefs: SharedPreferences,
        remoteConfig: FirebaseRemoteConfig,
    ): ApiKeyProvider =
        ApiKeyProvider(
            isDebug = BuildConfig.DEBUG,
            bakedApiKey = BuildConfig.TBA_API_KEY,
            prefs = prefs,
            remoteConfig = remoteConfig,
        )

    // TBA APIv3 read client — built by the shared factory (canonical JSON, X-TBA-Auth-Key
    // header, debug-only logging, canonical timeouts).
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
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        TbaClientFactory.retrofit(BuildConfig.TBA_BASE_URL, client)

    @Provides
    @Singleton
    fun provideTbaApi(retrofit: Retrofit): TbaApi = retrofit.create(TbaApi::class.java)

    // The authenticated myTBA client uses a user auth token (not the read API key) and no
    // cache, so it stays bespoke — but shares the canonical JSON.
    @Provides
    @Singleton
    @Named("authenticated")
    fun provideAuthOkHttpClient(authTokenInterceptor: AuthTokenInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authTokenInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).build()

    // GitHub (contributors) is a different host with no TBA auth, so it stays bespoke — but
    // shares the canonical JSON.
    @Provides
    @Singleton
    fun provideGitHubApi(
        @ApplicationContext context: Context,
        json: Json,
    ): GitHubApi =
        Retrofit
            .Builder()
            .baseUrl("https://api.github.com/")
            .client(
                OkHttpClient
                    .Builder()
                    .cache(
                        Cache(context.cacheDir.resolve("github_http_cache"), HTTP_CACHE_SIZE_BYTES),
                    ).addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level =
                                if (BuildConfig.DEBUG) {
                                    HttpLoggingInterceptor.Level.BASIC
                                } else {
                                    HttpLoggingInterceptor.Level.NONE
                                }
                        },
                    ).build(),
            ).addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GitHubApi::class.java)

    @Provides
    @Singleton
    fun provideClientApi(
        @Named("authenticated") client: OkHttpClient,
        json: Json,
    ): ClientApi =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.TBA_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ClientApi::class.java)
}
