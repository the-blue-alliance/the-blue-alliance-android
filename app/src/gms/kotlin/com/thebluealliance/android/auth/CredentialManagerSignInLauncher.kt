package com.thebluealliance.android.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Google Sign-In through Credential Manager, which is backed by Google Play services. */
class CredentialManagerSignInLauncher(
    private val firebaseAuth: FirebaseAuth,
) : SignInLauncher {
    override fun signIn(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    ) {
        val googleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .build()

        val request =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        activity.lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)
                val result = credentialManager.getCredential(activity, request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(
                        googleIdToken.idToken,
                        null,
                    )
                firebaseAuth.signInWithCredential(firebaseCredential).await()
                onSignedIn()
            } catch (e: NoCredentialException) {
                Log.i(TAG, "No Google credential available", e)
            } catch (e: Exception) {
                Log.e(TAG, "Sign-in failed", e)
            }
        }
    }

    private companion object {
        const val TAG = "SignInLauncher"
    }
}
