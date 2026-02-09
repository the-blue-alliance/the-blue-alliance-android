package com.thebluealliance.android.data.remote

import com.thebluealliance.android.data.remote.dto.GitHubContributorDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 100,
    ): List<GitHubContributorDto>
}
