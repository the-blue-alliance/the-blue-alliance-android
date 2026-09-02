package com.thebluealliance.android

import android.Manifest
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.data.sync.DataSyncManager
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.navigation.DeeplinkMatcher
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.TBAApp
import com.thebluealliance.android.widget.TeamTrackingWidgetOpenAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var firebaseAuth: FirebaseAuth

    @Inject lateinit var dataSyncManager: DataSyncManager

    @Inject lateinit var deviceRegistrationManager: DeviceRegistrationManager

    @Inject lateinit var themePreferences: ThemePreferences

    private val deepLinkHandler = DeeplinkMatcher()

    /**
     * Destinations from intents delivered to an already-running Activity (see [onNewIntent]).
     *
     * [onCreate] can only route the intent the Activity was launched with, so warm deep links —
     * browser links, notification taps, `am start` — need their own channel into the navigation
     * that [com.thebluealliance.android.navigation.TBANavigation] collects.
     */
    private val newIntentRoutes =
        MutableSharedFlow<NavKey>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val intentRoutes: SharedFlow<NavKey> = newIntentRoutes.asSharedFlow()

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            Log.d("MainActivity", "Notification permission granted: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        val flags = intent.flags
        val isNewTask =
            flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0 &&
                flags and Intent.FLAG_ACTIVITY_CLEAR_TASK != 0

        val startRoute = intent.destination() ?: Screen.Events()

        setContent {
            TBAApp(
                startRoute = startRoute,
                isNewTask = isNewTask,
                themePreferences = themePreferences,
            )
        }

        // Register device if already signed in
        lifecycleScope.launch { deviceRegistrationManager.registerIfNeeded() }

        // Sync missing event years and team pages for comprehensive search
        lifecycleScope.launch { dataSyncManager.syncIfNeeded() }
    }

    /** The destination this intent targets: a notification/widget extra, or a deeplink URI. */
    private fun Intent.destination(): NavKey? = notificationDestination() ?: deeplinkDestination()

    private fun Intent.notificationDestination(): NavKey? {
        val matchKey = getStringExtra(NotificationBuilder.EXTRA_MATCH_KEY)
        val eventKey = getStringExtra(NotificationBuilder.EXTRA_EVENT_KEY)
        val teamKey = getStringExtra(NotificationBuilder.EXTRA_TEAM_KEY)

        return when {
            matchKey != null -> Screen.MatchDetail(matchKey)
            teamKey != null && eventKey != null -> Screen.TeamEventDetail(teamKey, eventKey)
            eventKey != null -> Screen.EventDetail(eventKey)
            teamKey != null -> {
                val initialTab =
                    getIntExtra(
                        TeamTrackingWidgetOpenAction.EXTRA_INITIAL_TAB,
                        0,
                    )
                Screen.TeamDetail(teamKey, initialTab)
            }
            else -> null
        }
    }

    private fun Intent.deeplinkDestination(): NavKey? {
        val uri = data ?: return null
        return deepLinkHandler.match(uri)
    }

    /**
     * Routes intents that arrive while this Activity is already running.
     *
     * The Activity is `singleTop`, so a deeplink or notification tap aimed at the running app is
     * delivered here instead of through a fresh [onCreate]. Publishing the destination on
     * [intentRoutes] navigates the live back stack, rather than dropping the intent.
     */
    override fun onNewIntent(
        intent: Intent,
        caller: ComponentCaller,
    ) {
        super.onNewIntent(intent, caller)
        val destination = intent.destination()
        Log.d("MainActivity", "New intent: $intent -> $destination")
        if (destination == null) return
        newIntentRoutes.tryEmit(destination)
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun startGoogleSignIn() {
        if (BuildConfig.DEBUG) {
            signInWithEmulator()
            return
        }

        val googleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build()

        val request =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(this@MainActivity)
                val result = credentialManager.getCredential(this@MainActivity, request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(
                        googleIdToken.idToken,
                        null,
                    )
                firebaseAuth.signInWithCredential(firebaseCredential).await()
                requestNotificationPermission()
            } catch (e: NoCredentialException) {
                Log.i("MainActivity", "No Google credential available", e)
            } catch (e: Exception) {
                Log.e("MainActivity", "Sign-in failed", e)
            }
        }
    }

    private fun signInWithEmulator() {
        lifecycleScope.launch {
            try {
                // The Firebase Auth emulator accepts a JSON object as a fake Google ID token
                val fakeIdToken =
                    """{"sub":"2","email":"user@thebluealliance.com","email_verified":true}"""
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
