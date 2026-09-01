package com.thebluealliance.android.auth

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

/**
 * Google Sign-In through the system browser, for Horizon OS, which has no Google Play
 * services and therefore no Credential Manager. Standard authorization code + PKCE (AppAuth
 * derives the code challenge itself), asking for an OpenID Connect ID token that the shared
 * `signInWithCredential` path accepts exactly like the Credential Manager one.
 *
 * The browser is a separate app — a separate panel in the headset — so this activity can be
 * stopped or its process killed while the user signs in. Everything the result needs is
 * therefore rebuilt in [register] rather than captured at tap time.
 */
class MetaVRSignInLauncher(
    private val firebaseAuth: FirebaseAuth,
) : SignInLauncher {
    private var activity: ComponentActivity? = null
    private var onSignedIn: (() -> Unit)? = null
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var service: AuthorizationService? = null
    private var signInInProgress = false

    override fun register(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    ) {
        this.activity = activity
        this.onSignedIn = onSignedIn
        activity.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) = unbind(owner)
            },
        )
        // The lifecycle-scoped overload registers before STARTED and unregisters itself on
        // destroy, so a result the system held while this process was dead is delivered here
        // as soon as the recreated activity starts.
        launcher =
            activity.activityResultRegistry.register(
                RESULT_KEY,
                activity,
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                onAuthorizationResult(result.data)
            }
    }

    override fun signIn() {
        val activity = activity
        val launcher = launcher
        if (activity == null || launcher == null) {
            Log.e(TAG, "signIn() before register(); ignoring")
            return
        }

        val clientId = BuildConfig.OAUTH_CLIENT_ID
        if (!MetaVROAuthConfig.isConfigured(clientId)) {
            Log.w(TAG, "No OAuth client id configured; sign-in is unavailable")
            activity.toast(R.string.sign_in_unavailable)
            return
        }

        if (signInInProgress) {
            // Otherwise a second tap would leak the first AuthorizationService and reuse its
            // registry key.
            Log.i(TAG, "Sign-in is already in progress; ignoring the tap")
            return
        }

        try {
            val service = authorizationService(activity)
            launcher.launch(service.getAuthorizationRequestIntent(authorizationRequest(clientId)))
            signInInProgress = true
        } catch (e: ActivityNotFoundException) {
            // Horizon OS ships Quest Browser, but a stripped image (e.g. the simulator) may
            // have no browser at all, and then retrying can never help.
            Log.e(TAG, "No browser available for sign-in", e)
            activity.toast(R.string.sign_in_needs_browser)
            disposeService()
        }
    }

    private fun authorizationRequest(clientId: String): AuthorizationRequest =
        AuthorizationRequest
            .Builder(
                GOOGLE_SERVICE_CONFIG,
                clientId,
                ResponseTypeValues.CODE,
                MetaVROAuthConfig.redirectUri(clientId).toUri(),
            ).setScopes(
                AuthorizationRequest.Scope.OPENID,
                AuthorizationRequest.Scope.EMAIL,
                AuthorizationRequest.Scope.PROFILE,
            ).build()

    private fun onAuthorizationResult(data: Intent?) {
        signInInProgress = false
        val activity = activity
        if (activity == null) {
            Log.e(TAG, "Authorization result with no registered activity; dropping it")
            return
        }

        val response = data?.let { AuthorizationResponse.fromIntent(it) }
        if (response == null) {
            // Backing out of the browser comes back either as no response and no error at
            // all, or as one of AppAuth's cancellation exceptions. Both mean the user chose
            // not to sign in, so both stay quiet.
            val error = data?.let { AuthorizationException.fromIntent(it) }
            if (error == null || error.isCancellation()) {
                Log.i(TAG, "Sign-in cancelled")
            } else {
                Log.e(TAG, "Authorization failed", error)
                activity.toast(R.string.sign_in_failed)
            }
            disposeService()
            return
        }

        // After a process death the service that launched the request is gone; the token
        // exchange only needs a live one, so make it here.
        val service = authorizationService(activity)
        service.performTokenRequest(response.createTokenExchangeRequest()) { tokens, tokenError ->
            disposeService()
            val idToken = tokens?.idToken
            if (idToken == null) {
                Log.e(TAG, "Token exchange returned no ID token", tokenError)
                activity.toast(R.string.sign_in_failed)
            } else {
                signInToFirebase(activity, idToken)
            }
        }
    }

    private fun signInToFirebase(
        activity: ComponentActivity,
        idToken: String,
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity.lifecycleScope.launch {
            try {
                firebaseAuth.signInWithCredential(credential).await()
                onSignedIn?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "Firebase sign-in failed", e)
                activity.toast(R.string.sign_in_failed)
            }
        }
    }

    private fun authorizationService(activity: ComponentActivity): AuthorizationService =
        service ?: AuthorizationService(activity).also { service = it }

    /** [AuthorizationService.dispose] is idempotent, so callers needn't track who disposed. */
    private fun disposeService() {
        service?.dispose()
        service = null
    }

    private fun unbind(owner: LifecycleOwner) {
        if (activity !== owner) return
        disposeService()
        // A pending browser result is restored and redelivered to the next register(), and a
        // lost one must not wedge sign-in, so the recreated activity always starts clean.
        signInInProgress = false
        launcher = null
        onSignedIn = null
        activity = null
    }

    // Known limitation: a toast composites outside the app's panel on Horizon OS, so it can
    // land outside the user's gaze. The app has no snackbar host to route these through
    // today, and inventing MainActivity → Compose error plumbing for sign-in alone isn't
    // worth it; revisit when the Compose layer grows one.
    private fun ComponentActivity.toast(
        @StringRes message: Int,
    ) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private companion object {
        const val TAG = "SignInLauncher"
        const val RESULT_KEY = "metavr-sign-in"

        val GOOGLE_SERVICE_CONFIG =
            AuthorizationServiceConfiguration(
                "https://accounts.google.com/o/oauth2/v2/auth".toUri(),
                "https://oauth2.googleapis.com/token".toUri(),
            )
    }
}

/**
 * Whether AppAuth reported an abandoned flow rather than a failed one — nothing went wrong, so
 * the caller should stay silent instead of blaming the user's headset.
 *
 * - `USER_CANCELED_AUTH_FLOW` is the plain back-out, and is also what Horizon OS produces when
 *   the user walks away from a system passkey prompt that didn't complete.
 * - `PROGRAM_CANCELED_AUTH_FLOW` is the same event from the other side: the app or the system
 *   dropped the pending intent (this activity going away mid-flow, for instance). The user
 *   still ends up not signed in through no fault of theirs, and there is nothing to retry
 *   right now, so it gets the same quiet treatment.
 *
 * Every other general error (network, bad discovery document) and every OAuth-protocol error is
 * a real failure and must keep its loud path.
 *
 * The exception is rebuilt from JSON on its way through the result intent, so it is never the
 * same instance as the constant. [AuthorizationException.equals] compares type and code, which
 * is how the library itself matches an exception against one of its constants.
 */
internal fun AuthorizationException.isCancellation(): Boolean =
    this == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW ||
        this == AuthorizationException.GeneralErrors.PROGRAM_CANCELED_AUTH_FLOW
