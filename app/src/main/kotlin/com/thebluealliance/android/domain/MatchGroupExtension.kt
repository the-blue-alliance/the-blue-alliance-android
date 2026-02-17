package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.MatchGroup
import com.thebluealliance.android.domain.model.PlayoffType

/*
 * The TBA API has some awkward representation of different playoff match types, spread out across
 * `compLevel`, `matchNumber`, and `setNumber`.
 *
 * These extensions aim to map these fields into `MatchGroup` which is intended to represent
 * the match in a grouping that is more familiar to users (e.g. "double elimination round 1").
 *
 * The logic here was largely lifted from https://github.com/the-blue-alliance/the-blue-alliance/blob/ad984d18edf671c37662d642b77ae20e6cbfeb38/src/backend/common/helpers/playoff_type_helper.py#L176
 */

private val PLAYOFF_COMP_LEVELS = setOf(
  CompLevel.OCTOFINAL,
  CompLevel.QUARTERFINAL,
  CompLevel.SEMIFINAL,
  CompLevel.FINAL,
)

/**
 * Get a match's grouping.
 *
 * This requires a playoff type to correctly interpret the comp level, which is typically fetched from an Event object.
 *
 * TODO - it would be nice to memoize this for a given match to avoid re-calculating it every time we need it.
 */
fun Match.getGroup(playoffType: PlayoffType): MatchGroup {
  // These competition levels don't have any special handling, so we can use them as-is
  return if (compLevel in PLAYOFF_COMP_LEVELS) {
    when (playoffType) {
      // Double elims of all types are poorly represented in the API model
      PlayoffType.DOUBLE_ELIM_8_TEAM -> getDoubleElimRound()
      PlayoffType.DOUBLE_ELIM_4_TEAM -> getDoubleElim4Round()
      PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getLegacyDoubleElimRound()

      // Otherwise use competition level default
      else -> MatchGroup.CompetitionLevel(compLevel)
    }
  } else {
    MatchGroup.CompetitionLevel(compLevel)
  }
}

/**
 * Get the double elimination round number for 2023+; only valid if comp level is semifinals
 */
private fun Match.getDoubleElimRound(): MatchGroup.DoubleEliminationRound {
  if (compLevel == CompLevel.FINAL) return MatchGroup.DoubleEliminationRound.Finals

  return when {
    setNumber <= 4 -> MatchGroup.DoubleEliminationRound.Round( 1)
    setNumber <= 8 -> MatchGroup.DoubleEliminationRound.Round(2)
    setNumber <= 10 -> MatchGroup.DoubleEliminationRound.Round(3)
    setNumber <= 12 -> MatchGroup.DoubleEliminationRound.Round(4)
    setNumber <= 13 -> MatchGroup.DoubleEliminationRound.Round(5)
    else -> MatchGroup.DoubleEliminationRound.Unknown
  }
}

private fun Match.getDoubleElim4Round(): MatchGroup.DoubleEliminationRound {
  if (compLevel == CompLevel.FINAL) return MatchGroup.DoubleEliminationRound.Finals

  return when {
    setNumber <= 2 ->MatchGroup.DoubleEliminationRound.Round( 1)
    setNumber <= 4 -> MatchGroup.DoubleEliminationRound.Round(2)
    setNumber <= 5 -> MatchGroup.DoubleEliminationRound.Round(3)
    else -> MatchGroup.DoubleEliminationRound.Unknown
  }
}

/**
 * Get the double elimination round for pre-2023 double elim format
 */
private fun Match.getLegacyDoubleElimRound(): MatchGroup.DoubleEliminationRound {
  return when(compLevel) {
    CompLevel.OCTOFINAL -> when {
      setNumber <= 4 -> MatchGroup.DoubleEliminationRound.Round(1)
      setNumber <= 6 -> MatchGroup.DoubleEliminationRound.Round(2)
      else -> MatchGroup.DoubleEliminationRound.Unknown
    }
    CompLevel.QUARTERFINAL -> when {
      setNumber <= 2 -> MatchGroup.DoubleEliminationRound.Round(2)
      setNumber <= 4 -> MatchGroup.DoubleEliminationRound.Round(3)
      else -> MatchGroup.DoubleEliminationRound.Unknown
    }
    CompLevel.SEMIFINAL -> MatchGroup.DoubleEliminationRound.Round(4)
    CompLevel.FINAL -> when {
      setNumber == 1 -> MatchGroup.DoubleEliminationRound.Round(5)
      setNumber == 2 -> MatchGroup.DoubleEliminationRound.Finals
      else -> MatchGroup.DoubleEliminationRound.Unknown
    }
    else -> MatchGroup.DoubleEliminationRound.Unknown
  }
}

/**
 * Get a short label for this match
 *
 * Examples: "Q1", "SF2-1", "F1-1", "R1-8"
 */
fun Match.getShortLabel(playoffType: PlayoffType): String {
  return when (val group = getGroup(playoffType)) {
    is MatchGroup.DoubleEliminationRound.Round -> "R${group.number}-$setNumber"
    MatchGroup.DoubleEliminationRound.Finals -> "F-$matchNumber"

    is MatchGroup.CompetitionLevel,
    MatchGroup.DoubleEliminationRound.Unknown -> getCompLevelShortLabel()
  }
}

/**
 * Get a full label for this match
 *
 * Examples: "Qual 1", "Final 1-1", "Match 8 (Round 1)"
 */
fun Match.getFullLabel(playoffType: PlayoffType): String {
  return when (val group = getGroup(playoffType)) {
    is MatchGroup.DoubleEliminationRound.Round -> "Match $setNumber (Round ${group.number})"
    MatchGroup.DoubleEliminationRound.Finals -> "Final $matchNumber"

    is MatchGroup.CompetitionLevel,
    MatchGroup.DoubleEliminationRound.Unknown -> getCompLevelFullLabel()
  }
}

private fun Match.getCompLevelShortLabel(): String = when (compLevel) {
  CompLevel.QUAL -> "Q$matchNumber"
  CompLevel.OCTOFINAL -> "EF$setNumber-$matchNumber"
  CompLevel.QUARTERFINAL -> "QF$setNumber-$matchNumber"
  CompLevel.SEMIFINAL -> "SF$setNumber-$matchNumber"
  CompLevel.FINAL -> "F$setNumber-$matchNumber"
  else -> "${compLevel.code}$setNumber-$matchNumber"
}

private fun Match.getCompLevelFullLabel(): String = when (compLevel) {
  CompLevel.QUAL -> "Qual $matchNumber"
  CompLevel.OCTOFINAL -> "Eights $setNumber-$matchNumber"
  CompLevel.QUARTERFINAL -> "Quarters $setNumber-$matchNumber"
  CompLevel.SEMIFINAL -> "Semis $setNumber-$matchNumber"
  CompLevel.FINAL -> "Final $setNumber-$matchNumber"
  else -> "${compLevel.code}$setNumber-$matchNumber"
}
