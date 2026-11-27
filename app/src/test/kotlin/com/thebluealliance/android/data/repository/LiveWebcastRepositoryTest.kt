package com.thebluealliance.android.data.repository

import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.domain.model.Webcast
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LiveWebcastRepositoryTest {
    private val json = TbaClientFactory.json

    // Shape written by the server's FirebasePusher.update_live_events.
    private val body =
        """
        {
          "2026mimil": {
            "key": "2026mimil",
            "name": "FIM District Milford Event",
            "short_name": "[FIM] Milford",
            "webcasts": [
              {"type": "twitch", "channel": "firstinmichigan", "status": "online", "viewer_count": 812},
              {"type": "youtube", "channel": "abc123", "date": "2026-03-14", "status": "offline"},
              {"type": "youtube", "channel": "def456", "date": "2026-03-15", "status": "unknown"}
            ]
          },
          "2026txhou": {
            "key": "2026txhou",
            "name": "FIT District Houston Event",
            "webcasts": [{"type": "youtube", "channel": "xyz789", "date": "2026-03-14", "status": "online"}]
          },
          "2026onwat": {"key": "2026onwat", "name": "No webcasts"}
        }
        """.trimIndent()

    @Test
    fun `keeps only online webcasts, by event key`() {
        assertEquals(
            mapOf(
                "2026mimil" to listOf(Webcast("twitch", "firstinmichigan", null, null)),
                "2026txhou" to listOf(Webcast("youtube", "xyz789", null, "2026-03-14")),
            ),
            parseOnlineWebcasts(json, body),
        )
    }

    @Test
    fun `nothing live parses to no webcasts`() {
        assertEquals(emptyMap<String, List<Webcast>>(), parseOnlineWebcasts(json, "null"))
    }

    @Test
    fun `fetches once per window and again after it`() =
        runTest {
            var now = 0L
            var fetches = 0
            val repository =
                LiveWebcastRepository(
                    fetchBody = {
                        fetches++
                        body
                    },
                    json = json,
                    now = { now },
                    ttlMillis = 180_000,
                )
            repository.onlineWebcasts()
            now = 179_999
            assertEquals(2, repository.onlineWebcasts().size)
            assertEquals(1, fetches)
            now = 180_000
            repository.onlineWebcasts()
            assertEquals(2, fetches)
        }

    @Test
    fun `a failed fetch reads as nothing live and waits out the window`() =
        runTest {
            var fetches = 0
            val repository =
                LiveWebcastRepository(
                    fetchBody = {
                        fetches++
                        error("offline")
                    },
                    json = json,
                    now = { 0L },
                )
            assertEquals(emptyMap<String, List<Webcast>>(), repository.onlineWebcasts())
            repository.onlineWebcasts()
            assertEquals(1, fetches)
        }
}
