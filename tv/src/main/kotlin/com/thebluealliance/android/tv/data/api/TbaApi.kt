package com.thebluealliance.android.tv.data.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thebluealliance.android.data.remote.dto.EventDto
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/** Shared lenient JSON, reused by both the network and bundled-asset paths. */
val TbaJson: Json =
    Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

interface TbaApi {
    @GET("api/v3/events/{year}")
    suspend fun getEvents(
        @Path("year") year: Int,
    ): List<EventDto>
}

object TbaApiFactory {
    fun create(
        context: Context,
        baseUrl: String,
        apiKey: () -> String,
    ): TbaApi {
        val client =
            OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .callTimeout(20, TimeUnit.SECONDS)
                .cache(Cache(context.cacheDir.resolve("tba_http"), 10L * 1024 * 1024))
                .addInterceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("X-TBA-Auth-Key", apiKey())
                            .addHeader("Accept", "application/json")
                            .build()
                    chain.proceed(request)
                }.addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    },
                ).build()

        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(TbaJson.asConverterFactory(contentType))
            .build()
            .create(TbaApi::class.java)
    }
}
