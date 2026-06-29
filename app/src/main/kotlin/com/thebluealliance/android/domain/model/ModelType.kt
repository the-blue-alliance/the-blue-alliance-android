package com.thebluealliance.android.domain.model

object ModelType {
    const val EVENT = 0
    const val TEAM = 1
    const val MATCH = 2

    // myTBA also models these; the old app / website can create them even though the new app
    // doesn't yet, so they must render + be removable here (#1460).
    const val EVENT_TEAM = 3 // key is "{eventKey}_{teamKey}", e.g. 2024micmp4_frc2471
    const val DISTRICT = 4
}
