package com.thebluealliance.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.navigation.DeeplinkMatcher
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.TBAApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var deviceRegistrationManager: DeviceRegistrationManager

    private val deepLinkHandler = DeeplinkMatcher()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("MainActivity", "Notification permission granted: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val initalScreen = getNotificationDestination()
                ?: getDeeplinkDestination()
                ?: Screen.Events
            TBAApp(initalScreen = initalScreen)
        }

        // Register device if already signed in
        lifecycleScope.launch { deviceRegistrationManager.registerIfNeeded() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    private fun getNotificationDestination(): NavKey? {
        val matchKey = intent.getStringExtra(NotificationBuilder.EXTRA_MATCH_KEY)
        val eventKey = intent.getStringExtra(NotificationBuilder.EXTRA_EVENT_KEY)
        val teamKey = intent.getStringExtra(NotificationBuilder.EXTRA_TEAM_KEY)

        val destination = when {
            matchKey != null -> Screen.MatchDetail(matchKey)
            eventKey != null -> Screen.EventDetail(eventKey)
            teamKey != null -> Screen.TeamDetail(teamKey)
            else -> null
        }

        return destination
    }

    private fun getDeeplinkDestination(): NavKey? {
        val data = intent.data ?: return null
        return deepLinkHandler.match(data)
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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
                requestNotificationPermission()
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
                requestNotificationPermission()
            } catch (e: Exception) {
                Log.e("MainActivity", "Emulator sign-in failed", e)
            }
        }
    }

}
