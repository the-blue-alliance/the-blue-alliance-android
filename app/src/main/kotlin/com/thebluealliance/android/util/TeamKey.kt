package com.thebluealliance.android.util

/** The team number portion of an FRC team key, e.g. "frc254" -> "254". */
val String.teamNumber: String
    get() = removePrefix("frc")
