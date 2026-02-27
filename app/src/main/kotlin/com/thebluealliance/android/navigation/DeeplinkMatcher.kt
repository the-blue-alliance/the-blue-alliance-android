package com.thebluealliance.android.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey

class DeeplinkMatcher {
    fun match(uri: Uri): NavKey? {
        val segments = uri.pathSegments
        if (segments.isEmpty()) return null

        // https://www.thebluealliance.com/event/{eventKey}
        if (segments[0] == "event" && segments.size >= 2) {
            return Screen.EventDetail(segments[1])
        }

        // https://www.thebluealliance.com/team/{teamKey}
        if (segments[0] == "team" && segments.size >= 2) {
            return Screen.TeamDetail(segments[1])
        }

        // https://www.thebluealliance.com/match/{matchKey}
        if (segments[0] == "match" && segments.size >= 2) {
            return Screen.MatchDetail(segments[1])
        }

        // https://www.thebluealliance.com/events or /events/{year}
        if (segments[0] == "events") {
            return Screen.Events(year = segments.getOrNull(1)?.toIntOrNull())
        }

        // https://www.thebluealliance.com/teams or /teams/{page}
        if (segments[0] == "teams") {
            return Screen.Teams
        }

        return null
    }
}
