package com.thebluealliance.android.ui.matches

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ParseScoreBreakdownTest {
    @Test
    fun `null values are left out`() {
        val parsed =
            parseScoreBreakdown(
                """{"red":{"tba_rpEarned":null,"autoPoints":12},"blue":{"tba_rpEarned":null,"autoPoints":6}}""",
            )!!
        assertFalse("tba_rpEarned" in parsed.getValue("red"))
        assertEquals("12", parsed.getValue("red")["autoPoints"])
        assertEquals("6", parsed.getValue("blue")["autoPoints"])
    }

    @Test
    fun `nested objects are skipped and booleans kept as text`() {
        val parsed = parseScoreBreakdown("""{"red":{"nested":{"a":1},"rotor1Auto":true}}""")!!
        assertEquals(mapOf("rotor1Auto" to "true"), parsed.getValue("red"))
    }

    @Test
    fun `invalid json gives null`() {
        assertNull(parseScoreBreakdown("not json"))
    }
}
