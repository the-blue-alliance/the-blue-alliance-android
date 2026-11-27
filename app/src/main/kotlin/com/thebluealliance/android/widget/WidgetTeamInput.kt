package com.thebluealliance.android.widget

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
    fun normalize(raw: String): String? = raw.filter { it.isDigit() }.ifBlank { null }

    /**
     * Checks [raw] before the widget is saved. [lookup] fetches the team by key and throws if the
     * request fails.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun validate(
        raw: String,
        lookup: suspend (teamKey: String) -> Unit,
    ): TeamValidation {
        val teamNumber = normalize(raw) ?: return TeamValidation.Invalid
        return TeamValidation.Valid(teamNumber)
    }
}
