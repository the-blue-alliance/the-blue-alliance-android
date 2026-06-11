package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.RegionalRanking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionalAdvancementRepository
    @Inject
    constructor(
        private val api: TbaApi,
        private val eventDao: EventDao,
    ) {
        private val cmpAdvancementMutex = Mutex()
        private val cmpAdvancementByYear = mutableMapOf<Int, Map<String, CmpAdvancement>>()

        suspend fun getRegionalRankings(year: Int): List<RegionalRanking> {
            val rankings = api.getRegionalAdvancementRankings(year).orEmpty()
            val advancements = api.getRegionalAdvancement(year).orEmpty()

            return rankings
                .map { ranking ->
                    val advancement = advancements[ranking.teamKey]
                    ranking.toDomain(year, advancement?.cmpStatus)
                }.sortedBy { it.rank }
        }

        /**
         * Championship advancement status for every team in [year]. The payload covers the whole
         * season, so it is fetched at most once per year per app session and shared across
         * screens; [forceRefresh] (e.g. pull-to-refresh) bypasses the cache.
         */
        suspend fun getCmpAdvancementByTeam(
            year: Int,
            forceRefresh: Boolean = false,
        ): Map<String, CmpAdvancement> {
            if (year < FIRST_REGIONAL_ADVANCEMENT_YEAR) return emptyMap()
            return cmpAdvancementMutex.withLock {
                val cached = if (forceRefresh) null else cmpAdvancementByYear[year]
                cached
                    ?: fetchCmpAdvancementByTeam(year).also { cmpAdvancementByYear[year] = it }
            }
        }

        private suspend fun fetchCmpAdvancementByTeam(year: Int): Map<String, CmpAdvancement> {
            val advancements = api.getRegionalAdvancement(year).orEmpty()

            val eventKeys =
                advancements.values
                    .filter { it.cmpStatus == CMP_STATUS_EVENT_QUALIFIED }
                    .mapNotNull { it.qualifyingEvent }
                    .distinct()
            val eventsByKey =
                if (eventKeys.isNotEmpty()) {
                    eventDao.getByKeys(eventKeys).associateBy { it.key }
                } else {
                    emptyMap()
                }

            return advancements
                .mapNotNull { (teamKey, advancement) ->
                    if (!advancement.cmp) return@mapNotNull null
                    val cmpAdv: CmpAdvancement =
                        when (advancement.cmpStatus) {
                            CMP_STATUS_EVENT_QUALIFIED -> {
                                val eventKey = advancement.qualifyingEvent ?: ""
                                val entity = eventsByKey[eventKey]
                                val shortName =
                                    entity?.shortName?.takeIf { it.isNotBlank() }
                                        ?: entity?.name
                                        ?: eventKey.ifBlank { null }
                                CmpAdvancement.EventQualified(
                                    eventKey = eventKey,
                                    eventShortName = shortName,
                                )
                            }
                            CMP_STATUS_POOL_QUALIFIED ->
                                CmpAdvancement.PoolQualified(
                                    week = advancement.qualifyingPoolWeek ?: 0,
                                )
                            else -> CmpAdvancement.Qualified
                        }
                    teamKey to cmpAdv
                }.toMap()
        }

        companion object {
            /** Regional championship advancement points were introduced in the 2025 season. */
            const val FIRST_REGIONAL_ADVANCEMENT_YEAR = 2025

            private const val CMP_STATUS_EVENT_QUALIFIED = "EventQualified"
            private const val CMP_STATUS_POOL_QUALIFIED = "PoolQualified"
        }
    }
