package com.thebluealliance.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.data.remote.AuthTokenInterceptor
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.GitHubApi
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.TbaApiKeyInterceptor
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
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HTTP_CACHE_SIZE_BYTES = 20L * 1024 * 1024

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        apiKeyInterceptor: TbaApiKeyInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .cache(Cache(context.cacheDir.resolve("http_cache"), HTTP_CACHE_SIZE_BYTES))
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.TBA_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideTbaApi(retrofit: Retrofit): TbaApi = retrofit.create(TbaApi::class.java)

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
