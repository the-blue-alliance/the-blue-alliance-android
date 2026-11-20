package com.thebluealliance.android.data.remote.dto

import com.thebluealliance.android.core.network.TbaClientFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SponsorsDtoTest {
    private val json = TbaClientFactory.json

    private fun decode(payload: String) =
        json.decodeFromString(SponsorsResponseDto.serializer(), payload)

    @Test
    fun `parses every field in display order`() {
        val response =
            decode(
                """{"sponsors":[
                  {"name":"AndyMark","url":"https://www.andymark.com/","logo_style":"wordmark",
                   "logo_url":"https://x/am-light.png","logo_url_dark":"https://x/am-dark.png"},
                  {"name":"Quillfeather Robotics","url":"https://example.com/","logo_style":"mark",
                   "logo_url":"https://x/qf.png","logo_url_dark":null}
                ]}""",
            )
        assertEquals(listOf("AndyMark", "Quillfeather Robotics"), response.sponsors.map { it.name })
        assertEquals("https://www.andymark.com/", response.sponsors[0].url)
        assertEquals("wordmark", response.sponsors[0].logoStyle)
        assertEquals("https://x/am-light.png", response.sponsors[0].logoUrl)
        assertEquals("https://x/am-dark.png", response.sponsors[0].logoUrlDark)
        assertEquals("mark", response.sponsors[1].logoStyle)
        assertNull(response.sponsors[1].logoUrlDark)
    }

    @Test
    fun `missing logo fields parse as null`() {
        val sponsor =
            decode(
                """{"sponsors":[{"name":"AndyMark","url":"https://www.andymark.com/"}]}""",
            ).sponsors.single()
        assertNull(sponsor.logoStyle)
        assertNull(sponsor.logoUrl)
        assertNull(sponsor.logoUrlDark)
    }

    @Test
    fun `empty list parses`() {
        assertTrue(decode("""{"sponsors":[]}""").sponsors.isEmpty())
    }
}
