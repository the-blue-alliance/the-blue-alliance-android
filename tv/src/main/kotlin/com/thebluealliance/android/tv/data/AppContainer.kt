package com.thebluealliance.android.tv.data

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.core.network.ApiKeyProvider
import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.tv.BuildConfig
import com.thebluealliance.android.tv.data.api.GitHubApiFactory
import com.thebluealliance.android.tv.data.api.TbaApi
import com.thebluealliance.android.tv.data.model.WebcastThumbnails
import com.thebluealliance.android.tv.data.repository.AssetEventRepository
import com.thebluealliance.android.tv.data.repository.ContributorRepository
import com.thebluealliance.android.tv.data.repository.EventRepository
import com.thebluealliance.android.tv.data.repository.NetworkContributorRepository
import com.thebluealliance.android.tv.data.repository.NetworkEventRepository
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import java.io.IOException

/** Tiny manual DI container. Picks the data source from build config. */
interface AppContainer {
    val eventRepository: EventRepository
    val contributorRepository: ContributorRepository
    val apiKeyProvider: ApiKeyProvider
    val usingMockData: Boolean

    /** Loads stream thumbnails; served to Coil as the app's singleton loader. */
    val imageLoader: ImageLoader
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext

    // tv keeps a modest HTTP cache; the size stays a per-module choice.
    private val tbaCacheSizeBytes = 10L * 1024 * 1024

    // The 10-foot UI strands the user on a loading skeleton (no retry affordance) until a
    // request resolves, so the tv read client keeps an absolute 20s ceiling — preserving the
    // callTimeout the pre-unification tv client had.
    private val tbaCallTimeoutSeconds = 20L

    override val usingMockData: Boolean = BuildConfig.USE_MOCK_DATA

    override val apiKeyProvider: ApiKeyProvider by lazy {
        ApiKeyProvider(
            isDebug = BuildConfig.DEBUG,
            bakedApiKey = BuildConfig.TBA_API_KEY,
            // Same "api_config" prefs the tv copy always used, so the cached key survives.
            prefs = appContext.getSharedPreferences("api_config", Context.MODE_PRIVATE),
            remoteConfig = FirebaseRemoteConfig.getInstance(),
        )
    }

    override val eventRepository: EventRepository by lazy {
        if (usingMockData) {
            AssetEventRepository(appContext)
        } else {
            val client =
                TbaClientFactory.okHttpClient(
                    apiKey = { apiKeyProvider.apiKey },
                    cacheDir = appContext.cacheDir.resolve("tba_http"),
                    cacheSizeBytes = tbaCacheSizeBytes,
                    isDebug = BuildConfig.DEBUG,
                    callTimeoutSeconds = tbaCallTimeoutSeconds,
                )
            NetworkEventRepository(
                TbaClientFactory
                    .retrofit(BuildConfig.TBA_BASE_URL, client)
                    .create(TbaApi::class.java),
            )
        }
    }

    // GitHub's public contributors endpoint is independent of the TBA key, so the Thanks
    // list loads live even in the bundled-sample build.
    override val contributorRepository: ContributorRepository by lazy {
        NetworkContributorRepository(GitHubApiFactory.create(appContext))
    }

    // Its own client, not the TBA one: that client stamps the TBA API key on every request, which
    // must never reach YouTube's or Twitch's CDN.
    override val imageLoader: ImageLoader by lazy {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    // Application interceptors see the response after redirects, so this is the
                    // URL the CDN finally served. Failing the load keeps the placeholder art.
                    val response = chain.proceed(chain.request())
                    if (WebcastThumbnails.isTwitchOfflinePreview(response.request.url.toString())) {
                        response.close()
                        throw IOException("Twitch channel is offline: ${chain.request().url}")
                    }
                    response
                }.build()
        ImageLoader
            .Builder(appContext)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { client })) }
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(appContext, IMAGE_MEMORY_CACHE_FRACTION)
                    .build()
            }.diskCache {
                DiskCache
                    .Builder()
                    .directory(appContext.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizeBytes(IMAGE_DISK_CACHE_BYTES)
                    .build()
            }.build()
    }

    private companion object {
        // A feed holds at most a few dozen ~640×360 stills, so a small cache covers a full scroll.
        const val IMAGE_MEMORY_CACHE_FRACTION = 0.10
        const val IMAGE_DISK_CACHE_BYTES = 32L * 1024 * 1024
    }
}
