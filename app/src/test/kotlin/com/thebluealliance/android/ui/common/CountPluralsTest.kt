package com.thebluealliance.android.ui.common

import com.thebluealliance.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CountPluralsTest {
    private val strings = ResourceQuantityStrings

    @Test
    fun `one point abbreviates singular`() {
        assertEquals("1 pt", strings.get(R.plurals.points_short, 1))
    }

    @Test
    fun `zero and two points abbreviate plural`() {
        assertEquals("0 pts", strings.get(R.plurals.points_short, 0))
        assertEquals("2 pts", strings.get(R.plurals.points_short, 2))
    }

    @Test
    fun `every plural has distinct one and other items`() {
        R.plurals::class.java.fields.forEach { field ->
            val items = ResourceQuantityStrings.itemsOf(field.name)
            assertTrue("one" in items && "other" in items, field.name)
            assertNotEquals(items["one"], items["other"], field.name)
        }
    }

    @Test
    fun `counted plurals format the count`() {
        R.plurals::class.java.fields.filter { it.name != "sponsor_credit" }.forEach { field ->
            val id = field.getInt(null)
            assertEquals("1", strings.get(id, 1).filter(Char::isDigit), field.name)
            assertEquals("2", strings.get(id, 2).filter(Char::isDigit), field.name)
        }
    }
}
