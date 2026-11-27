package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Year-aware RP bonus field names from the TBA API score breakdown.
 */
val rpBonusFieldsByYear =
    mapOf(
        2026 to listOf("energizedAchieved", "superchargedAchieved", "traversalAchieved"),
        2025 to listOf("autoBonusAchieved", "coralBonusAchieved", "bargeBonusAchieved"),
        // Crescendo's coopertition lowered the Melody threshold but awarded no RP.
        2024 to listOf("melodyBonusAchieved", "ensembleBonusAchieved"),
        2023 to listOf("activationBonusAchieved", "sustainabilityBonusAchieved"),
    )

/** All RP bonus field names across all supported years. */
val rpBonusFields: Set<String> = rpBonusFieldsByYear.values.flatten().toSet()

data class AllianceRpBonuses(
    val red: List<Boolean>,
    val blue: List<Boolean>,
)

/**
 * Parses RP bonus achievements from the score breakdown for played qualification matches.
 * Returns null for non-qual matches, unplayed matches, or unsupported years.
 */
fun Match.rpBonuses(): AllianceRpBonuses? {
    if (compLevel != CompLevel.QUAL || redScore < 0) return null
    val year = eventKey.substring(0, 4).toIntOrNull() ?: return null
    val fields = rpBonusFieldsByYear[year] ?: return null
    val breakdown = scoreBreakdown ?: return null
    return try {
        val obj = Json.parseToJsonElement(breakdown) as? JsonObject ?: return null

        fun allianceBonuses(alliance: String): List<Boolean> {
            val allianceObj = obj[alliance] as? JsonObject ?: return fields.map { false }
            return fields.map { field ->
                val value = allianceObj[field]
                value is JsonPrimitive && value.jsonPrimitive.content == "true"
            }
        }
        AllianceRpBonuses(
            red = allianceBonuses("red"),
            blue = allianceBonuses("blue"),
        )
    } catch (_: Exception) {
        null
    }
}

/** Shown for a breakdown value the API left out. */
const val BREAKDOWN_MISSING = NO_VALUE

/** Shown for a met boolean breakdown field. */
const val BREAKDOWN_YES = "✓"

/** Shown for a missed boolean breakdown field. */
const val BREAKDOWN_NO = "✗"

/** Shown for a met RP bonus. */
const val BREAKDOWN_RP_BONUS_MET = "$BREAKDOWN_YES (+1 RP)"

/** Prefix and suffix around the count of a ranking points row, e.g. "+2 RP". */
const val BREAKDOWN_RP_PREFIX = "+"
const val BREAKDOWN_RP_SUFFIX = " RP"

/** Sign before nonzero foul points, which count against the alliance. */
const val BREAKDOWN_FOUL_SIGN = "−"

/**
 * Formats a score breakdown value for display in the match detail screen.
 */
fun formatBreakdownValue(
    apiKey: String,
    value: String,
): String {
    if (value == BREAKDOWN_MISSING) return value
    return when {
        apiKey == "rp" -> "$BREAKDOWN_RP_PREFIX$value$BREAKDOWN_RP_SUFFIX"
        apiKey in rpBonusFields -> if (value == "true") BREAKDOWN_RP_BONUS_MET else BREAKDOWN_NO
        // 2022+ robot booleans such as autoLineRobot1 arrive as "Yes"/"No".
        value == "true" || value == "Yes" -> BREAKDOWN_YES
        value == "false" || value == "No" -> BREAKDOWN_NO
        apiKey == "foul_points" && value != "0" -> "$BREAKDOWN_FOUL_SIGN$value"
        enumValue.matches(value) -> enumValueToWords(value)
        else -> value
    }
}

/** Label for a breakdown key with no curated label, e.g. "robot1Auto" -> "Robot 1 auto". */
internal fun camelCaseToLabel(key: String): String =
    enumValueToWords(key.split('_').joinToString("") { it.replaceFirstChar(Char::uppercase) })

/** A PascalCase API enum such as "DeepCage" or "Level3"; all-caps strings like "LRL" don't match. */
private val enumValue = Regex("[A-Z][A-Za-z0-9]*[a-z][A-Za-z0-9]*")

private val enumWordBoundary =
    Regex("(?<=[a-z])(?=[A-Z0-9])|(?<=[0-9])(?=[A-Za-z])|(?<=[A-Z])(?=[A-Z][a-z])")

/** "DeepCage" -> "Deep cage", "Level3" -> "Level 3"; acronyms and lone capitals stay capitalized. */
private fun enumValueToWords(value: String): String =
    value
        .split(enumWordBoundary)
        .mapIndexed { i, word -> if (i == 0 || word.isAcronym()) word else word.lowercase() }
        .joinToString(" ")

private fun String.isAcronym(): Boolean = isNotEmpty() && all { it.isUpperCase() }
