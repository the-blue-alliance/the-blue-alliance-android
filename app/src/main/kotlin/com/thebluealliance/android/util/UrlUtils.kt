package com.thebluealliance.android.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
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

/**
 * Copies [text] to the clipboard under [label]. Shows a confirmation Toast on Android < 13;
 * Android 13+ surfaces its own system copy confirmation, so we skip the Toast there.
 */
fun Context.copyToClipboard(
    label: String,
    text: String,
) {
    getSystemService(ClipboardManager::class.java)
        ?.setPrimaryClip(ClipData.newPlainText(label, text))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(this, "Copied $text", Toast.LENGTH_SHORT).show()
    }
}
