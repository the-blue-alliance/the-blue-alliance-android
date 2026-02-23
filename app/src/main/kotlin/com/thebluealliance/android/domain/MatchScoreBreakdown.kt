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
val rpBonusFieldsByYear = mapOf(
    2026 to listOf("energizedAchieved", "superchargedAchieved", "traversalAchieved"),
    2025 to listOf("autoBonusAchieved", "coralBonusAchieved", "bargeBonusAchieved"),
    2024 to listOf("coopertitionBonusAchieved", "melodyBonusAchieved", "ensembleBonusAchieved"),
    2023 to listOf("activationBonusAchieved", "sustainabilityBonusAchieved"),
)

/** All RP bonus field names across all supported years. */
val rpBonusFields: Set<String> = rpBonusFieldsByYear.values.flatten().toSet()

/** Boolean fields that should display as ✓/✗ (RP bonuses + other booleans). */
val booleanDisplayFields: Set<String> = rpBonusFields + setOf("coopertitionCriteriaMet", "g206Penalty")

data class AllianceRpBonuses(
    val red: List<Boolean>,
    val blue: List<Boolean>,
)

private val breakdownJson = Json { ignoreUnknownKeys = true }

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
        val obj = breakdownJson.parseToJsonElement(breakdown) as? JsonObject ?: return null
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

/**
 * Formats a score breakdown value for display in the match detail screen.
 */
fun formatBreakdownValue(apiKey: String, value: String): String {
    if (value == "-") return value
    return when {
        apiKey == "rp" -> "+$value RP"
        apiKey in rpBonusFields -> if (value == "true") "✓ (+1 RP)" else "✗"
        apiKey in booleanDisplayFields -> if (value == "true") "✓" else "✗"
        else -> value
    }
}
