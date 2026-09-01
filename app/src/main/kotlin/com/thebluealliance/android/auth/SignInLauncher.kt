package com.thebluealliance.android.auth

import androidx.activity.ComponentActivity

/**
 * Starts the interactive sign-in flow, which is distribution-specific: the `gms` flavor
 * uses Credential Manager, while Horizon OS ships no Google Mobile Services and needs a
 * browser-based flow.
 *
 * Only the front door varies. Everything behind the credential — `FirebaseAuth`,
 * `AuthRepository`, `AuthTokenInterceptor` — is shared.
 */
interface SignInLauncher {
    /**
     * Launches sign-in from [activity], invoking [onSignedIn] once Firebase holds the
     * credential. Failures are logged, not surfaced.
     */
    fun signIn(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    )
}
