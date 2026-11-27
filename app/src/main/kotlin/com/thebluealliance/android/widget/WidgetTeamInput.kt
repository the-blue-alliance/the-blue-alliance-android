package com.thebluealliance.android.widget

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/** Result of checking the team number typed into the widget's config dialog. */
internal sealed interface TeamValidation {
    /** Save the widget for this team. */
    data class Valid(
        val teamNumber: String,
    ) : TeamValidation

    /** The input can't be a team number. */
    data object Invalid : TeamValidation

    /** The API has no such team. */
    data class NotFound(
        val teamNumber: String,
    ) : TeamValidation
}

/** Turns the config dialog's input into a team number, and checks the team exists. */
internal object WidgetTeamInput {
    /** FRC team numbers are 1 to 5 digits with no leading zeros ("frc0254" doesn't exist). */
    private const val MAX_DIGITS = 5

    fun normalize(raw: String): String? =
        raw
            .filter { it.isDigit() }
            .trimStart('0')
            .takeIf { it.isNotEmpty() && it.length <= MAX_DIGITS }

    /**
     * Checks [raw] before the widget is saved. [lookup] fetches the team by key and throws if the
     * request fails.
     */
    suspend fun validate(
        raw: String,
        lookup: suspend (teamKey: String) -> Unit,
    ): TeamValidation {
        val teamNumber = normalize(raw) ?: return TeamValidation.Invalid
        return try {
            lookup("frc$teamNumber")
            TeamValidation.Valid(teamNumber)
        } catch (e: HttpException) {
            // Only a 404 proves the team doesn't exist. Other errors (offline, server trouble)
            // can't tell, so save and let the widget's own refresh report them.
            if (e.code() ==
                HTTP_NOT_FOUND
            ) {
                TeamValidation.NotFound(teamNumber)
            } else {
                TeamValidation.Valid(teamNumber)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            TeamValidation.Valid(teamNumber)
        }
    }

    private const val HTTP_NOT_FOUND = 404
}
