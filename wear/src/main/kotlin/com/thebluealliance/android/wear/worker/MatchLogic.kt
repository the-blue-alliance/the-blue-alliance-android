package com.thebluealliance.android.wear.worker

import com.thebluealliance.android.data.remote.dto.MatchDto
import com.thebluealliance.android.wear.tracker.Alliance
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pure match/record formatting logic for the complication worker, extracted so it can be unit
 * tested without constructing a WorkManager worker. Everything here is a pure function of its
 * arguments.
 */
internal object MatchLogic {
    /** Sort key placing quals first, then elimination rounds; unknown levels last. */
    fun compLevelOrder(compLevel: String): Int =
        when (compLevel) {
            "qm" -> 0
            "ef" -> 1
            "qf" -> 2
            "sf" -> 3
            "f" -> 4
            else -> Int.MAX_VALUE
        }

    /** Simplified short label — "Q18", "SF2-1", "F1-1". */
    fun getShortLabel(match: MatchDto): String =
        when (match.compLevel) {
            "qm" -> "Q${match.matchNumber}"
            "ef" -> "EF${match.setNumber}-${match.matchNumber}"
            "qf" -> "QF${match.setNumber}-${match.matchNumber}"
            "sf" -> "SF${match.setNumber}-${match.matchNumber}"
            "f" -> "F${match.setNumber}-${match.matchNumber}"
            else -> "${match.compLevel}${match.setNumber}-${match.matchNumber}"
        }

    /**
     * Win-loss-tie record over the team's PLAYED qualification matches, formatted "W-L-T".
     * A blank/absent winningAlliance counts as a tie; otherwise the team wins iff its alliance
     * matches the winner.
     */
    fun qualRecord(
        matches: List<MatchDto>,
        teamKey: String,
    ): String {
        var wins = 0
        var losses = 0
        var ties = 0
        for (match in matches) {
            if (match.compLevel != "qm" || (match.alliances?.red?.score ?: -1) < 0) continue
            val teamAlliance = Alliance.of(match, teamKey)
            when {
                match.winningAlliance.isNullOrBlank() -> ties++
                Alliance.fromKey(match.winningAlliance) == teamAlliance -> wins++
                else -> losses++
            }
        }
        return "$wins-$losses-$ties"
    }

    /** Extract bonus RPs (total minus win/tie RPs) from score_breakdown. */
    fun extractBonusRp(
        match: MatchDto,
        alliance: Alliance?,
    ): Int {
        if (alliance == null) return 0
        val breakdown = match.scoreBreakdown?.get(alliance.key)?.jsonObject ?: return 0
        val totalRp = breakdown["rp"]?.jsonPrimitive?.intOrNull ?: return 0
        val winRp =
            when {
                Alliance.fromKey(match.winningAlliance) == alliance -> 2
                match.winningAlliance.isNullOrBlank() -> 1
                else -> 0
            }
        return (totalRp - winRp).coerceAtLeast(0)
    }

    /**
     * Clock text for a match starting at [epochSeconds], as seen at [now]: the time alone when
     * it's today, or the weekday and time otherwise.
     */
    fun formatMatchClock(
        epochSeconds: Long,
        now: ZonedDateTime,
        use24Hour: Boolean,
        locale: Locale,
    ): String {
        val zoned = Instant.ofEpochSecond(epochSeconds).atZone(now.zone)
        val time = if (use24Hour) "H:mm" else "h:mm a"
        val pattern = if (zoned.toLocalDate() == now.toLocalDate()) time else "EEE $time"
        return zoned.format(DateTimeFormatter.ofPattern(pattern, locale))
    }
}
