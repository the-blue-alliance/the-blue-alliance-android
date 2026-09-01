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
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thebluealliance.android.auth.SignInLauncher
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.data.sync.DataSyncManager
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.messaging.PushRegistrar
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

    @Inject lateinit var dataSyncManager: DataSyncManager

    @Inject lateinit var pushRegistrar: PushRegistrar

    @Inject lateinit var themePreferences: ThemePreferences

    @Inject lateinit var signInLauncher: SignInLauncher

    private val deepLinkHandler = DeeplinkMatcher()

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

        // Before the activity is STARTED, so a sign-in that finished in another app while
        // this process was gone still comes back to us.
        signInLauncher.register(this, ::requestNotificationPermission)

        val startRoute =
            getNotificationDestination()
                ?: getDeeplinkDestination()
                ?: Screen.Events()

        setContent {
            TBAApp(
                startRoute = startRoute,
                isNewTask = isNewTask,
                themePreferences = themePreferences,
                onSignIn = ::startSignIn,
            )
        }

        // Register device if already signed in
        lifecycleScope.launch { pushRegistrar.registerIfNeeded() }

        // Sync missing event years and team pages for comprehensive search
        lifecycleScope.launch { dataSyncManager.syncIfNeeded() }
    }

    private fun getNotificationDestination(): NavKey? {
        val matchKey = intent.getStringExtra(NotificationBuilder.EXTRA_MATCH_KEY)
        val eventKey = intent.getStringExtra(NotificationBuilder.EXTRA_EVENT_KEY)
        val teamKey = intent.getStringExtra(NotificationBuilder.EXTRA_TEAM_KEY)

        val destination =
            when {
                matchKey != null -> Screen.MatchDetail(matchKey)
                teamKey != null && eventKey != null -> Screen.TeamEventDetail(teamKey, eventKey)
                eventKey != null -> Screen.EventDetail(eventKey)
                teamKey != null -> {
                    val initialTab =
                        intent.getIntExtra(
                            NotificationBuilder.EXTRA_INITIAL_TAB,
                            0,
                        )
                    Screen.TeamDetail(teamKey, initialTab)
                }
                else -> null
            }

        return destination
    }

    private fun getDeeplinkDestination(): NavKey? {
        val data = intent.data ?: return null
        return deepLinkHandler.match(data)
    }

    override fun onNewIntent(
        intent: Intent,
        caller: ComponentCaller,
    ) {
        super.onNewIntent(intent, caller)
        Log.d("MainActivity", "Received new intent: $intent")
    }

    private fun requestNotificationPermission() {
        // Nothing can push to a distribution without a push transport, so don't ask.
        if (!pushRegistrar.isPushAvailable) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startSignIn() {
        // The same flag that points Firebase Auth at the local emulator (AuthModule) decides
        // whether sign-in short-circuits to it — a real flow can't complete against it anyway.
        if (BuildConfig.AUTH_EMULATOR) {
            signInWithEmulator()
            return
        }
        signInLauncher.signIn()
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
