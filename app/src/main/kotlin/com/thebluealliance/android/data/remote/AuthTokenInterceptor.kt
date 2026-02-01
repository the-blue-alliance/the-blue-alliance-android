package com.thebluealliance.android.data.remote

import com.thebluealliance.android.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(
    private val authRepository: dagger.Lazy<AuthRepository>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = try {
            runBlocking { authRepository.get().getIdToken() }
        } catch (_: Exception) {
            null
        }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
