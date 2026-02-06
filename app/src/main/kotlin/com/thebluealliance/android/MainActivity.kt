package com.thebluealliance.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.ui.TBAApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var firebaseAuth: FirebaseAuth

    private var navController: NavHostController? = null

    fun setNavController(controller: NavHostController) {
        navController = controller
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TBAApp(activity = this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }

    fun startGoogleSignIn() {
        if (BuildConfig.DEBUG) {
            signInWithEmulator()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(this@MainActivity)
                val result = credentialManager.getCredential(this@MainActivity, request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken.idToken, null)
                firebaseAuth.signInWithCredential(firebaseCredential).await()
            } catch (e: Exception) {
                Log.e("MainActivity", "Sign-in failed", e)
            }
        }
    }

    private fun signInWithEmulator() {
        lifecycleScope.launch {
            try {
                // The Firebase Auth emulator accepts a JSON object as a fake Google ID token
                val fakeIdToken = """{"sub":"2","email":"user@thebluealliance.com","email_verified":true}"""
                val credential = GoogleAuthProvider.getCredential(fakeIdToken, null)
                firebaseAuth.signInWithCredential(credential).await()
                Log.d("MainActivity", "Emulator sign-in succeeded")
            } catch (e: Exception) {
                Log.e("MainActivity", "Emulator sign-in failed", e)
            }
        }
    }

}
