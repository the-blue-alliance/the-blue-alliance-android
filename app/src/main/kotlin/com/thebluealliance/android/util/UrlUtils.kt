package com.thebluealliance.android.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.thebluealliance.android.ui.common.showUserMessage

/**
 * Opens a URL in an external app. Shows a message if no app can handle the intent.
 */
fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (_: ActivityNotFoundException) {
        showUserMessage("No app available to open this link")
    }
}
