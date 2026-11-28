package com.thebluealliance.android.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TeamLabelTest {
    @Test
    fun `nickname follows the number after a middle dot`() {
        assertEquals("254 · The Cheesy Poofs", teamLabel(254, "The Cheesy Poofs", "NASA Ames"))
    }

    @Test
    fun `name stands in for a missing nickname`() {
        assertEquals("1 · The Juggernauts", teamLabel(1, null, "The Juggernauts"))
    }

    @Test
    fun `no nickname or name gives just the number`() {
        assertEquals("254", teamLabel(254, null, null))
        assertEquals("254", teamLabel(254, "", ""))
        assertEquals("254", teamLabel(254, null))
    }

    @Test
    fun `whitespace-only nickname counts as missing`() {
        assertEquals("254 · NASA Ames", teamLabel(254, "   ", "NASA Ames"))
        assertEquals("254", teamLabel(254, "   ", " "))
    }

    @Test
    fun `padding around the nickname and name is trimmed`() {
        assertEquals("254 · The Cheesy Poofs", teamLabel(254, "  The Cheesy Poofs ", null))
        assertEquals("1 · The Juggernauts", teamLabel(1, null, " The Juggernauts  "))
    }

    @Test
    fun `blank string team number leaves just the label`() {
        assertEquals("The Cheesy Poofs", teamLabel(" ", "The Cheesy Poofs"))
    }

    @Test
    fun `string team number formats the same way`() {
        assertEquals("177 · Bobcat Robotics", teamLabel("177", "Bobcat Robotics"))
    }
}
