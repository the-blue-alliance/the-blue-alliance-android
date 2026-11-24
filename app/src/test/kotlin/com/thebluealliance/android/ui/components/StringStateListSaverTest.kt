package com.thebluealliance.android.ui.components

import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.toMutableStateList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringStateListSaverTest {
    private val scope = SaverScope { true }

    @Test
    fun `unsaved toggles survive a save and restore`() {
        val selected = listOf("upcoming_match", "match_score").toMutableStateList()
        selected.remove("match_score")
        selected.add("awards_posted")

        val saved = with(StringStateListSaver) { scope.save(selected) }!!
        val restored = StringStateListSaver.restore(saved)

        assertEquals(listOf("upcoming_match", "awards_posted"), restored?.toList())
    }

    @Test
    fun `an empty selection round-trips`() {
        val saved =
            with(
                StringStateListSaver,
            ) { scope.save(emptyList<String>().toMutableStateList()) }!!

        assertEquals(emptyList<String>(), StringStateListSaver.restore(saved)?.toList())
    }
}
