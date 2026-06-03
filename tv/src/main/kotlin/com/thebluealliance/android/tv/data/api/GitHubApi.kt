package com.thebluealliance.android.tv.data.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GitHubContributorDto(
    val login: String,
    val contributions: Int = 0,
    // "User" or "Bot" — lets us drop dependabot et al. from the Thanks list.
    @SerialName("type") val type: String = "User",
)

interface GitHubApi {
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 100,
    ): List<GitHubContributorDto>
}

object GitHubApiFactory {
    fun create(context: Context): GitHubApi {
        val client =
            OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .callTimeout(20, TimeUnit.SECONDS)
                .cache(Cache(context.cacheDir.resolve("github_http"), 5L * 1024 * 1024))
                .addInterceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("Accept", "application/vnd.github+json")
                            .addHeader("X-GitHub-Api-Version", "2022-11-28")
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
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(TbaJson.asConverterFactory(contentType))
            .build()
            .create(GitHubApi::class.java)
    }
}
