package com.thebluealliance.android.auth

import android.widget.Toast
import androidx.activity.ComponentActivity
import com.thebluealliance.android.R

/**
 * Horizon OS has no Google Mobile Services, so Credential Manager can't run. Until the
 * browser-based (AppAuth) flow lands, say so rather than silently doing nothing.
 */
class UnavailableSignInLauncher : SignInLauncher {
    override fun signIn(
        activity: ComponentActivity,
        onSignedIn: () -> Unit,
    ) {
        Toast.makeText(activity, R.string.sign_in_unavailable, Toast.LENGTH_LONG).show()
    }
}
