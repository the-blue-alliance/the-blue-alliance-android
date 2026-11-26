package com.thebluealliance.android.ui.components

/** "7 wins, 3 losses, 0 ties", so a W-L-T record isn't read as a date or a subtraction. */
internal fun recordDescription(
    wins: Int,
    losses: Int,
    ties: Int,
): String =
    listOf(
        count(wins, "win", "wins"),
        count(losses, "loss", "losses"),
        count(ties, "tie", "ties"),
    ).joinToString(", ")

/**
 * One spoken summary of a rankings row, naming each value's column, which the row shows only in
 * the header. A null value is a missing cell.
 */
internal fun rankingRowDescription(
    rank: Int,
    teamNumber: String,
    record: String,
    sortLabels: List<String>,
    sortValues: List<String?>,
): String =
    buildList {
        add("Rank $rank")
        add("Team $teamNumber")
        add(record)
        sortLabels.zip(sortValues).forEach { (label, value) -> add("$label ${value ?: "none"}") }
    }.joinToString(", ")

private fun count(
    n: Int,
    singular: String,
    plural: String,
) = "$n ${if (n == 1) singular else plural}"
