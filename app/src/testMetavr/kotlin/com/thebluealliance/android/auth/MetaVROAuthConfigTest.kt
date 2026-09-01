package com.thebluealliance.android.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MetaVROAuthConfigTest {
    @Test
    fun `a build without a client id is not configured`() {
        assertFalse(MetaVROAuthConfig.isConfigured(""))
        assertFalse(MetaVROAuthConfig.isConfigured("   "))
        assertTrue(MetaVROAuthConfig.isConfigured("123-abc.apps.googleusercontent.com"))
    }

    @Test
    fun `redirect uri is the reversed client id`() {
        assertEquals(
            "com.googleusercontent.apps.123-abc:/oauth2redirect",
            MetaVROAuthConfig.redirectUri("123-abc.apps.googleusercontent.com"),
        )
    }

    @Test
    fun `redirect scheme matches the one build gradle puts in the manifest`() {
        // app/build.gradle.kts derives the AppAuth manifest placeholder the same way.
        assertEquals(
            "com.googleusercontent.apps.123-abc",
            MetaVROAuthConfig.redirectScheme("123-abc.apps.googleusercontent.com"),
        )
    }
}
