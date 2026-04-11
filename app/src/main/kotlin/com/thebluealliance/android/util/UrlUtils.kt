package com.thebluealliance.android.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

/**
 * Opens a URL in an external app. Shows a Toast if no app can handle the intent.
 */
fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, "No app available to open this link", Toast.LENGTH_SHORT).show()
    }
}
