package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.RegionalRanking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionalAdvancementRepository @Inject constructor(
    private val api: TbaApi,
) {
    suspend fun getRegionalRankings(year: Int): List<RegionalRanking> {
        val rankings = api.getRegionalAdvancementRankings(year).orEmpty()
        val advancements = api.getRegionalAdvancement(year).orEmpty()

        return rankings.map { ranking ->
            val advancement = advancements[ranking.teamKey]
            ranking.toDomain(year, advancement?.cmpStatus)
        }.sortedBy { it.rank }
    }
}

