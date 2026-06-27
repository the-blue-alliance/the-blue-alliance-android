package com.thebluealliance.android.tv

import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.tv.data.api.toDomainOrNull
import kotlinx.serialization.builtins.ListSerializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Locks the bundled-sample (no-API-key) TV path to the canonical strict JSON. The demo build's
 * AssetEventRepository decodes events_fixture.json with TbaClientFactory.json, which — after the
 * RFC 0004 unification — dropped `isLenient`. This guards the checked-in fixture against ever
 * drifting into lenient-only JSON that the shipped strict parser would then reject at runtime.
 */
class FixtureDecodeTest {
    // Unit tests run with the module dir as the working dir; fall back to the repo-root-relative
    // path so the test is robust to either.
    private val fixture: File =
        listOf(
            File("src/main/assets/events_fixture.json"),
            File("tv/src/main/assets/events_fixture.json"),
        ).first { it.exists() }

    @Test
    fun `bundled events fixture decodes under the canonical strict JSON`() {
        val events =
            TbaClientFactory.json.decodeFromString(
                ListSerializer(EventDto.serializer()),
                fixture.readText(),
            )
        assertTrue(events.isNotEmpty(), "fixture should contain sample events")
        // Mirrors AssetEventRepository.getEvents (mapNotNull): the demo screen needs at least
        // one renderable event.
        assertTrue(
            events.any { it.toDomainOrNull() != null },
            "at least one bundled fixture event should map to a domain Event",
        )
    }
}
