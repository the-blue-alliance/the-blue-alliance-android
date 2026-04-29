package com.thebluealliance.android.navigation

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeeplinkMatcherTest {
    private val matcher = DeeplinkMatcher()

    @Test
    fun `event pitmap deeplink maps to pit map screen`() {
        val uri = mockk<Uri>()
        every { uri.pathSegments } returns listOf("event", "2026miket", "pitmap")
        every { uri.getQueryParameter("teams") } returns "frc254,frc1678"

        val result =
            matcher.match(uri)

        assertTrue(result is Screen.EventPitMap)
        val pitMap = result as Screen.EventPitMap
        assertEquals("2026miket", pitMap.eventKey)
        assertEquals("frc254,frc1678", pitMap.teamsCsv)
    }

    @Test
    fun `event detail deeplink remains unchanged`() {
        val uri = mockk<Uri>()
        every { uri.pathSegments } returns listOf("event", "2026miket")

        val result = matcher.match(uri)

        assertEquals(Screen.EventDetail("2026miket"), result)
    }
}

