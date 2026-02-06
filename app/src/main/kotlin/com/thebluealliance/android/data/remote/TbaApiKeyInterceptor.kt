package com.thebluealliance.android.data.remote

import com.thebluealliance.android.config.ApiKeyProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TbaApiKeyInterceptor @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-TBA-Auth-Key", apiKeyProvider.apiKey)
            .build()
        return chain.proceed(request)
    }
}
