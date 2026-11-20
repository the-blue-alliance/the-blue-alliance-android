package com.thebluealliance.android.data.mappers

import com.thebluealliance.android.data.remote.dto.SponsorDto
import com.thebluealliance.android.domain.model.PlatinumSponsor
import com.thebluealliance.android.domain.model.SponsorLogoStyle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SponsorMappersTest {
    private fun dto(
        style: String? = "wordmark",
        light: String? = "https://x/light.png",
        dark: String? = "https://x/dark.png",
    ) = SponsorDto(
        name = "AndyMark",
        url = "https://www.andymark.com/",
        logoStyle = style,
        logoUrl = light,
        logoUrlDark = dark,
    )

    @Test
    fun `maps light and dark logos`() {
        assertEquals(
            PlatinumSponsor(
                name = "AndyMark",
                url = "https://www.andymark.com/",
                logoOnLight = "https://x/light.png",
                logoOnDark = "https://x/dark.png",
                logoStyle = SponsorLogoStyle.Wordmark,
            ),
            dto().toDomain(),
        )
    }

    @Test
    fun `mark style maps to Mark`() {
        assertEquals(SponsorLogoStyle.Mark, dto(style = "mark").toDomain().logoStyle)
    }

    @Test
    fun `unknown or missing style falls back to Wordmark`() {
        assertEquals(SponsorLogoStyle.Wordmark, dto(style = "banner").toDomain().logoStyle)
        assertEquals(SponsorLogoStyle.Wordmark, dto(style = null).toDomain().logoStyle)
    }

    @Test
    fun `null logos stay null`() {
        val sponsor = dto(light = null, dark = null).toDomain()
        assertEquals(null, sponsor.logoOnLight)
        assertEquals(null, sponsor.logoOnDark)
    }
}
