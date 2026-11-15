package com.thebluealliance.android.ui.more

import com.thebluealliance.android.domain.model.PlatinumSponsor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SponsorCreditsTest {
    private fun sponsor(
        onLight: Any? = "light.png",
        onDark: Any? = "dark.png",
    ) = PlatinumSponsor(
        name = "AndyMark",
        url = "https://www.andymark.com/",
        logoOnLight = onLight,
        logoOnDark = onDark,
    )

    @Test
    fun labelIsSingularForOneSponsor() {
        assertEquals("Thanks to our platinum sponsor", sponsorCreditLabel(1))
    }

    @Test
    fun labelIsPluralForSeveralSponsors() {
        assertEquals("Thanks to our platinum sponsors", sponsorCreditLabel(3))
    }

    @Test
    fun logoForPicksTheVariantForTheBackground() {
        assertEquals("dark.png", sponsor().logoFor(darkBackground = true))
        assertEquals("light.png", sponsor().logoFor(darkBackground = false))
    }

    @Test
    fun logoForFallsBackToTheOtherVariant() {
        assertEquals("light.png", sponsor(onDark = null).logoFor(darkBackground = true))
        assertEquals("dark.png", sponsor(onLight = null).logoFor(darkBackground = false))
    }

    @Test
    fun logoForIsNullWithoutAnyLogo() {
        assertNull(sponsor(onLight = null, onDark = null).logoFor(darkBackground = true))
    }
}
