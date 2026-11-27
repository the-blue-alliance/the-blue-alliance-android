package com.thebluealliance.android.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Mirrors ENABLED_*_NOTIFICATIONS in the server's src/backend/common/consts/notification_type.py.
class NotificationTypeTest {
    private fun serverKeysFor(modelType: Int) =
        NotificationType.forModelType(modelType).map { it.serverKey }.toSet()

    @Test
    fun `team offers the server's enabled team notifications`() {
        assertEquals(
            setOf(
                "upcoming_match",
                "match_score",
                "alliance_selection",
                "awards_posted",
                "match_video",
            ),
            serverKeysFor(ModelType.TEAM),
        )
    }

    @Test
    fun `event offers the server's enabled event notifications`() {
        assertEquals(
            setOf(
                "upcoming_match",
                "match_score",
                "starting_comp_level",
                "alliance_selection",
                "awards_posted",
                "schedule_updated",
                "match_video",
            ),
            serverKeysFor(ModelType.EVENT),
        )
    }

    @Test
    fun `match offers the server's enabled match notifications`() {
        assertEquals(
            setOf("upcoming_match", "match_score", "match_video"),
            serverKeysFor(ModelType.MATCH),
        )
    }
}
