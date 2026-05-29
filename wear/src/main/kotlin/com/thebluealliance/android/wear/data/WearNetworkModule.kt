package com.thebluealliance.android.wear.data

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thebluealliance.android.wear.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WearNetworkModule {
    private const val HTTP_CACHE_SIZE_BYTES = 5L * 1024 * 1024

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        apiKeyProvider: ApiKeyProvider,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .cache(Cache(context.cacheDir.resolve("http_cache"), HTTP_CACHE_SIZE_BYTES))
            .addInterceptor(
                Interceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("X-TBA-Auth-Key", apiKeyProvider.apiKey)
                            .build()
                    chain.proceed(request)
                },
            ).addInterceptor(
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
    fun provideWearTbaApi(
        client: OkHttpClient,
        json: Json,
    ): WearTbaApi =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.TBA_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WearTbaApi::class.java)
}
