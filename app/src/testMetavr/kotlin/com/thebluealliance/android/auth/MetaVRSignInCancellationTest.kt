package com.thebluealliance.android.auth

import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationException.AuthorizationRequestErrors
import net.openid.appauth.AuthorizationException.GeneralErrors
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MetaVRSignInCancellationTest {
    @Test
    fun `a cancelled flow is a cancellation even after a round trip through the intent`() {
        assertTrue(rebuilt(GeneralErrors.USER_CANCELED_AUTH_FLOW).isCancellation())
        assertTrue(rebuilt(GeneralErrors.PROGRAM_CANCELED_AUTH_FLOW).isCancellation())
    }

    @Test
    fun `a real failure is not a cancellation`() {
        assertFalse(rebuilt(GeneralErrors.NETWORK_ERROR).isCancellation())
        assertFalse(rebuilt(GeneralErrors.SERVER_ERROR).isCancellation())
        assertFalse(rebuilt(AuthorizationRequestErrors.ACCESS_DENIED).isCancellation())
    }

    @Test
    fun `a matching code in another error type is not a cancellation`() {
        val sameCodeDifferentType =
            AuthorizationException(
                AuthorizationException.TYPE_OAUTH_AUTHORIZATION_ERROR,
                GeneralErrors.USER_CANCELED_AUTH_FLOW.code,
                null,
                null,
                null,
                null,
            )
        assertFalse(sameCodeDifferentType.isCancellation())
    }

    /**
     * AppAuth serialises the exception into the result intent and parses a fresh instance back
     * out, so the launcher never sees the constant itself. Stand in for that trip.
     */
    private fun rebuilt(template: AuthorizationException) =
        AuthorizationException(
            template.type,
            template.code,
            template.error,
            template.errorDescription,
            null,
            null,
        )
}
