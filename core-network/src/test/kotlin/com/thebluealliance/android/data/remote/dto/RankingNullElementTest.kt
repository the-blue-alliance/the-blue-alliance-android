package com.thebluealliance.android.data.remote.dto

import com.thebluealliance.android.core.network.TbaClientFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Locks the #1445 ranking null-element handling against the REAL canonical config
 * (TbaClientFactory.json). A team with incomplete ranking data can emit a null *element* inside
 * the index-aligned `sort_orders` / `extra_stats` / `sort_order_info` arrays; the element is kept
 * in place (so columns stay aligned and the UI renders that one cell blank) rather than dropped.
 */
class RankingNullElementTest {
    private val json = TbaClientFactory.json

    @Test
    fun `null element inside sort_orders or extra_stats is kept in place, not crashed`() {
        val payload =
            """{"team_key":"frc254","rank":1,"sort_orders":[2.0,null,0.5],"extra_stats":[null,3.0]}"""
        val item = json.decodeFromString(RankingItemDto.serializer(), payload)
        assertEquals(listOf(2.0, null, 0.5), item.sortOrders)
        assertEquals(listOf(null, 3.0), item.extraStats)
    }

    @Test
    fun `null element inside sort_order_info is kept in place, not crashed`() {
        val payload =
            """{"rankings":[],"sort_order_info":[{"name":"RS","precision":2},null,{"name":"Coop","precision":0}]}"""
        val resp = json.decodeFromString(RankingResponseDto.serializer(), payload)
        assertEquals(3, resp.sortOrderInfo?.size)
        assertEquals(null, resp.sortOrderInfo?.get(1))
        assertEquals("Coop", resp.sortOrderInfo?.get(2)?.name)
    }

    @Test
    fun `well-formed payload still parses unchanged`() {
        val item =
            json.decodeFromString(
                RankingItemDto.serializer(),
                """{"team_key":"frc254","rank":3,"sort_orders":[2.0,1.0],"extra_stats":[5.0]}""",
            )
        assertEquals(3, item.rank)
        assertEquals(listOf(2.0, 1.0), item.sortOrders)
        assertEquals(listOf(5.0), item.extraStats)
    }

    @Test
    fun `serialization still emits a plain array`() {
        val encoded =
            json.encodeToString(
                RankingItemDto.serializer(),
                RankingItemDto(teamKey = "frc254", rank = 1, sortOrders = listOf(2.0, 0.5)),
            )
        assertTrue(encoded.contains("\"sort_orders\":[2.0,0.5]"), "got: $encoded")
    }
}
