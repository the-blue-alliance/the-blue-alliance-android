package com.thebluealliance.android.domain.model

private const val WINNER_LABEL = "Winner 🏆"

data class Alliance(
    val eventKey: String,
    val number: Int,
    val name: String?,
    val picks: List<String>,
    val declines: List<String>,
    val backupIn: String?,
    val backupOut: String?,
    val playoffStatus: String? = null,
    val playoffLevel: String? = null,
    val playoffDoubleElimRound: String? = null,
)

val Alliance.displayTitle: String
    get() {
        return this.name?.takeIf { it.isNotBlank() } ?: "Alliance ${this.number}"
    }

val Alliance.displayTitleShort: String
    get() {
        if (this.name.isNullOrBlank()
            // Regular playoff matches also have the property name = "Alliance #".
            // We want "A#" instead to be concise.
            || this.name.startsWith("Alliance ")) return "A${this.number}"

        return this.name
    }

val Alliance.playoffSummary: String?
    get() {
        val statusLabel = playoffStatus?.toAllianceStatusLabel()
        if (statusLabel == WINNER_LABEL) return statusLabel

        val roundLabel = playoffDoubleElimRound?.takeIf { it.isNotBlank() }
        val levelLabel = playoffLevel?.toAllianceCompLevelLabel()

        return when {
            statusLabel != null && roundLabel != null -> "$statusLabel in $roundLabel"
            statusLabel != null && levelLabel != null -> "$statusLabel in the $levelLabel"
            statusLabel != null -> statusLabel
            roundLabel != null -> "In $roundLabel"
            levelLabel != null -> "In the $levelLabel"
            else -> null
        }
    }

private fun String.toAllianceStatusLabel(): String =
    trim()
        .takeIf { it.isNotEmpty() }
        ?.lowercase()
        ?.replace('_', ' ')
        ?.let { status ->
            if (status == "won") WINNER_LABEL
            else status.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
        .orEmpty()

private fun String.toAllianceCompLevelLabel(): String = when (lowercase()) {
    CompLevel.QUAL.code -> "Qualifications"
    CompLevel.OCTOFINAL.code -> "Eighths"
    CompLevel.QUARTERFINAL.code -> "Quarterfinals"
    CompLevel.SEMIFINAL.code -> "Semifinals"
    CompLevel.FINAL.code -> "Finals"
    else -> trim().takeIf { it.isNotEmpty() }?.uppercase().orEmpty()
}

