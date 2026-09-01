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
 */
class MetaVRSignInLauncher(
    private val firebaseAuth: FirebaseAuth,
) : SignInLauncher {
    override fun signIn(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    ) {
        val clientId = BuildConfig.OAUTH_CLIENT_ID
        if (!MetaVROAuthConfig.isConfigured(clientId)) {
            Log.w(TAG, "No OAuth client id configured; sign-in is unavailable")
            activity.toast(R.string.sign_in_unavailable)
            return
        }

        val service = AuthorizationService(activity)
        var launcher: ActivityResultLauncher<Intent>? = null
        launcher =
            activity.activityResultRegistry.register(
                RESULT_KEY,
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                launcher?.unregister()
                onAuthorizationResult(activity, service, result.data, onSignedIn)
            }

        try {
            launcher.launch(service.getAuthorizationRequestIntent(authorizationRequest(clientId)))
        } catch (e: ActivityNotFoundException) {
            // Horizon OS ships Quest Browser, but a stripped image (e.g. the simulator) may
            // have no browser at all.
            Log.e(TAG, "No browser available for sign-in", e)
            activity.toast(R.string.sign_in_failed)
            launcher.unregister()
            service.dispose()
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

    private fun onAuthorizationResult(
        activity: ComponentActivity,
        service: AuthorizationService,
        data: Intent?,
        onSignedIn: () -> Unit,
    ) {
        val response = data?.let { AuthorizationResponse.fromIntent(it) }
        if (response == null) {
            // Backing out of the browser comes back as no response and no error.
            val error = data?.let { AuthorizationException.fromIntent(it) }
            if (error == null) {
                Log.i(TAG, "Sign-in cancelled")
            } else {
                Log.e(TAG, "Authorization failed", error)
                activity.toast(R.string.sign_in_failed)
            }
            service.dispose()
            return
        }

        service.performTokenRequest(response.createTokenExchangeRequest()) { tokens, tokenError ->
            service.dispose()
            val idToken = tokens?.idToken
            if (idToken == null) {
                Log.e(TAG, "Token exchange returned no ID token", tokenError)
                activity.toast(R.string.sign_in_failed)
            } else {
                signInToFirebase(activity, idToken, onSignedIn)
            }
        }
    }

    private fun signInToFirebase(
        activity: ComponentActivity,
        idToken: String,
        onSignedIn: () -> Unit,
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity.lifecycleScope.launch {
            try {
                firebaseAuth.signInWithCredential(credential).await()
                onSignedIn()
            } catch (e: Exception) {
                Log.e(TAG, "Firebase sign-in failed", e)
                activity.toast(R.string.sign_in_failed)
            }
        }
    }

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
