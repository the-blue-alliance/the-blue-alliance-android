package com.thebluealliance.android.tv

import com.thebluealliance.android.data.remote.dto.SponsorDto
import com.thebluealliance.android.tv.data.repository.Sponsor
import com.thebluealliance.android.tv.data.repository.SponsorLogoStyle
import com.thebluealliance.android.tv.data.repository.toDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SponsorMappingTest {
    private fun dto(
        style: String? = "wordmark",
        light: String? = "https://x/andymark-on-light.png",
        dark: String? = "https://x/andymark-on-dark.png",
    ) = SponsorDto(
        name = "AndyMark",
        url = "https://www.andymark.com/",
        logoStyle = style,
        logoUrl = light,
        logoUrlDark = dark,
    )

    @Test
    fun `prefers the on-dark logo for the dark TV theme`() {
        assertEquals(
            Sponsor("AndyMark", "https://x/andymark-on-dark.png", SponsorLogoStyle.Wordmark),
            dto().toDomain(),
        )
    }

    @Test
    fun `falls back to the light logo, then to none`() {
        assertEquals("https://x/andymark-on-light.png", dto(dark = null).toDomain().logoUrl)
        assertEquals(null, dto(light = null, dark = null).toDomain().logoUrl)
    }

    @Test
    fun `only mark is a mark, anything else draws as a wordmark`() {
        assertEquals(SponsorLogoStyle.Mark, dto(style = "mark").toDomain().logoStyle)
        assertEquals(SponsorLogoStyle.Wordmark, dto(style = null).toDomain().logoStyle)
        assertEquals(SponsorLogoStyle.Wordmark, dto(style = "banner").toDomain().logoStyle)
    }
}
