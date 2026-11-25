package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchDetailPaneTest {
    @Test
    fun `list pane is 40 percent held to 360-400dp`() {
        // 840dp window less the 80dp rail.
        assertEquals(360.dp, matchListPaneWidth(760.dp))
        assertEquals(377.6.dp.value, matchListPaneWidth(944.dp).value, 0.01f)
        assertEquals(400.dp, matchListPaneWidth(1200.dp))
    }

    // Window width less the 80dp navigation rail.
    @Test
    fun `at the expanded breakpoint both panes get a phone's width`() {
        val available = 840.dp - 80.dp
        assertEquals(MatchPaneMinWidth, matchListPaneWidth(available))
        assertEquals(399.dp, matchDetailPaneWidth(available))
        assertTrue(matchDetailPaneWidth(available) >= MatchPaneMinWidth)
    }

    @Test
    fun `a portrait tablet is too narrow for two panes`() {
        assertTrue(matchDetailPaneWidth(800.dp - 80.dp) < MatchPaneMinWidth)
    }

    @Test
    fun `a landscape tablet gives the match most of the room`() {
        assertEquals(799.dp, matchDetailPaneWidth(1280.dp - 80.dp))
    }

    private fun match(key: String) =
        Match(
            key = key,
            eventKey = "2025mifer",
            compLevel = CompLevel.QUAL,
            matchNumber = 1,
            setNumber = 1,
            time = null,
            predictedTime = null,
            actualTime = null,
            redTeamKeys = emptyList(),
            redScore = -1,
            blueTeamKeys = emptyList(),
            blueScore = -1,
            winningAlliance = null,
        )

    @Test
    fun `item index counts each group's header`() {
        val groups = listOf(listOf(match("q1"), match("q2")), listOf(match("sf1")))
        assertEquals(1, matchListItemIndex(groups, "q1"))
        assertEquals(2, matchListItemIndex(groups, "q2"))
        assertEquals(4, matchListItemIndex(groups, "sf1"))
        assertEquals(6, matchListItemIndex(groups, "sf1", headerOffset = 2))
        assertNull(matchListItemIndex(groups, "f1"))
    }
}
