package com.thebluealliance.android.navigation

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/** A [Screen.RegionalAdvancement] for [year], built by name so this compiles before the field exists. */
internal fun regionalAdvancement(year: Int? = null): Screen =
    Json.decodeFromString(
        serializer(Screen.RegionalAdvancement::class.java),
        if (year == null) "{}" else """{"year":$year}""",
    ) as Screen
