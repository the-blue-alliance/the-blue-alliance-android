package com.thebluealliance.android.tv.data

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.tv.BuildConfig
import com.thebluealliance.android.tv.data.api.GitHubApiFactory
import com.thebluealliance.android.tv.data.api.TbaApiFactory
import com.thebluealliance.android.tv.data.repository.AssetEventRepository
import com.thebluealliance.android.tv.data.repository.ContributorRepository
import com.thebluealliance.android.tv.data.repository.EventRepository
import com.thebluealliance.android.tv.data.repository.NetworkContributorRepository
import com.thebluealliance.android.tv.data.repository.NetworkEventRepository

/** Tiny manual DI container. Picks the data source from build config. */
interface AppContainer {
    val eventRepository: EventRepository
    val contributorRepository: ContributorRepository
    val apiKeyProvider: ApiKeyProvider
    val usingMockData: Boolean
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext

    override val usingMockData: Boolean = BuildConfig.USE_MOCK_DATA

    override val apiKeyProvider: ApiKeyProvider by lazy {
        ApiKeyProvider(appContext, FirebaseRemoteConfig.getInstance())
    }

    override val eventRepository: EventRepository by lazy {
        if (usingMockData) {
            AssetEventRepository(appContext)
        } else {
            NetworkEventRepository(
                TbaApiFactory.create(
                    context = appContext,
                    baseUrl = BuildConfig.TBA_BASE_URL,
                    apiKey = { apiKeyProvider.apiKey },
                ),
            )
        }
    }

    // GitHub's public contributors endpoint is independent of the TBA key, so the Thanks
    // list loads live even in the bundled-sample build.
    override val contributorRepository: ContributorRepository by lazy {
        NetworkContributorRepository(GitHubApiFactory.create(appContext))
    }
}
