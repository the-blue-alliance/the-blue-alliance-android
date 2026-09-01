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
     * Binds the launcher to [activity], calling [onSignedIn] once Firebase holds the
     * credential.
     *
     * **Call this from `onCreate`, before the activity is STARTED.** A flow that hands off
     * to another app (the browser, on Horizon OS) can outlive the process, and AndroidX
     * only redelivers the restored result to an `ActivityResultLauncher` registered this
     * early. Passing [onSignedIn] here rather than at tap time is what lets a sign-in that
     * finished while the process was dead still run to completion.
     *
     * The binding is released when [activity] is destroyed; a recreated activity registers
     * again.
     */
    fun register(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    )

    /**
     * Launches sign-in from the registered activity. Failures are logged and surfaced to
     * the user; a second call while one flow is already pending is ignored.
     */
    fun signIn()
}
