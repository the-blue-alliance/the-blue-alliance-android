package com.thebluealliance.android.messaging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class NotificationCollapseTest {
    private fun matchData(
        team: String,
        match: String = "2026cmptx_qm61",
    ) = mapOf(
        "notification_type" to "match_score",
        "event_key" to "2026cmptx",
        "match_key" to match,
        "team_key" to team,
    )

    @Test
    fun `same match, different followed team collapses to one id`() {
        assertEquals(
            NotificationBuilder.collapseId(matchData("frc254")),
            NotificationBuilder.collapseId(matchData("frc1323")),
        )
    }

    @Test
    fun `different matches stay separate`() {
        assertNotEquals(
            NotificationBuilder.collapseId(matchData("frc254", "2026cmptx_qm61")),
            NotificationBuilder.collapseId(matchData("frc254", "2026cmptx_qm62")),
        )
    }

    @Test
    fun `different notification types for the same match stay separate`() {
        val score = matchData("frc254") + ("notification_type" to "match_score")
        val upcoming = matchData("frc254") + ("notification_type" to "upcoming_match")
        assertNotEquals(
            NotificationBuilder.collapseId(score),
            NotificationBuilder.collapseId(upcoming),
        )
    }

    @Test
    fun `non-match notifications keep a per-message id (not collapsed across teams)`() {
        val a =
            mapOf(
                "notification_type" to "alliance_selection",
                "event_key" to "2026cmptx",
                "team_key" to "frc254",
            )
        val b =
            mapOf(
                "notification_type" to "alliance_selection",
                "event_key" to "2026cmptx",
                "team_key" to "frc1323",
            )
        assertNotEquals(NotificationBuilder.collapseId(a), NotificationBuilder.collapseId(b))
    }
}
