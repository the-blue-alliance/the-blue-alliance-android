package com.thebluealliance.android.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * The one way to build a TBA APIv3 read client, shared by `:app`, `:wear` and `:tv`
 * (RFC 0004 network unification, #1393). Each module still owns its own Retrofit API
 * interface — the endpoint subsets genuinely differ — but every client is now built from an
 * identically-configured [json] + OkHttp + Retrofit here, so the three can no longer silently
 * drift in JSON leniency, auth header, logging or timeouts.
 */
object TbaClientFactory {
    /**
     * The single canonical JSON config — the union of what the three forks each set, minus
     * `isLenient`:
     *  - `ignoreUnknownKeys`: tolerate response fields the client doesn't model yet (all three).
     *  - `coerceInputValues`: a `null` / out-of-range value for a non-nullable Kotlin field
     *    *that has a default* falls back to that default instead of throwing — it does NOT
     *    rescue arbitrary nulls (a null element inside a `List`, or a non-nullable field with
     *    no default, still throws). THIS is the divergence that used to crash `:app` / `:wear`
     *    (which lacked it) but not `:tv` (which had it) — e.g. an unplayed match whose `score`
     *    is `null` now coerces to the field's `-1` default instead of crashing.
     *  - `encodeDefaults`: preserve `:app`'s request-body serialization for authenticated writes.
     *
     * `isLenient` is deliberately NOT set: TBA APIv3 returns strict JSON, and leniency would
     * only mask genuinely malformed responses.
     */
    val json: Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L

    /**
     * A read-client OkHttp instance: a disk [Cache] at [cacheDir] capped at [cacheSizeBytes]
     * (kept a per-module parameter so the watch can stay small), the `X-TBA-Auth-Key` header
     * resolved lazily per request via [apiKey], and HTTP logging only in debug builds.
     *
     * [callTimeoutSeconds] is an optional absolute ceiling on the whole call (connect + write +
     * read). Pass it for a surface that must guarantee a failure surfaces within a bound — the
     * 10-foot `:tv` UI strands the user on a loading skeleton with no retry until the request
     * either succeeds or throws, so it sets one. `:app` / `:wear` leave it null (as they always
     * have), relying on their own loading/error UI plus connect/read timeouts.
     */
    fun okHttpClient(
        apiKey: () -> String,
        cacheDir: File,
        cacheSizeBytes: Long,
        isDebug: Boolean,
        callTimeoutSeconds: Long? = null,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .cache(Cache(cacheDir, cacheSizeBytes))
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
                    level =
                        if (isDebug) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply { callTimeoutSeconds?.let { callTimeout(it, TimeUnit.SECONDS) } }
            .build()

    /** A Retrofit pointed at [baseUrl] using [client] and the canonical [json] converter. */
    fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
