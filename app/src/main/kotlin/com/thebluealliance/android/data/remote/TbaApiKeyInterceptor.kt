package com.thebluealliance.android.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TbaApiKeyInterceptor @Inject constructor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-TBA-Auth-Key", apiKey)
            .build()
        return chain.proceed(request)
    }
}
