package com.thebluealliance.android.tv.data.repository

import com.thebluealliance.android.tv.data.api.GitHubApi

/** A person we want to thank, reduced to what the 10-foot Thanks list shows. */
data class Contributor(
    val login: String,
    val contributions: Int,
)

interface ContributorRepository {
    /** Contributors to thank, most prolific first. Throws on network/parse failure. */
    suspend fun getContributors(): List<Contributor>
}

class NetworkContributorRepository(
    private val api: GitHubApi,
    private val owner: String = "the-blue-alliance",
    // This TV app has no public repo yet, so we thank the people who built the sibling
    // the-blue-alliance-android phone app this one descends from.
    private val repo: String = "the-blue-alliance-android",
) : ContributorRepository {
    override suspend fun getContributors(): List<Contributor> =
        api
            .getContributors(owner, repo)
            .filter { it.type != "Bot" }
            .map { Contributor(it.login, it.contributions) }
            .sortedByDescending { it.contributions }
}
