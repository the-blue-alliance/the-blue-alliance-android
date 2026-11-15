package com.thebluealliance.android.ui.more

import com.thebluealliance.android.BuildConfig
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/** Short version for the More tab's link row: "11.11.2-dev.86" shows as "v11.11.2". About has the full details. */
internal fun versionLabel(versionName: String = BuildConfig.VERSION_NAME): String =
    "v${versionName.substringBefore('-')}"

private val buildTimeFormatter =
    DateTimeFormatter.ofPattern("MMM d, yyyy, HH:mm 'UTC'", Locale.US).withZone(ZoneOffset.UTC)

/** Formats the ISO-8601 build timestamp as "Nov 12, 2026, 03:05 UTC"; returns it unchanged if unparseable. */
internal fun buildTimeLabel(buildTime: String = BuildConfig.BUILD_TIME): String =
    try {
        buildTimeFormatter.format(Instant.parse(buildTime))
    } catch (e: DateTimeParseException) {
        buildTime
    }
