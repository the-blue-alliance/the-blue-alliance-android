package com.thebluealliance.android.data.mappers

import com.thebluealliance.android.data.local.entity.EventRankingSortOrderEntity
import com.thebluealliance.android.data.remote.dto.RankingResponseDto
import com.thebluealliance.android.data.remote.dto.RankingSortOrderDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RankingMappersTest {

    @Test
    fun `toSortOrderEntity persists both sort order and extra stats metadata`() {
        val response = RankingResponseDto(
            sortOrderInfo = listOf(
                RankingSortOrderDto(name = "Ranking Score", precision = 2),
                RankingSortOrderDto(name = "Auto", precision = 0),
            ),
            extraStatsInfo = listOf(
                RankingSortOrderDto(name = "Cargo", precision = 1),
            ),
        )

        val entity = response.toSortOrderEntity("2026test")

        assertEquals("2026test", entity.eventKey)
        assertEquals(true, entity.sortOrderInfo.contains("Ranking Score"))
        assertEquals(true, entity.extraStatsInfo?.contains("Cargo") == true)
    }

    @Test
    fun `getSortOrderInfo decodes metadata names and precision`() {
        val response = RankingResponseDto(
            sortOrderInfo = listOf(
                RankingSortOrderDto(name = "Ranking Score", precision = 2),
                RankingSortOrderDto(name = "Auto", precision = 0),
            ),
            extraStatsInfo = emptyList(),
        )
        val entity = response.toSortOrderEntity("2026test")
        assertEquals(true, entity.sortOrderInfo.startsWith("["))

        val sortOrders = entity.getSortOrderInfo()

        assertEquals(2, sortOrders.size)
        assertEquals("Ranking Score", sortOrders[0].name)
        assertEquals(2, sortOrders[0].precision)
        assertEquals("Auto", sortOrders[1].name)
        assertEquals(0, sortOrders[1].precision)
    }

    @Test
    fun `getExtraStatsInfo returns empty list for malformed json`() {
        val entity = EventRankingSortOrderEntity(
            eventKey = "2026test",
            sortOrderInfo = "[]",
            extraStatsInfo = "not-json",
        )

        val extraStats = entity.getExtraStatsInfo()

        assertEquals(0, extraStats.size)
    }
}
