package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType

fun Match.getGrouping(event: Event): Any {
  if (compLevel == CompLevel.QUAL || compLevel == CompLevel.PRACTICE || compLevel == CompLevel.OTHER) {
    return compLevel
  }

  return when (event.playoffType) {
    PlayoffType.DOUBLE_ELIM_8_TEAM,
    PlayoffType.DOUBLE_ELIM_4_TEAM,
    PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getDoubleElimShortLabel(event)
    else -> getDefaultShortLabel()
  }
}

fun Match.getShortLabel(event: Event): String {
  if (compLevel == CompLevel.QUAL || compLevel == CompLevel.PRACTICE || compLevel == CompLevel.OTHER) {
    return getDefaultShortLabel()
  }

  return when (event.playoffType) {
    PlayoffType.DOUBLE_ELIM_8_TEAM,
      PlayoffType.DOUBLE_ELIM_4_TEAM,
      PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getDoubleElimShortLabel(event)
    else -> getDefaultShortLabel()
  }
}

fun Match.getFullLabel(event: Event): String {
  if (compLevel == CompLevel.QUAL || compLevel == CompLevel.PRACTICE || compLevel == CompLevel.OTHER) {
    return getDefaultFullLabel()
  }

  return when (event.playoffType) {
    PlayoffType.DOUBLE_ELIM_8_TEAM,
      PlayoffType.DOUBLE_ELIM_4_TEAM,
      PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getDoubleElimFullLabel(event)
    else -> getDefaultFullLabel()
  }
}

private sealed class DoubleEliminationRound {
  object Unknown: DoubleEliminationRound()
  object Finals: DoubleEliminationRound()
  class Round(val number: Int): DoubleEliminationRound()
}

/** Short label for list rows, e.g. "Q1", "SF2-1", "F1-1" */
private fun Match.getDefaultShortLabel(): String = when (compLevel) {
  CompLevel.QUAL -> "Q$matchNumber"
  CompLevel.QUARTERFINAL -> "QF$setNumber-$matchNumber"
  CompLevel.SEMIFINAL -> "SF$setNumber-$matchNumber"
  CompLevel.FINAL -> "F$setNumber-$matchNumber"
  else -> "${compLevel.code}$setNumber-$matchNumber"
}

/** Full label for title bars, e.g. "Qual 1", "Final 1-1" */
private fun Match.getDefaultFullLabel(): String = when (compLevel) {
  CompLevel.QUAL -> "Qual $matchNumber"
  CompLevel.QUARTERFINAL -> "QF$setNumber-$matchNumber"
  CompLevel.SEMIFINAL -> "SF$setNumber-$matchNumber"
  CompLevel.FINAL -> "Final $setNumber-$matchNumber"
  else -> "${compLevel.code}$setNumber-$matchNumber"
}

private fun Match.getDoubleElimShortLabel(event: Event): String {
  val round = when (event.playoffType) {
    PlayoffType.DOUBLE_ELIM_8_TEAM -> getDoubleElimRound()
    PlayoffType.DOUBLE_ELIM_4_TEAM -> getDoubleElim4Round()
    PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getLegacyDoubleElimRound()
    else -> return getDefaultShortLabel()
  }

  return when (round) {
    is DoubleEliminationRound.Round -> "R${round.number} - $matchNumber"
    DoubleEliminationRound.Finals -> "F-$matchNumber"
    DoubleEliminationRound.Unknown -> getDefaultShortLabel()
  }
}

private fun Match.getDoubleElimFullLabel(event: Event): String {
  val round = when (event.playoffType) {
    PlayoffType.DOUBLE_ELIM_8_TEAM -> getDoubleElimRound()
    PlayoffType.DOUBLE_ELIM_4_TEAM -> getDoubleElim4Round()
    PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM -> getLegacyDoubleElimRound()
    else -> return getDefaultShortLabel()
  }

  return when (round) {
    is DoubleEliminationRound.Round -> "Round ${round.number} - Match $matchNumber"
    DoubleEliminationRound.Finals -> "Final $matchNumber"
    DoubleEliminationRound.Unknown -> getDefaultShortLabel()
  }
}

/**
 * Get the double elimination round number for 2023+; only valid if comp level is semifinals
 */
private fun Match.getDoubleElimRound(): DoubleEliminationRound {
  if (compLevel == CompLevel.FINAL) return DoubleEliminationRound.Finals

  return when {
    setNumber <= 4 ->DoubleEliminationRound.Round( 1)
    setNumber <= 8 -> DoubleEliminationRound.Round(2)
    setNumber <= 10 -> DoubleEliminationRound.Round(3)
    setNumber <= 12 -> DoubleEliminationRound.Round(4)
    setNumber <= 13 -> DoubleEliminationRound.Round(5)
    else -> DoubleEliminationRound.Unknown
  }
}

private fun Match.getDoubleElim4Round(): DoubleEliminationRound {
  if (compLevel == CompLevel.FINAL) return DoubleEliminationRound.Finals

  return when {
    setNumber <= 2 ->DoubleEliminationRound.Round( 1)
    setNumber <= 4 -> DoubleEliminationRound.Round(2)
    setNumber <= 5 -> DoubleEliminationRound.Round(3)
    else -> DoubleEliminationRound.Unknown
  }
}

/**
 * Get the double elimination round for pre-2023 double elim format
 */
private fun Match.getLegacyDoubleElimRound(): DoubleEliminationRound {
  return when(compLevel) {
    CompLevel.OCTOFINAL -> when {
      setNumber <= 4 -> DoubleEliminationRound.Round(1)
      setNumber <= 6 -> DoubleEliminationRound.Round(2)
      else -> DoubleEliminationRound.Unknown
    }
    CompLevel.QUARTERFINAL -> when {
      setNumber <= 2 -> DoubleEliminationRound.Round(2)
      setNumber <= 4 -> DoubleEliminationRound.Round(3)
      else -> DoubleEliminationRound.Unknown
    }
    CompLevel.SEMIFINAL -> DoubleEliminationRound.Round(4)
    CompLevel.FINAL -> when {
      setNumber == 1 -> DoubleEliminationRound.Round(5)
      setNumber == 2 -> DoubleEliminationRound.Finals
      else -> DoubleEliminationRound.Unknown
    }
    else -> DoubleEliminationRound.Unknown
  }
}