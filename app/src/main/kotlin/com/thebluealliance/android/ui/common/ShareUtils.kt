package com.thebluealliance.android.ui.common

import android.content.Context
import android.content.Intent

fun Context.shareTbaUrl(title: String, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
    }
    startActivity(Intent.createChooser(intent, null))
}
